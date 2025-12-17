import React, { useState, useEffect } from 'react';
import { flightService, bookingService, adminService } from '../../services/api';

const SeatAssignmentManagement = () => {
    const [flights, setFlights] = useState([]);
    const [selectedFlight, setSelectedFlight] = useState(null);
    const [bookings, setBookings] = useState([]);
    const [seats, setSeats] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadFlights();
    }, []);

    const loadFlights = async () => {
        try {
            const response = await flightService.getAllFlights();
            // Only show upcoming flights
            const upcomingFlights = response.data.filter(f =>
                new Date(f.departureTime) > new Date() && f.status !== 'CANCELLED'
            );
            setFlights(upcomingFlights);
        } catch (error) {
            console.error('Error loading flights:', error);
        }
    };

    const loadFlightData = async (flight) => {
        setLoading(true);
        try {
            // Load bookings for this flight
            const bookingsResponse = await bookingService.getBookingsByFlight(flight.id);
            setBookings(bookingsResponse.data);

            // Load seats for the aircraft
            if (flight.aircraft?.id) {
                const seatsResponse = await adminService.getSeatsByAircraft(flight.aircraft.id);
                setSeats(seatsResponse.data);
            }
        } catch (error) {
            console.error('Error loading flight data:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleFlightSelect = (flight) => {
        setSelectedFlight(flight);
        loadFlightData(flight);
    };

    const handleAutoAssign = async (ticketId, seatClass) => {
        try {
            await bookingService.autoAssignSeat(ticketId, seatClass);
            alert('Koltuk otomatik olarak atandı!');
            loadFlightData(selectedFlight);
        } catch (error) {
            console.error('Error auto-assigning seat:', error);
            alert('Koltuk atanamadı: ' + (error.response?.data?.message || error.message));
        }
    };

    const getAssignedTickets = () => {
        const tickets = [];
        bookings.forEach(booking => {
            booking.tickets?.forEach(ticket => {
                if (ticket.seatAssigned && ticket.seatNumber) {
                    tickets.push({ ...ticket, booking });
                }
            });
        });
        return tickets;
    };

    const getPendingTickets = () => {
        const tickets = [];
        bookings.forEach(booking => {
            booking.tickets?.forEach(ticket => {
                if (!ticket.seatAssigned || !ticket.seatNumber) {
                    tickets.push({ ...ticket, booking });
                }
            });
        });
        return tickets;
    };

    const assignedTickets = getAssignedTickets();
    const pendingTickets = getPendingTickets();

    return (
        <div>
            <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>🎫 Koltuk Atama Yönetimi</h2>

            {/* Flight Selection */}
            <div className="card" style={{ marginBottom: '24px' }}>
                <h3 style={{ fontSize: '18px', marginBottom: '16px' }}>Uçuş Seçin</h3>
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '12px' }}>
                    {flights.map(flight => (
                        <button
                            key={flight.id}
                            onClick={() => handleFlightSelect(flight)}
                            className={selectedFlight?.id === flight.id ? 'btn btn-primary' : 'btn btn-secondary'}
                            style={{
                                padding: '16px',
                                textAlign: 'left',
                                display: 'flex',
                                flexDirection: 'column',
                                gap: '8px'
                            }}
                        >
                            <strong style={{ fontSize: '16px' }}>{flight.flightNumber}</strong>
                            <span style={{ fontSize: '14px', opacity: 0.9 }}>
                                {flight.departureAirport?.code} → {flight.arrivalAirport?.code}
                            </span>
                            <span style={{ fontSize: '12px', opacity: 0.7 }}>
                                {new Date(flight.departureTime).toLocaleDateString('tr-TR')}
                            </span>
                        </button>
                    ))}
                </div>
            </div>

            {selectedFlight && !loading && (
                <>
                    {/* Flight Info */}
                    <div className="card" style={{ marginBottom: '24px' }}>
                        <h3 style={{ fontSize: '20px', marginBottom: '16px' }}>
                            {selectedFlight.flightNumber} - {selectedFlight.aircraft?.model}
                        </h3>
                        <div style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
                            gap: '16px',
                            padding: '16px',
                            background: 'var(--dark-bg)',
                            borderRadius: '8px'
                        }}>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--success-color)' }}>
                                    {assignedTickets.length}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Koltuk Atandı</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--warning-color)' }}>
                                    {pendingTickets.length}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Atama Bekliyor</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--primary-light)' }}>
                                    {seats.filter(s => s.isAvailable).length}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Boş Koltuk</div>
                            </div>
                        </div>
                    </div>

                    {/* Pending Assignments */}
                    {pendingTickets.length > 0 && (
                        <div className="card" style={{ marginBottom: '24px' }}>
                            <h3 style={{ fontSize: '18px', marginBottom: '16px', color: 'var(--warning-color)' }}>
                                ⏳ Koltuk Atama Bekleyenler ({pendingTickets.length})
                            </h3>
                            <div style={{ overflowX: 'auto' }}>
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th>Yolcu</th>
                                            <th>Rezervasyon No</th>
                                            <th>Sınıf</th>
                                            <th>Durum</th>
                                            <th>İşlem</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {pendingTickets.map(ticket => (
                                            <tr key={ticket.id}>
                                                <td>
                                                    <strong>{ticket.passengerFirstName} {ticket.passengerLastName}</strong>
                                                    <div style={{ fontSize: '12px', color: 'var(--text-gray)' }}>
                                                        {ticket.passportNumber}
                                                    </div>
                                                </td>
                                                <td>{ticket.booking?.bookingReference}</td>
                                                <td>
                                                    <span className={`flight-status ${ticket.seatClass === 'BUSINESS' ? 'status-boarding' : 'status-scheduled'}`}>
                                                        {ticket.seatClass}
                                                    </span>
                                                </td>
                                                <td>
                                                    <span className="flight-status status-delayed">
                                                        Koltuk Bekleniyor
                                                    </span>
                                                </td>
                                                <td>
                                                    <button
                                                        onClick={() => handleAutoAssign(ticket.id, ticket.seatClass)}
                                                        className="btn btn-primary"
                                                        style={{ padding: '6px 12px', fontSize: '14px' }}
                                                    >
                                                        🎲 Otomatik Ata
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}

                    {/* Assigned Seats */}
                    {assignedTickets.length > 0 && (
                        <div className="card">
                            <h3 style={{ fontSize: '18px', marginBottom: '16px', color: 'var(--success-color)' }}>
                                ✅ Koltuk Atananlar ({assignedTickets.length})
                            </h3>
                            <div style={{ overflowX: 'auto' }}>
                                <table className="table">
                                    <thead>
                                        <tr>
                                            <th>Yolcu</th>
                                            <th>Rezervasyon No</th>
                                            <th>Koltuk</th>
                                            <th>Sınıf</th>
                                            <th>Seçim Durumu</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {assignedTickets.map(ticket => (
                                            <tr key={ticket.id}>
                                                <td>
                                                    <strong>{ticket.passengerFirstName} {ticket.passengerLastName}</strong>
                                                    <div style={{ fontSize: '12px', color: 'var(--text-gray)' }}>
                                                        {ticket.passportNumber}
                                                    </div>
                                                </td>
                                                <td>{ticket.booking?.bookingReference}</td>
                                                <td>
                                                    <strong style={{ fontSize: '18px', color: 'var(--primary-light)' }}>
                                                        {ticket.seatNumber}
                                                    </strong>
                                                </td>
                                                <td>
                                                    <span className={`flight-status ${ticket.seatClass === 'BUSINESS' ? 'status-boarding' : 'status-scheduled'}`}>
                                                        {ticket.seatClass}
                                                    </span>
                                                </td>
                                                <td>
                                                    {ticket.seatSelectionPaid ? (
                                                        <span className="flight-status status-scheduled">
                                                            💳 Koltuk Seçti (+200₺)
                                                        </span>
                                                    ) : (
                                                        <span className="flight-status status-delayed">
                                                            🎲 Otomatik Atandı
                                                        </span>
                                                    )}
                                                </td>
                                            </tr>
                                        ))}
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    )}
                </>
            )}

            {loading && (
                <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                    Yükleniyor...
                </div>
            )}
        </div>
    );
};

export default SeatAssignmentManagement;
