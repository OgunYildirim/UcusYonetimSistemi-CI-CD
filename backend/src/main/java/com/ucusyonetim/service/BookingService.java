package com.ucusyonetim.service;

import com.ucusyonetim.dto.BookingRequest;
import com.ucusyonetim.dto.BookingResponse;
import com.ucusyonetim.entity.*;
import com.ucusyonetim.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;
    private final BaggageRepository baggageRepository;
    private final PaymentRepository paymentRepository;
    private final FlightPricingRepository flightPricingRepository;

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // Security olmadan basit kullanıcı kontrolü - test için userId=1 kullanıyoruz
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Flight flight = flightRepository.findById(request.getFlightId())
                .orElseThrow(() -> new RuntimeException("Flight not found"));

        FlightPricing pricing = flightPricingRepository.findActiveByFlightId(flight.getId())
                .orElseThrow(() -> new RuntimeException("Flight pricing not found"));

        // Check seat availability
        int requestedSeats = request.getPassengers().size();
        if (flight.getAvailableSeats() < requestedSeats) {
            throw new RuntimeException("Not enough available seats");
        }

        // Calculate total amount
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        for (BookingRequest.PassengerInfo passenger : request.getPassengers()) {
            if ("ECONOMY".equals(passenger.getSeatClass())) {
                totalAmount = totalAmount.add(pricing.getEconomyPrice());
            } else if ("BUSINESS".equals(passenger.getSeatClass())) {
                totalAmount = totalAmount.add(pricing.getBusinessPrice());
            }

            // Calculate baggage fee
            if (passenger.getBaggageWeightKg() > pricing.getFreeBaggageKg()) {
                double excessWeight = passenger.getBaggageWeightKg() - pricing.getFreeBaggageKg();
                java.math.BigDecimal baggageFee = pricing.getBaggagePricePerKg()
                        .multiply(java.math.BigDecimal.valueOf(excessWeight));
                totalAmount = totalAmount.add(baggageFee);
            }
        }

        // Create booking
        Booking booking = new Booking();
        booking.setBookingReference(generateBookingReference());
        booking.setUser(user);
        booking.setFlight(flight);
        booking.setStatus("CONFIRMED");
        booking.setNumberOfPassengers(requestedSeats);
        booking.setTotalAmount(totalAmount.doubleValue());

        Booking savedBooking = bookingRepository.save(booking);

        // Create tickets for each passenger
        for (BookingRequest.PassengerInfo passenger : request.getPassengers()) {
            Ticket ticket = new Ticket();
            ticket.setTicketNumber(generateTicketNumber());
            ticket.setBooking(savedBooking);
            ticket.setPassengerFirstName(passenger.getFirstName());
            ticket.setPassengerLastName(passenger.getLastName());
            ticket.setPassportNumber(passenger.getPassportNumber());
            ticket.setSeatNumber(passenger.getSeatNumber());
            ticket.setSeatClass(passenger.getSeatClass());
            ticket.setStatus("ACTIVE");

            // Set seat assignment status
            if (passenger.getSeatNumber() != null && !passenger.getSeatNumber().isEmpty()) {
                ticket.setSeatAssigned(true);
                ticket.setSeatSelectionPaid(true); // User selected seat, so they paid for it
            } else {
                ticket.setSeatAssigned(false);
                ticket.setSeatSelectionPaid(false); // Waiting for auto-assignment
            }

            if ("ECONOMY".equals(passenger.getSeatClass())) {
                ticket.setTicketPrice(pricing.getEconomyPrice().doubleValue());
            } else {
                ticket.setTicketPrice(pricing.getBusinessPrice().doubleValue());
            }

            Ticket savedTicket = ticketRepository.save(ticket);

            // Create baggage if weight > 0
            if (passenger.getBaggageWeightKg() != null && passenger.getBaggageWeightKg() > 0) {
                Baggage baggage = new Baggage();
                baggage.setTicket(savedTicket);
                baggage.setWeightKg(passenger.getBaggageWeightKg());
                baggage.setBaggageType("CHECKED");
                baggage.setStatus("CHECKED_IN");
                baggage.setBaggageTag(generateBaggageTag());

                double baggageFee = 0.0;
                if (passenger.getBaggageWeightKg() > pricing.getFreeBaggageKg()) {
                    double excessWeight = passenger.getBaggageWeightKg() - pricing.getFreeBaggageKg();
                    baggageFee = pricing.getBaggagePricePerKg()
                            .multiply(java.math.BigDecimal.valueOf(excessWeight))
                            .doubleValue();
                }
                baggage.setBaggageFee(baggageFee);

                baggageRepository.save(baggage);
            }
        }

        // Create payment
        Payment payment = new Payment();
        payment.setBooking(savedBooking);
        payment.setAmount(totalAmount.doubleValue());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setStatus("COMPLETED");
        payment.setTransactionId(generateTransactionId());
        paymentRepository.save(payment);

        // Update flight available seats
        flight.setAvailableSeats(flight.getAvailableSeats() - requestedSeats);
        flightRepository.save(flight);

        return convertToResponse(savedBooking);
    }

    public List<BookingResponse> getUserBookings() {
        // Security olmadan basit kullanıcı kontrolü - test için userId=1 kullanıyoruz
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUserId(user.getId());
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public String cancelBooking(Long bookingId) {
        // Security olmadan basit kullanıcı kontrolü - test için userId=1 kullanıyoruz
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (!booking.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized to cancel this booking");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking is already cancelled");
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);

        // Update tickets
        booking.getTickets().forEach(ticket -> {
            ticket.setStatus("CANCELLED");
            ticketRepository.save(ticket);
        });

        // Restore flight seats
        Flight flight = booking.getFlight();
        flight.setAvailableSeats(flight.getAvailableSeats() + booking.getNumberOfPassengers());
        flightRepository.save(flight);

        return "Booking cancelled successfully";
    }

    private BookingResponse convertToResponse(Booking booking) {
        BookingResponse response = new BookingResponse();
        response.setId(booking.getId());
        response.setBookingReference(booking.getBookingReference());
        response.setBookingDate(booking.getBookingDate());
        response.setStatus(booking.getStatus());
        response.setNumberOfPassengers(booking.getNumberOfPassengers());
        response.setTotalPrice(booking.getTotalAmount());

        // Flight info
        Flight flight = booking.getFlight();
        BookingResponse.FlightInfo flightInfo = new BookingResponse.FlightInfo();
        flightInfo.setId(flight.getId());
        flightInfo.setFlightNumber(flight.getFlightNumber());
        flightInfo.setDepartureTime(flight.getDepartureTime());
        flightInfo.setArrivalTime(flight.getArrivalTime());

        // Departure airport
        BookingResponse.AirportInfo depAirport = new BookingResponse.AirportInfo();
        depAirport.setId(flight.getDepartureAirport().getId());
        depAirport.setCode(flight.getDepartureAirport().getCode());
        depAirport.setName(flight.getDepartureAirport().getName());
        depAirport.setCity(flight.getDepartureAirport().getCity());
        flightInfo.setDepartureAirport(depAirport);

        // Arrival airport
        BookingResponse.AirportInfo arrAirport = new BookingResponse.AirportInfo();
        arrAirport.setId(flight.getArrivalAirport().getId());
        arrAirport.setCode(flight.getArrivalAirport().getCode());
        arrAirport.setName(flight.getArrivalAirport().getName());
        arrAirport.setCity(flight.getArrivalAirport().getCity());
        flightInfo.setArrivalAirport(arrAirport);

        // Aircraft info
        if (flight.getAircraft() != null) {
            BookingResponse.AircraftInfo aircraftInfo = new BookingResponse.AircraftInfo();
            aircraftInfo.setId(flight.getAircraft().getId());
            aircraftInfo.setModel(flight.getAircraft().getModel());
            aircraftInfo.setRegistrationNumber(flight.getAircraft().getRegistrationNumber());
            flightInfo.setAircraft(aircraftInfo);
        }

        response.setFlight(flightInfo);

        // Tickets
        List<BookingResponse.TicketInfo> ticketInfos = booking.getTickets().stream()
                .map(ticket -> {
                    BookingResponse.TicketInfo ticketInfo = new BookingResponse.TicketInfo();
                    ticketInfo.setId(ticket.getId());
                    ticketInfo.setTicketNumber(ticket.getTicketNumber());
                    ticketInfo.setPassengerFirstName(ticket.getPassengerFirstName());
                    ticketInfo.setPassengerLastName(ticket.getPassengerLastName());
                    ticketInfo.setPassengerPassportNumber(ticket.getPassportNumber());
                    ticketInfo.setSeatNumber(ticket.getSeatNumber());
                    ticketInfo.setSeatClass(ticket.getSeatClass());
                    ticketInfo.setSeatAssigned(ticket.getSeatAssigned());
                    ticketInfo.setSeatSelectionPaid(ticket.getSeatSelectionPaid());
                    ticketInfo.setTicketPrice(ticket.getTicketPrice());
                    ticketInfo.setStatus(ticket.getStatus());
                    return ticketInfo;
                })
                .collect(Collectors.toList());
        response.setTickets(ticketInfos);

        // Payment
        if (!booking.getPayments().isEmpty()) {
            Payment payment = booking.getPayments().iterator().next();
            BookingResponse.PaymentInfo paymentInfo = new BookingResponse.PaymentInfo();
            paymentInfo.setId(payment.getId());
            paymentInfo.setAmount(payment.getAmount());
            paymentInfo.setPaymentMethod(payment.getPaymentMethod());
            paymentInfo.setStatus(payment.getStatus());
            paymentInfo.setPaymentDate(payment.getPaymentDate());
            response.setPayment(paymentInfo);
        }

        return response;
    }

    private String generateBookingReference() {
        return "BK" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private String generateTicketNumber() {
        return "TK" + System.currentTimeMillis() + new Random().nextInt(10000);
    }

    private String generateBaggageTag() {
        return "BG" + System.currentTimeMillis() + new Random().nextInt(1000);
    }

    private String generateTransactionId() {
        return "TX" + System.currentTimeMillis() + new Random().nextInt(10000);
    }

    public List<BookingResponse> getBookingsByFlight(Long flightId) {
        List<Booking> bookings = bookingRepository.findByFlightId(flightId);
        return bookings.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public String autoAssignSeat(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getSeatAssigned()) {
            throw new RuntimeException("Seat already assigned");
        }

        // Get flight and aircraft
        Flight flight = ticket.getBooking().getFlight();
        Aircraft aircraft = flight.getAircraft();

        // Find available seat using SeatService
        // For now, just mark as assigned with a placeholder
        // In production, integrate with SeatService to get actual seat
        ticket.setSeatAssigned(true);
        ticket.setSeatSelectionPaid(false); // Not paid for selection

        // If no seat number, assign one
        if (ticket.getSeatNumber() == null || ticket.getSeatNumber().isEmpty()) {
            ticket.setSeatNumber("AUTO-" + ticket.getId());
        }

        ticketRepository.save(ticket);

        return "Seat assigned successfully";
    }
}
