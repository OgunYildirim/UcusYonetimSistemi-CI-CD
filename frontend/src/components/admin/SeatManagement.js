import React, { useState, useEffect } from 'react';
import { adminService, flightService, bookingService } from '../../services/api';

const SeatManagement = () => {
    const [flights, setFlights] = useState([]);
    const [selectedFlight, setSelectedFlight] = useState(null);
    const [seats, setSeats] = useState([]);
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadFlights();
    }, []);

    const loadFlights = async () => {
        try {
            const response = await flightService.getAllFlights();
            const upcomingFlights = response.data.filter(f =>
                new Date(f.departureTime) > new Date() && f.status !== 'CANCELLED'
            );
            setFlights(upcomingFlights);
        } catch (error) {
            console.error('Error loading flights:', error);
        }
    };

    const loadFlightSeats = async (flight) => {
        setLoading(true);
        try {
            if (flight.aircraft?.id) {
                const seatsResponse = await adminService.getSeatsByAircraft(flight.aircraft.id);
                setSeats(seatsResponse.data);
            }

            const bookingsResponse = await bookingService.getBookingsByFlight(flight.id);
            setBookings(bookingsResponse.data);
        } catch (error) {
            console.error('Error loading seats:', error);
            setSeats([]);
        } finally {
            setLoading(false);
        }
    };

    const handleFlightSelect = (flight) => {
        setSelectedFlight(flight);
        loadFlightSeats(flight);
    };

    const handleGenerateSeats = async () => {
        if (!selectedFlight?.aircraft?.id) return;

        if (window.confirm(`${selectedFlight.aircraft.registrationNumber} için otomatik koltuk oluşturulsun mu?`)) {
            setLoading(true);
            try {
                await adminService.generateSeats(selectedFlight.aircraft.id);
                alert('Koltuklar başarıyla oluşturuldu!');
                loadFlightSeats(selectedFlight);
            } catch (error) {
                console.error('Error generating seats:', error);
                alert('Koltuklar oluşturulurken hata: ' + (error.response?.data?.message || error.message));
            } finally {
                setLoading(false);
            }
        }
    };

    const getSeatStatus = (seatNumber) => {
        // Check if this seat is taken in any booking
        for (const booking of bookings) {
            for (const ticket of (booking.tickets || [])) {
                if (ticket.seatNumber === seatNumber && ticket.seatAssigned) {
                    return {
                        taken: true,
                        passenger: `${ticket.passengerFirstName} ${ticket.passengerLastName}`,
                        paid: ticket.seatSelectionPaid
                    };
                }
            }
        }
        return { taken: false };
    };

    const groupSeatsByClass = () => {
        const business = seats.filter(s => s.seatClass === 'BUSINESS');
        const economy = seats.filter(s => s.seatClass === 'ECONOMY');
        return { business, economy };
    };

    const renderSeatGrid = (seatList, seatClass) => {
        if (seatList.length === 0) return null;

        const rows = {};
        seatList.forEach(seat => {
            const row = seat.seatNumber.match(/\d+/)[0];
            if (!rows[row]) rows[row] = [];
            rows[row].push(seat);
        });

        return (
            <div style={{ marginBottom: '32px' }}>
                <h4 style={{ fontSize: '18px', marginBottom: '16px', color: 'var(--primary-light)' }}>
                    {seatClass === 'BUSINESS' ? '💼 Business Class' : '🎫 Economy Class'}
                </h4>
                <div style={{
                    display: 'grid',
                    gap: '8px',
                    background: 'var(--card-bg)',
                    padding: '20px',
                    borderRadius: '12px',
                    border: '1px solid var(--dark-border)'
                }}>
                    {Object.keys(rows).sort((a, b) => parseInt(a) - parseInt(b)).map(rowNum => (
                        <div key={rowNum} style={{ display: 'flex', gap: '8px', alignItems: 'center' }}>
                            <span style={{
                                width: '40px',
                                fontSize: '14px',
                                fontWeight: '600',
                                color: 'var(--text-gray)'
                            }}>
                                {rowNum}
                            </span>
                            {rows[rowNum].sort((a, b) => a.seatNumber.localeCompare(b.seatNumber)).map(seat => {
                                const status = getSeatStatus(seat.seatNumber);
                                return (
                                    <div
                                        key={seat.id}
                                        style={{
                                            width: '50px',
                                            height: '50px',
                                            border: '2px solid',
                                            borderColor: status.taken ? 'var(--danger-color)' : 'var(--success-color)',
                                            background: status.taken ? 'var(--danger-color)' : 'var(--success-color)',
                                            color: 'white',
                                            borderRadius: '8px',
                                            fontSize: '12px',
                                            fontWeight: '600',
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            position: 'relative',
                                            cursor: status.taken ? 'help' : 'default'
                                        }}
                                        title={status.taken ? `Dolu - ${status.passenger} ${status.paid ? '(Seçti)' : '(Otomatik)'}` : 'Müsait'}
                                    >
                                        <span>{seat.seatNumber}</span>
                                        <span style={{ fontSize: '10px' }}>
                                            {status.taken ? (status.paid ? '💳' : '🎲') : (seat.isWindowSeat ? '🪟' : seat.isAisleSeat ? '🚶' : '')}
                                        </span>
                                    </div>
                                );
                            })}
                        </div>
                    ))}
                </div>
            </div>
        );
    };

    const { business, economy } = groupSeatsByClass();

    const getTakenSeatsCount = () => {
        let count = 0;
        bookings.forEach(booking => {
            booking.tickets?.forEach(ticket => {
                if (ticket.seatAssigned) count++;
            });
        });
        return count;
    };

    return (
        <div>
            <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>💺 Koltuk Haritası (Uçuşa Göre)</h2>

            {/* Uçuş Seçimi */}
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

            {/* Koltuk Bilgileri */}
            {selectedFlight && (
                <div className="card" style={{ marginBottom: '24px' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
                        <div>
                            <h3 style={{ fontSize: '20px', marginBottom: '8px' }}>
                                {selectedFlight.flightNumber} - {selectedFlight.aircraft?.model}
                            </h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                {selectedFlight.aircraft?.registrationNumber}
                            </p>
                        </div>
                        <button
                            onClick={handleGenerateSeats}
                            className="btn btn-primary"
                            disabled={loading || seats.length > 0}
                        >
                            {seats.length > 0 ? '✅ Koltuklar Mevcut' : '🔄 Otomatik Oluştur'}
                        </button>
                    </div>

                    {/* Koltuk İstatistikleri */}
                    {seats.length > 0 && (
                        <div style={{
                            display: 'grid',
                            gridTemplateColumns: 'repeat(auto-fit, minmax(150px, 1fr))',
                            gap: '16px',
                            marginBottom: '24px',
                            padding: '16px',
                            background: 'var(--dark-bg)',
                            borderRadius: '8px'
                        }}>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--primary-light)' }}>
                                    {seats.length}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Toplam Koltuk</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--danger-color)' }}>
                                    {getTakenSeatsCount()}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Dolu</div>
                            </div>
                            <div>
                                <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--success-color)' }}>
                                    {seats.length - getTakenSeatsCount()}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>Müsait</div>
                            </div>
                        </div>
                    )}

                    {/* Koltuk Gösterimi */}
                    {loading ? (
                        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                            Koltuklar yükleniyor...
                        </div>
                    ) : seats.length === 0 ? (
                        <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                            Bu uçak için henüz koltuk oluşturulmamış. "Otomatik Oluştur" butonuna tıklayın.
                        </div>
                    ) : (
                        <>
                            {renderSeatGrid(business, 'BUSINESS')}
                            {renderSeatGrid(economy, 'ECONOMY')}

                            {/* Açıklama */}
                            <div style={{
                                marginTop: '24px',
                                padding: '16px',
                                background: 'var(--dark-bg)',
                                borderRadius: '8px',
                                fontSize: '14px',
                                color: 'var(--text-gray)'
                            }}>
                                <strong>Bilgi:</strong> Koltuk durumları bu uçuş için gösterilmektedir.
                                <br />
                                🪟 = Pencere | 🚶 = Koridor | 💳 = Kullanıcı seçti | 🎲 = Otomatik atandı
                            </div>
                        </>
                    )}
                </div>
            )}
        </div>
    );
};

export default SeatManagement;
