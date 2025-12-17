import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { flightService, bookingService, adminService } from '../services/api';

const BookFlight = () => {
    const { flightId } = useParams();
    const navigate = useNavigate();
    const [flight, setFlight] = useState(null);
    const [seats, setSeats] = useState([]);
    const [loading, setLoading] = useState(true);
    const [step, setStep] = useState(1); // 1: Koltuk Seçimi, 2: Yolcu Bilgileri, 3: Ödeme
    const [selectedSeats, setSelectedSeats] = useState([]);
    const [passengers, setPassengers] = useState([]);
    const [paymentMethod, setPaymentMethod] = useState('CREDIT_CARD');
    const [totalPrice, setTotalPrice] = useState(0);

    useEffect(() => {
        loadFlightAndSeats();
    }, [flightId]);

    useEffect(() => {
        calculateTotalPrice();
    }, [selectedSeats, passengers, flight]);

    const loadFlightAndSeats = async () => {
        try {
            const flightResponse = await flightService.getFlightById(flightId);
            setFlight(flightResponse.data);

            // Load seats for the aircraft
            if (flightResponse.data.aircraft?.id) {
                const seatsResponse = await adminService.getSeatsByAircraft(flightResponse.data.aircraft.id);
                setSeats(seatsResponse.data.filter(s => s.isAvailable));
            }
        } catch (error) {
            console.error('Error loading flight:', error);
            alert('Uçuş bilgileri yüklenemedi');
            navigate('/flights');
        } finally {
            setLoading(false);
        }
    };

    const calculateTotalPrice = () => {
        if (!flight) return;

        let total = 0;

        // If seats are selected, calculate based on seat prices
        if (selectedSeats.length > 0) {
            selectedSeats.forEach((seat) => {
                if (seat.seatClass === 'ECONOMY') {
                    total += flight.pricing?.economyPrice || 500;
                } else {
                    total += flight.pricing?.businessPrice || 1500;
                }
            });

            // Add seat selection fee (200 TL per seat)
            total += selectedSeats.length * 200;
        } else if (passengers.length > 0) {
            // If no seats selected, calculate based on passenger count (economy price)
            passengers.forEach((passenger) => {
                if (passenger.seatClass === 'ECONOMY') {
                    total += flight.pricing?.economyPrice || 500;
                } else {
                    total += flight.pricing?.businessPrice || 1500;
                }
            });
        }

        // Add baggage fees
        passengers.forEach((passenger) => {
            const freeBaggage = flight.pricing?.freeBaggageKg || 20;
            const baggagePrice = flight.pricing?.baggagePricePerKg || 10;
            if (passenger.baggageWeightKg > freeBaggage) {
                total += (passenger.baggageWeightKg - freeBaggage) * baggagePrice;
            }
        });

        setTotalPrice(total);
    };

    const handleSeatSelect = (seat) => {
        const isSelected = selectedSeats.find(s => s.id === seat.id);

        if (isSelected) {
            setSelectedSeats(selectedSeats.filter(s => s.id !== seat.id));
        } else {
            setSelectedSeats([...selectedSeats, seat]);
        }
    };

    const proceedToPassengerInfo = () => {
        // If seats are available but none selected, show alert
        if (seats.length > 0 && selectedSeats.length === 0) {
            const passengerCount = prompt('Kaç yolcu için rezervasyon yapacaksınız? (Koltuklar otomatik atanacak)', '1');
            if (!passengerCount || isNaN(passengerCount) || passengerCount < 1) {
                return;
            }

            const count = parseInt(passengerCount);
            const initialPassengers = Array.from({ length: count }, () => ({
                firstName: '',
                lastName: '',
                passportNumber: '',
                seatNumber: '',
                seatClass: 'ECONOMY',
                baggageWeightKg: flight.pricing?.freeBaggageKg || 20,
            }));
            setPassengers(initialPassengers);
            setSelectedSeats([]);
            setStep(2);
            return;
        }

        // If no seats available, ask for passenger count
        if (seats.length === 0) {
            const passengerCount = prompt('Kaç yolcu için rezervasyon yapacaksınız?', '1');
            if (!passengerCount || isNaN(passengerCount) || passengerCount < 1) {
                return;
            }

            const count = parseInt(passengerCount);
            const initialPassengers = Array.from({ length: count }, () => ({
                firstName: '',
                lastName: '',
                passportNumber: '',
                seatNumber: '',
                seatClass: 'ECONOMY',
                baggageWeightKg: flight.pricing?.freeBaggageKg || 20,
            }));
            setPassengers(initialPassengers);
            setSelectedSeats([]);
        } else {
            // Initialize passengers based on selected seats
            const initialPassengers = selectedSeats.map(seat => ({
                firstName: '',
                lastName: '',
                passportNumber: '',
                seatNumber: seat.seatNumber,
                seatClass: seat.seatClass,
                baggageWeightKg: flight.pricing?.freeBaggageKg || 20,
            }));
            setPassengers(initialPassengers);
        }
        setStep(2);
    };

    const updatePassenger = (index, field, value) => {
        const updated = [...passengers];
        updated[index][field] = value;
        setPassengers(updated);
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        const bookingData = {
            flightId: parseInt(flightId),
            passengers: passengers,
            paymentMethod: paymentMethod,
        };

        try {
            await bookingService.createBooking(bookingData);
            alert('Rezervasyon başarıyla oluşturuldu! Biletiniz e-posta adresinize gönderildi.');
            navigate('/my-bookings');
        } catch (error) {
            console.error('Error creating booking:', error);
            alert(error.response?.data?.message || 'Rezervasyon oluşturulamadı');
        }
    };

    const groupSeatsByClass = () => {
        const business = seats.filter(s => s.seatClass === 'BUSINESS');
        const economy = seats.filter(s => s.seatClass === 'ECONOMY');
        return { business, economy };
    };

    const renderSeatMap = (seatList, seatClass) => {
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
                    background: 'var(--dark-bg)',
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
                                const isSelected = selectedSeats.find(s => s.id === seat.id);
                                return (
                                    <button
                                        key={seat.id}
                                        onClick={() => handleSeatSelect(seat)}
                                        type="button"
                                        style={{
                                            width: '50px',
                                            height: '50px',
                                            border: '2px solid',
                                            borderColor: isSelected ? 'var(--primary-color)' : 'var(--success-color)',
                                            background: isSelected ? 'var(--primary-color)' : 'var(--success-color)',
                                            color: 'white',
                                            borderRadius: '8px',
                                            cursor: 'pointer',
                                            fontSize: '12px',
                                            fontWeight: '600',
                                            display: 'flex',
                                            flexDirection: 'column',
                                            alignItems: 'center',
                                            justifyContent: 'center',
                                            transition: 'all 0.2s',
                                            transform: isSelected ? 'scale(1.1)' : 'scale(1)'
                                        }}
                                        title={`${seat.seatNumber} - ${isSelected ? 'Seçili' : 'Müsait'}`}
                                    >
                                        <span>{seat.seatNumber}</span>
                                        <span style={{ fontSize: '10px' }}>
                                            {seat.isWindowSeat ? '🪟' : seat.isAisleSeat ? '🚶' : ''}
                                        </span>
                                    </button>
                                );
                            })}
                        </div>
                    ))}
                </div>
            </div>
        );
    };

    if (loading) {
        return (
            <div className="loading">
                <div className="spinner"></div>
            </div>
        );
    }

    if (!flight) {
        return null;
    }

    const { business, economy } = groupSeatsByClass();

    return (
        <div className="container" style={{ marginTop: '40px', marginBottom: '80px' }}>
            <h1 style={{ fontSize: '36px', marginBottom: '32px' }}>Rezervasyon Yap</h1>

            {/* Progress Steps */}
            <div style={{
                display: 'flex',
                justifyContent: 'center',
                marginBottom: '40px',
                gap: '16px'
            }}>
                {['Koltuk Seçimi', 'Yolcu Bilgileri', 'Ödeme'].map((label, index) => (
                    <div key={index} style={{
                        display: 'flex',
                        alignItems: 'center',
                        gap: '8px'
                    }}>
                        <div style={{
                            width: '40px',
                            height: '40px',
                            borderRadius: '50%',
                            background: step > index ? 'var(--success-color)' : step === index + 1 ? 'var(--primary-color)' : 'var(--dark-border)',
                            color: 'white',
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            fontWeight: '700'
                        }}>
                            {step > index ? '✓' : index + 1}
                        </div>
                        <span style={{
                            color: step >= index + 1 ? 'var(--text-light)' : 'var(--text-gray)',
                            fontWeight: step === index + 1 ? '600' : '400'
                        }}>
                            {label}
                        </span>
                        {index < 2 && <span style={{ color: 'var(--text-gray)' }}>→</span>}
                    </div>
                ))}
            </div>

            <div className="grid grid-2" style={{ alignItems: 'start' }}>
                {/* Flight Info - Always visible */}
                <div className="card">
                    <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>Uçuş Bilgileri</h2>

                    <div style={{ marginBottom: '16px' }}>
                        <div style={{ fontSize: '20px', fontWeight: '700', color: 'var(--primary-light)' }}>
                            {flight.flightNumber}
                        </div>
                    </div>

                    <div className="flight-route" style={{ marginBottom: '24px' }}>
                        <div className="airport">
                            <div className="airport-code">{flight.departureAirport?.code}</div>
                            <div className="airport-name">{flight.departureAirport?.city}</div>
                        </div>
                        <div className="route-arrow">→</div>
                        <div className="airport">
                            <div className="airport-code">{flight.arrivalAirport?.code}</div>
                            <div className="airport-name">{flight.arrivalAirport?.city}</div>
                        </div>
                    </div>

                    <div style={{ borderTop: '1px solid var(--dark-border)', paddingTop: '16px' }}>
                        <div style={{ marginBottom: '12px' }}>
                            <strong>Kalkış:</strong> {new Date(flight.departureTime).toLocaleString('tr-TR')}
                        </div>
                        <div style={{ marginBottom: '12px' }}>
                            <strong>Varış:</strong> {new Date(flight.arrivalTime).toLocaleString('tr-TR')}
                        </div>
                        <div style={{ marginBottom: '12px' }}>
                            <strong>Uçak:</strong> {flight.aircraft?.model}
                        </div>
                    </div>

                    <div style={{ marginTop: '24px', padding: '16px', background: 'var(--dark-bg)', borderRadius: '8px' }}>
                        <div style={{ marginBottom: '8px' }}>
                            <strong>Economy:</strong> ₺{flight.pricing?.economyPrice?.toFixed(2) || '500.00'}
                        </div>
                        <div style={{ marginBottom: '8px' }}>
                            <strong>Business:</strong> ₺{flight.pricing?.businessPrice?.toFixed(2) || '1500.00'}
                        </div>
                        <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>
                            * {flight.pricing?.freeBaggageKg || 20}kg bagaj ücretsiz, fazlası kg başına ₺{flight.pricing?.baggagePricePerKg || 10}
                        </div>
                    </div>

                    {selectedSeats.length > 0 && (
                        <div style={{ marginTop: '24px', padding: '20px', background: 'var(--gradient-primary)', borderRadius: '8px' }}>
                            <div style={{ fontSize: '16px', marginBottom: '12px', fontWeight: '600' }}>Seçili Koltuklar:</div>
                            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                                {selectedSeats.map(seat => (
                                    <span key={seat.id} style={{
                                        padding: '6px 12px',
                                        background: 'rgba(255,255,255,0.2)',
                                        borderRadius: '6px',
                                        fontSize: '14px',
                                        fontWeight: '600'
                                    }}>
                                        {seat.seatNumber} ({seat.seatClass})
                                    </span>
                                ))}
                            </div>
                            <div style={{ marginTop: '12px', fontSize: '14px', opacity: 0.9 }}>
                                💳 Koltuk Seçim Ücreti: {selectedSeats.length} × 200₺ = {selectedSeats.length * 200}₺
                            </div>
                            <div style={{ marginTop: '16px', fontSize: '24px', fontWeight: '700', textAlign: 'center' }}>
                                Toplam: ₺{totalPrice.toFixed(2)}
                            </div>
                        </div>
                    )}
                </div>

                {/* Step Content */}
                <div className="card">
                    {step === 1 && (
                        <>
                            <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>Koltuk Seçimi</h2>

                            {seats.length === 0 ? (
                                <>
                                    <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                                        <div style={{ fontSize: '48px', marginBottom: '16px' }}>💺</div>
                                        <h3 style={{ fontSize: '20px', marginBottom: '12px' }}>Koltuk Haritası Mevcut Değil</h3>
                                        <p>Bu uçuş için koltuk seçimi yapılamıyor. Yolcu bilgileri adımında manuel olarak koltuk numarası girebilirsiniz.</p>
                                    </div>
                                    <button
                                        onClick={proceedToPassengerInfo}
                                        className="btn btn-primary"
                                        style={{ width: '100%', marginTop: '24px', padding: '16px' }}
                                    >
                                        Yolcu Bilgilerine Geç →
                                    </button>
                                </>
                            ) : (
                                <>
                                    {renderSeatMap(business, 'BUSINESS')}
                                    {renderSeatMap(economy, 'ECONOMY')}

                                    <div style={{
                                        marginTop: '24px',
                                        padding: '16px',
                                        background: 'var(--dark-bg)',
                                        borderRadius: '8px',
                                        fontSize: '14px',
                                        color: 'var(--text-gray)'
                                    }}>
                                        <strong>Bilgi:</strong> Koltuk üzerine tıklayarak seçim yapabilirsiniz.
                                        <br />
                                        🪟 = Pencere kenarı | 🚶 = Koridor kenarı
                                    </div>

                                    <button
                                        onClick={proceedToPassengerInfo}
                                        className="btn btn-primary"
                                        style={{ width: '100%', marginTop: '24px', padding: '16px' }}
                                        disabled={selectedSeats.length === 0}
                                    >
                                        Devam Et ({selectedSeats.length} Koltuk Seçildi)
                                    </button>
                                </>
                            )}
                        </>
                    )}

                    {step === 2 && (
                        <>
                            <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>Yolcu Bilgileri</h2>

                            <form onSubmit={(e) => { e.preventDefault(); setStep(3); }}>
                                {passengers.map((passenger, index) => (
                                    <div key={index} style={{
                                        marginBottom: '32px',
                                        padding: '20px',
                                        background: 'var(--dark-bg)',
                                        borderRadius: '8px',
                                        border: '1px solid var(--dark-border)'
                                    }}>
                                        <h3 style={{ fontSize: '18px', marginBottom: '16px' }}>
                                            Yolcu {index + 1} - {passenger.seatNumber ? `Koltuk ${passenger.seatNumber}` : '🎲 Otomatik Atanacak'}
                                        </h3>

                                        <div className="grid grid-2">
                                            <div className="form-group">
                                                <label className="form-label">Ad *</label>
                                                <input
                                                    type="text"
                                                    className="form-input"
                                                    value={passenger.firstName}
                                                    onChange={(e) => updatePassenger(index, 'firstName', e.target.value)}
                                                    required
                                                />
                                            </div>

                                            <div className="form-group">
                                                <label className="form-label">Soyad *</label>
                                                <input
                                                    type="text"
                                                    className="form-input"
                                                    value={passenger.lastName}
                                                    onChange={(e) => updatePassenger(index, 'lastName', e.target.value)}
                                                    required
                                                />
                                            </div>
                                        </div>

                                        <div className="form-group">
                                            <label className="form-label">Pasaport No *</label>
                                            <input
                                                type="text"
                                                className="form-input"
                                                value={passenger.passportNumber}
                                                onChange={(e) => updatePassenger(index, 'passportNumber', e.target.value)}
                                                required
                                            />
                                        </div>

                                        <div className="form-group">
                                            <label className="form-label">Bagaj Ağırlığı (kg)</label>
                                            <input
                                                type="number"
                                                className="form-input"
                                                value={passenger.baggageWeightKg}
                                                onChange={(e) => updatePassenger(index, 'baggageWeightKg', parseFloat(e.target.value))}
                                                min="0"
                                                max="50"
                                            />
                                            {passenger.baggageWeightKg > (flight.pricing?.freeBaggageKg || 20) && (
                                                <div style={{ fontSize: '14px', color: 'var(--warning-color)', marginTop: '4px' }}>
                                                    Ek bagaj ücreti: ₺{((passenger.baggageWeightKg - (flight.pricing?.freeBaggageKg || 20)) * (flight.pricing?.baggagePricePerKg || 10)).toFixed(2)}
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ))}

                                <div style={{ display: 'flex', gap: '12px' }}>
                                    <button
                                        type="button"
                                        onClick={() => setStep(1)}
                                        className="btn btn-secondary"
                                        style={{ flex: 1 }}
                                    >
                                        ← Geri
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-primary"
                                        style={{ flex: 2 }}
                                    >
                                        Ödemeye Geç →
                                    </button>
                                </div>
                            </form>
                        </>
                    )}

                    {step === 3 && (
                        <>
                            <h2 style={{ fontSize: '24px', marginBottom: '24px' }}>Ödeme</h2>

                            <form onSubmit={handleSubmit}>
                                <div className="form-group">
                                    <label className="form-label">Ödeme Yöntemi</label>
                                    <select
                                        className="form-input"
                                        value={paymentMethod}
                                        onChange={(e) => setPaymentMethod(e.target.value)}
                                    >
                                        <option value="CREDIT_CARD">Kredi Kartı</option>
                                        <option value="DEBIT_CARD">Banka Kartı</option>
                                        <option value="PAYPAL">PayPal</option>
                                        <option value="BANK_TRANSFER">Havale</option>
                                    </select>
                                </div>

                                <div style={{
                                    marginTop: '24px',
                                    padding: '20px',
                                    background: 'var(--gradient-primary)',
                                    borderRadius: '8px',
                                    textAlign: 'center'
                                }}>
                                    <div style={{ fontSize: '16px', marginBottom: '8px' }}>Toplam Tutar</div>
                                    <div style={{ fontSize: '36px', fontWeight: '700' }}>₺{totalPrice.toFixed(2)}</div>
                                </div>

                                <div style={{ display: 'flex', gap: '12px', marginTop: '24px' }}>
                                    <button
                                        type="button"
                                        onClick={() => setStep(2)}
                                        className="btn btn-secondary"
                                        style={{ flex: 1 }}
                                    >
                                        ← Geri
                                    </button>
                                    <button
                                        type="submit"
                                        className="btn btn-success"
                                        style={{ flex: 2, padding: '16px' }}
                                    >
                                        Ödemeyi Tamamla ve Rezervasyon Yap
                                    </button>
                                </div>
                            </form>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BookFlight;
