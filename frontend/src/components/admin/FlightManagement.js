import React, { useState, useEffect } from 'react';
import { flightService, airportService, adminService } from '../../services/api';

const FlightManagement = () => {
    const [flights, setFlights] = useState([]);
    const [airports, setAirports] = useState([]);
    const [aircrafts, setAircrafts] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingFlight, setEditingFlight] = useState(null);
    const [formData, setFormData] = useState({
        flightNumber: '',
        departureAirportId: '',
        arrivalAirportId: '',
        aircraftId: '',
        departureTime: '',
        arrivalTime: '',
        status: 'SCHEDULED'
    });

    useEffect(() => {
        loadFlights();
        loadAirports();
        loadAircrafts();
    }, []);

    const loadFlights = async () => {
        try {
            const response = await flightService.getAllFlights();
            setFlights(response.data);
        } catch (error) {
            console.error('Error loading flights:', error);
            alert('Uçuşlar yüklenirken hata oluştu');
        }
    };

    const loadAirports = async () => {
        try {
            const response = await airportService.getAllAirports();
            setAirports(response.data);
        } catch (error) {
            console.error('Error loading airports:', error);
        }
    };

    const loadAircrafts = async () => {
        try {
            const response = await adminService.getAllAircrafts();
            setAircrafts(response.data.filter(a => !a.underMaintenance));
        } catch (error) {
            console.error('Error loading aircrafts:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData({
            ...formData,
            [name]: value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const submitData = {
                ...formData,
                departureAirportId: parseInt(formData.departureAirportId),
                arrivalAirportId: parseInt(formData.arrivalAirportId),
                aircraftId: parseInt(formData.aircraftId)
            };

            if (editingFlight) {
                await flightService.updateFlight(editingFlight.id, submitData);
                alert('Uçuş başarıyla güncellendi!');
            } else {
                await flightService.createFlight(submitData);
                alert('Uçuş başarıyla eklendi!');
            }
            setShowModal(false);
            resetForm();
            loadFlights();
        } catch (error) {
            console.error('Error saving flight:', error);
            alert('Uçuş kaydedilirken hata oluştu: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleEdit = (flight) => {
        setEditingFlight(flight);
        setFormData({
            flightNumber: flight.flightNumber,
            departureAirportId: flight.departureAirport?.id || '',
            arrivalAirportId: flight.arrivalAirport?.id || '',
            aircraftId: flight.aircraft?.id || '',
            departureTime: flight.departureTime ? flight.departureTime.substring(0, 16) : '',
            arrivalTime: flight.arrivalTime ? flight.arrivalTime.substring(0, 16) : '',
            status: flight.status
        });
        setShowModal(true);
    };

    const handleCancel = async (id) => {
        if (window.confirm('Bu uçuşu iptal etmek istediğinizden emin misiniz?')) {
            try {
                const flight = flights.find(f => f.id === id);
                await flightService.updateFlight(id, { ...flight, status: 'CANCELLED' });
                alert('Uçuş iptal edildi!');
                loadFlights();
            } catch (error) {
                console.error('Error cancelling flight:', error);
                alert('Uçuş iptal edilirken hata oluştu');
            }
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bu uçuşu silmek istediğinizden emin misiniz?')) {
            try {
                await flightService.deleteFlight(id);
                alert('Uçuş başarıyla silindi!');
                loadFlights();
            } catch (error) {
                console.error('Error deleting flight:', error);
                alert('Uçuş silinirken hata oluştu: ' + (error.response?.data?.message || error.message));
            }
        }
    };

    const resetForm = () => {
        setFormData({
            flightNumber: '',
            departureAirportId: '',
            arrivalAirportId: '',
            aircraftId: '',
            departureTime: '',
            arrivalTime: '',
            status: 'SCHEDULED'
        });
        setEditingFlight(null);
    };

    const handleAddNew = () => {
        resetForm();
        setShowModal(true);
    };

    const formatDateTime = (dateString) => {
        return new Date(dateString).toLocaleString('tr-TR', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const getStatusBadge = (status) => {
        const statusMap = {
            'SCHEDULED': { label: '📅 Planlandı', class: 'status-scheduled' },
            'BOARDING': { label: '🚪 Boarding', class: 'status-boarding' },
            'DEPARTED': { label: '✈️ Kalktı', class: 'status-departed' },
            'ARRIVED': { label: '🛬 İndi', class: 'status-arrived' },
            'CANCELLED': { label: '❌ İptal', class: 'status-cancelled' },
            'DELAYED': { label: '⏰ Gecikti', class: 'status-delayed' }
        };
        const statusInfo = statusMap[status] || { label: status, class: '' };
        return <span className={`flight-status ${statusInfo.class}`}>{statusInfo.label}</span>;
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h2 style={{ fontSize: '24px', margin: 0 }}>✈️ Uçuş Yönetimi</h2>
                <button onClick={handleAddNew} className="btn btn-primary">
                    + Yeni Uçuş Ekle
                </button>
            </div>

            <div className="card">
                <div style={{ overflowX: 'auto' }}>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Uçuş No</th>
                                <th>Kalkış</th>
                                <th>Varış</th>
                                <th>Uçak</th>
                                <th>Kalkış Zamanı</th>
                                <th>Varış Zamanı</th>
                                <th>Durum</th>
                                <th>İşlemler</th>
                            </tr>
                        </thead>
                        <tbody>
                            {flights.length === 0 ? (
                                <tr>
                                    <td colSpan="8" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                                        Henüz uçuş eklenmemiş
                                    </td>
                                </tr>
                            ) : (
                                flights.map(flight => (
                                    <tr key={flight.id}>
                                        <td><strong>{flight.flightNumber}</strong></td>
                                        <td>{flight.departureAirport?.code} - {flight.departureAirport?.city}</td>
                                        <td>{flight.arrivalAirport?.code} - {flight.arrivalAirport?.city}</td>
                                        <td>{flight.aircraft?.registrationNumber}</td>
                                        <td>{formatDateTime(flight.departureTime)}</td>
                                        <td>{formatDateTime(flight.arrivalTime)}</td>
                                        <td>{getStatusBadge(flight.status)}</td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap' }}>
                                                <button
                                                    onClick={() => handleEdit(flight)}
                                                    className="btn btn-secondary"
                                                    style={{ padding: '6px 12px', fontSize: '14px' }}
                                                >
                                                    ✏️ Düzenle
                                                </button>
                                                {flight.status !== 'CANCELLED' && (
                                                    <button
                                                        onClick={() => handleCancel(flight.id)}
                                                        className="btn"
                                                        style={{ padding: '6px 12px', fontSize: '14px', background: 'var(--warning-color)' }}
                                                    >
                                                        ❌ İptal
                                                    </button>
                                                )}
                                                <button
                                                    onClick={() => handleDelete(flight.id)}
                                                    className="btn"
                                                    style={{ padding: '6px 12px', fontSize: '14px', background: 'var(--danger-color)' }}
                                                >
                                                    🗑️ Sil
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                        </tbody>
                    </table>
                </div>
            </div>

            {/* Modal */}
            {showModal && (
                <div style={{
                    position: 'fixed',
                    top: 0,
                    left: 0,
                    right: 0,
                    bottom: 0,
                    background: 'rgba(0,0,0,0.7)',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    zIndex: 1000
                }}>
                    <div className="card" style={{ maxWidth: '600px', width: '90%', maxHeight: '90vh', overflow: 'auto' }}>
                        <h3 style={{ fontSize: '24px', marginBottom: '24px' }}>
                            {editingFlight ? '✏️ Uçuş Düzenle' : '➕ Yeni Uçuş Ekle'}
                        </h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Uçuş Numarası *</label>
                                <input
                                    type="text"
                                    name="flightNumber"
                                    className="form-input"
                                    value={formData.flightNumber}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Örn: TK001"
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Kalkış Havalimanı *</label>
                                    <select
                                        name="departureAirportId"
                                        className="form-input"
                                        value={formData.departureAirportId}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="">Seçin</option>
                                        {airports.map(airport => (
                                            <option key={airport.id} value={airport.id}>
                                                {airport.code} - {airport.city}
                                            </option>
                                        ))}
                                    </select>
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Varış Havalimanı *</label>
                                    <select
                                        name="arrivalAirportId"
                                        className="form-input"
                                        value={formData.arrivalAirportId}
                                        onChange={handleInputChange}
                                        required
                                    >
                                        <option value="">Seçin</option>
                                        {airports.map(airport => (
                                            <option key={airport.id} value={airport.id}>
                                                {airport.code} - {airport.city}
                                            </option>
                                        ))}
                                    </select>
                                </div>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Uçak *</label>
                                <select
                                    name="aircraftId"
                                    className="form-input"
                                    value={formData.aircraftId}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="">Seçin</option>
                                    {aircrafts.map(aircraft => (
                                        <option key={aircraft.id} value={aircraft.id}>
                                            {aircraft.registrationNumber} - {aircraft.model} ({aircraft.totalSeats} koltuk)
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Kalkış Zamanı *</label>
                                    <input
                                        type="datetime-local"
                                        name="departureTime"
                                        className="form-input"
                                        value={formData.departureTime}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Varış Zamanı *</label>
                                    <input
                                        type="datetime-local"
                                        name="arrivalTime"
                                        className="form-input"
                                        value={formData.arrivalTime}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Durum *</label>
                                <select
                                    name="status"
                                    className="form-input"
                                    value={formData.status}
                                    onChange={handleInputChange}
                                    required
                                >
                                    <option value="SCHEDULED">Planlandı</option>
                                    <option value="BOARDING">Boarding</option>
                                    <option value="DEPARTED">Kalktı</option>
                                    <option value="ARRIVED">İndi</option>
                                    <option value="CANCELLED">İptal</option>
                                    <option value="DELAYED">Gecikti</option>
                                </select>
                            </div>

                            <div style={{ display: 'flex', gap: '12px', justifyContent: 'flex-end' }}>
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowModal(false);
                                        resetForm();
                                    }}
                                    className="btn btn-secondary"
                                >
                                    İptal
                                </button>
                                <button type="submit" className="btn btn-primary">
                                    {editingFlight ? '💾 Güncelle' : '➕ Ekle'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default FlightManagement;
