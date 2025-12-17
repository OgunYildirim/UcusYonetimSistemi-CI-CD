import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { bookingService, authService } from '../services/api';

const MyBookings = () => {
    const navigate = useNavigate();
    const [bookings, setBookings] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!authService.isAuthenticated()) {
            navigate('/login');
            return;
        }
        loadBookings();
    }, []);

    const loadBookings = async () => {
        try {
            const response = await bookingService.getUserBookings();
            setBookings(response.data);
        } catch (error) {
            console.error('Error loading bookings:', error);
            alert('Rezervasyonlar yüklenirken hata oluştu');
        } finally {
            setLoading(false);
        }
    };

    const handleCancelBooking = async (bookingId) => {
        if (window.confirm('Bu rezervasyonu iptal etmek istediğinizden emin misiniz?')) {
            try {
                await bookingService.cancelBooking(bookingId);
                alert('Rezervasyon iptal edildi');
                loadBookings();
            } catch (error) {
                console.error('Error cancelling booking:', error);
                alert('Rezervasyon iptal edilirken hata oluştu');
            }
        }
    };

    const getStatusBadge = (status) => {
        const statusMap = {
            'CONFIRMED': { label: '✅ Onaylandı', class: 'status-scheduled', color: 'var(--success-color)' },
            'CANCELLED': { label: '❌ İptal', class: 'status-cancelled', color: 'var(--danger-color)' },
            'PENDING': { label: '⏳ Beklemede', class: 'status-delayed', color: 'var(--warning-color)' },
            'COMPLETED': { label: '✈️ Tamamlandı', class: 'status-arrived', color: 'var(--primary-color)' }
        };
        const statusInfo = statusMap[status] || { label: status, class: '', color: 'var(--text-gray)' };
        return (
            <span
                className={`flight-status ${statusInfo.class}`}
                style={{ background: statusInfo.color }}
            >
                {statusInfo.label}
            </span>
        );
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleString('tr-TR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    if (loading) {
        return (
            <div className="loading">
                <div className="spinner"></div>
            </div>
        );
    }

    return (
        <div className="container" style={{ marginTop: '40px', marginBottom: '80px' }}>
            <h1 style={{ fontSize: '36px', marginBottom: '32px' }}>Rezervasyonlarım</h1>

            {bookings.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
                    <div style={{ fontSize: '64px', marginBottom: '16px' }}>✈️</div>
                    <h2 style={{ fontSize: '24px', marginBottom: '16px' }}>Henüz rezervasyonunuz yok</h2>
                    <p style={{ color: 'var(--text-gray)', marginBottom: '32px' }}>
                        Uçuşları keşfedin ve ilk rezervasyonunuzu yapın!
                    </p>
                    <button
                        onClick={() => navigate('/flights')}
                        className="btn btn-primary"
                        style={{ padding: '12px 32px' }}
                    >
                        Uçuşları İncele
                    </button>
                </div>
            ) : (
                <div style={{ display: 'grid', gap: '24px' }}>
                    {bookings.map((booking) => (
                        <div key={booking.id} className="card">
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'start',
                                marginBottom: '24px',
                                flexWrap: 'wrap',
                                gap: '16px'
                            }}>
                                <div>
                                    <div style={{ fontSize: '24px', fontWeight: '700', color: 'var(--primary-light)', marginBottom: '8px' }}>
                                        {booking.flight?.flightNumber}
                                    </div>
                                    <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>
                                        Rezervasyon No: {booking.bookingReference}
                                    </div>
                                </div>
                                <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                                    {getStatusBadge(booking.status)}
                                    {booking.status === 'CONFIRMED' && (
                                        <button
                                            onClick={() => handleCancelBooking(booking.id)}
                                            className="btn"
                                            style={{
                                                padding: '8px 16px',
                                                fontSize: '14px',
                                                background: 'var(--danger-color)'
                                            }}
                                        >
                                            İptal Et
                                        </button>
                                    )}
                                </div>
                            </div>

                            {/* Flight Route */}
                            <div className="flight-route" style={{ marginBottom: '24px' }}>
                                <div className="airport">
                                    <div className="airport-code">{booking.flight?.departureAirport?.code}</div>
                                    <div className="airport-name">{booking.flight?.departureAirport?.city}</div>
                                    <div className="airport-time">{formatDate(booking.flight?.departureTime)}</div>
                                </div>
                                <div className="route-arrow">
                                    <div className="route-line"></div>
                                    <div className="route-icon">✈️</div>
                                </div>
                                <div className="airport">
                                    <div className="airport-code">{booking.flight?.arrivalAirport?.code}</div>
                                    <div className="airport-name">{booking.flight?.arrivalAirport?.city}</div>
                                    <div className="airport-time">{formatDate(booking.flight?.arrivalTime)}</div>
                                </div>
                            </div>

                            {/* Passengers */}
                            <div style={{
                                padding: '20px',
                                background: 'var(--dark-bg)',
                                borderRadius: '8px',
                                marginBottom: '16px'
                            }}>
                                <h3 style={{ fontSize: '18px', marginBottom: '16px' }}>Yolcular</h3>
                                <div style={{ display: 'grid', gap: '12px' }}>
                                    {booking.tickets?.map((ticket, index) => (
                                        <div key={ticket.id} style={{
                                            display: 'flex',
                                            justifyContent: 'space-between',
                                            alignItems: 'center',
                                            padding: '12px',
                                            background: 'var(--card-bg)',
                                            borderRadius: '6px',
                                            border: '1px solid var(--dark-border)'
                                        }}>
                                            <div>
                                                <div style={{ fontWeight: '600' }}>
                                                    {index + 1}. {ticket.passengerFirstName} {ticket.passengerLastName}
                                                </div>
                                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>
                                                    Pasaport: {ticket.passengerPassportNumber}
                                                </div>
                                            </div>
                                            <div style={{ textAlign: 'right' }}>
                                                <div style={{
                                                    fontSize: '18px',
                                                    fontWeight: '700',
                                                    color: 'var(--primary-light)'
                                                }}>
                                                    Koltuk {ticket.seatNumber}
                                                </div>
                                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>
                                                    {ticket.seatClass}
                                                </div>
                                            </div>
                                        </div>
                                    ))}
                                </div>
                            </div>

                            {/* Payment Info */}
                            <div style={{
                                display: 'flex',
                                justifyContent: 'space-between',
                                alignItems: 'center',
                                padding: '16px',
                                background: 'var(--gradient-primary)',
                                borderRadius: '8px'
                            }}>
                                <div>
                                    <div style={{ fontSize: '14px', marginBottom: '4px' }}>Ödeme Yöntemi</div>
                                    <div style={{ fontWeight: '600' }}>
                                        {booking.payment?.paymentMethod === 'CREDIT_CARD' ? '💳 Kredi Kartı' :
                                            booking.payment?.paymentMethod === 'DEBIT_CARD' ? '💳 Banka Kartı' :
                                                booking.payment?.paymentMethod === 'PAYPAL' ? '💰 PayPal' :
                                                    '🏦 Havale'}
                                    </div>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                    <div style={{ fontSize: '14px', marginBottom: '4px' }}>Toplam Tutar</div>
                                    <div style={{ fontSize: '28px', fontWeight: '700' }}>
                                        ₺{booking.totalPrice?.toFixed(2)}
                                    </div>
                                </div>
                            </div>

                            {/* Booking Date */}
                            <div style={{
                                marginTop: '16px',
                                fontSize: '14px',
                                color: 'var(--text-gray)',
                                textAlign: 'right'
                            }}>
                                Rezervasyon Tarihi: {formatDate(booking.bookingDate)}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default MyBookings;
