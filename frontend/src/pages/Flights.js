import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { flightService, airportService, authService } from '../services/api';

const Flights = () => {
    const navigate = useNavigate();
    const [flights, setFlights] = useState([]);
    const [airports, setAirports] = useState([]);
    const [loading, setLoading] = useState(true);
    const [searchParams, setSearchParams] = useState({
        departureAirportId: '',
        arrivalAirportId: '',
        departureDate: '',
    });

    useEffect(() => {
        loadAirports();
        loadFlights();
    }, []);

    const loadAirports = async () => {
        try {
            const response = await airportService.getAllAirports();
            setAirports(response.data);
        } catch (error) {
            console.error('Error loading airports:', error);
        }
    };

    const loadFlights = async () => {
        try {
            setLoading(true);
            const response = await flightService.getAllFlights();
            setFlights(response.data);
        } catch (error) {
            console.error('Error loading flights:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleSearch = async (e) => {
        e.preventDefault();
        if (!searchParams.departureAirportId || !searchParams.arrivalAirportId || !searchParams.departureDate) {
            alert('Lütfen tüm alanları doldurun');
            return;
        }

        try {
            setLoading(true);
            const response = await flightService.searchFlights(
                searchParams.departureAirportId,
                searchParams.arrivalAirportId,
                searchParams.departureDate
            );
            setFlights(response.data);
        } catch (error) {
            console.error('Error searching flights:', error);
            alert('Uçuş arama sırasında bir hata oluştu');
        } finally {
            setLoading(false);
        }
    };

    const handleBookFlight = (flightId) => {
        if (!authService.isAuthenticated()) {
            navigate('/login');
            return;
        }
        navigate(`/book/${flightId}`);
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString('tr-TR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit',
        });
    };

    const getStatusClass = (status) => {
        switch (status) {
            case 'SCHEDULED':
                return 'status-scheduled';
            case 'BOARDING':
                return 'status-boarding';
            case 'CANCELLED':
                return 'status-cancelled';
            default:
                return 'status-scheduled';
        }
    };

    return (
        <div className="container" style={{ marginTop: '40px', marginBottom: '80px' }}>
            <h1 style={{ fontSize: '36px', marginBottom: '32px', textAlign: 'center' }}>
                Uçuş Ara
            </h1>

            {/* Search Form */}
            <div className="card" style={{ marginBottom: '40px' }}>
                <form onSubmit={handleSearch}>
                    <div className="grid grid-3">
                        <div className="form-group">
                            <label className="form-label">Kalkış Havalimanı</label>
                            <select
                                className="form-select"
                                value={searchParams.departureAirportId}
                                onChange={(e) => setSearchParams({ ...searchParams, departureAirportId: e.target.value })}
                            >
                                <option value="">Seçiniz</option>
                                {airports.map((airport) => (
                                    <option key={airport.id} value={airport.id}>
                                        {airport.code} - {airport.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Varış Havalimanı</label>
                            <select
                                className="form-select"
                                value={searchParams.arrivalAirportId}
                                onChange={(e) => setSearchParams({ ...searchParams, arrivalAirportId: e.target.value })}
                            >
                                <option value="">Seçiniz</option>
                                {airports.map((airport) => (
                                    <option key={airport.id} value={airport.id}>
                                        {airport.code} - {airport.name}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="form-group">
                            <label className="form-label">Kalkış Tarihi</label>
                            <input
                                type="datetime-local"
                                className="form-input"
                                value={searchParams.departureDate}
                                onChange={(e) => setSearchParams({ ...searchParams, departureDate: e.target.value })}
                            />
                        </div>
                    </div>

                    <div style={{ display: 'flex', gap: '16px', marginTop: '16px' }}>
                        <button type="submit" className="btn btn-primary">
                            🔍 Uçuş Ara
                        </button>
                        <button type="button" className="btn btn-secondary" onClick={loadFlights}>
                            Tüm Uçuşları Göster
                        </button>
                    </div>
                </form>
            </div>

            {/* Flights List */}
            {loading ? (
                <div className="loading">
                    <div className="spinner"></div>
                </div>
            ) : flights.length === 0 ? (
                <div className="card" style={{ textAlign: 'center', padding: '60px' }}>
                    <div style={{ fontSize: '64px', marginBottom: '16px' }}>✈️</div>
                    <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Uçuş Bulunamadı</h3>
                    <p style={{ color: 'var(--text-gray)' }}>
                        Arama kriterlerinize uygun uçuş bulunamadı. Lütfen farklı tarih veya havalimanı deneyin.
                    </p>
                </div>
            ) : (
                <div className="grid grid-2">
                    {flights.map((flight) => (
                        <div key={flight.id} className="flight-card fade-in">
                            <div className="flight-header">
                                <span className="flight-number">{flight.flightNumber}</span>
                                <span className={`flight-status ${getStatusClass(flight.status)}`}>
                                    {flight.status}
                                </span>
                            </div>

                            <div className="flight-route">
                                <div className="airport">
                                    <div className="airport-code">{flight.departureAirportCode}</div>
                                    <div className="airport-name">{flight.departureAirportName}</div>
                                    <div style={{ fontSize: '14px', color: 'var(--text-gray)', marginTop: '8px' }}>
                                        {formatDate(flight.departureTime)}
                                    </div>
                                </div>

                                <div className="route-arrow">→</div>

                                <div className="airport">
                                    <div className="airport-code">{flight.arrivalAirportCode}</div>
                                    <div className="airport-name">{flight.arrivalAirportName}</div>
                                    <div style={{ fontSize: '14px', color: 'var(--text-gray)', marginTop: '8px' }}>
                                        {formatDate(flight.arrivalTime)}
                                    </div>
                                </div>
                            </div>

                            <div style={{ marginBottom: '16px' }}>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)', marginBottom: '8px' }}>
                                    Uçak: {flight.aircraftModel}
                                </div>
                                <div style={{ fontSize: '14px', color: 'var(--text-gray)' }}>
                                    Müsait Koltuk: {flight.availableSeats} (Economy: {flight.availableEconomySeats}, Business: {flight.availableBusinessSeats})
                                </div>
                            </div>

                            <div className="flight-price">
                                <div>
                                    <div className="price-label">Economy'den başlayan fiyat</div>
                                    <div className="price-amount">
                                        {flight.economyPrice ? `₺${flight.economyPrice.toFixed(2)}` : 'Fiyat yok'}
                                    </div>
                                </div>
                                <button
                                    className="btn btn-primary"
                                    onClick={() => handleBookFlight(flight.id)}
                                    disabled={flight.availableSeats === 0 || flight.status === 'CANCELLED'}
                                >
                                    Rezervasyon Yap
                                </button>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Flights;
