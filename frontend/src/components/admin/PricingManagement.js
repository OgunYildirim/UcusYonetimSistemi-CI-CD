import React, { useState, useEffect } from 'react';
import { adminService, flightService } from '../../services/api';

const PricingManagement = () => {
    const [pricings, setPricings] = useState([]);
    const [flights, setFlights] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingPricing, setEditingPricing] = useState(null);
    const [formData, setFormData] = useState({
        flightId: '',
        economyPrice: '',
        businessPrice: '',
        baggagePricePerKg: '',
        freeBaggageKg: '',
        effectiveFrom: '',
        effectiveTo: '',
        active: true
    });

    useEffect(() => {
        loadPricings();
        loadFlights();
    }, []);

    const loadPricings = async () => {
        try {
            const response = await adminService.getAllPricing();
            setPricings(response.data);
        } catch (error) {
            console.error('Error loading pricings:', error);
            alert('Fiyatlandırmalar yüklenirken hata oluştu');
        }
    };

    const loadFlights = async () => {
        try {
            const response = await flightService.getAllFlights();
            setFlights(response.data);
        } catch (error) {
            console.error('Error loading flights:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value, type, checked } = e.target;
        setFormData({
            ...formData,
            [name]: type === 'checkbox' ? checked : value
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        // Validate flightId
        if (!formData.flightId || formData.flightId === '') {
            alert('Lütfen bir uçuş seçin!');
            return;
        }

        try {
            const submitData = {
                flightId: parseInt(formData.flightId),
                economyPrice: parseFloat(formData.economyPrice),
                businessPrice: parseFloat(formData.businessPrice),
                baggagePricePerKg: parseFloat(formData.baggagePricePerKg),
                freeBaggageKg: parseInt(formData.freeBaggageKg),
                // Add time to dates to match LocalDateTime format
                effectiveFrom: formData.effectiveFrom ? `${formData.effectiveFrom}T00:00:00` : null,
                effectiveTo: formData.effectiveTo ? `${formData.effectiveTo}T23:59:59` : null,
                active: formData.active
            };

            console.log('Submitting pricing data:', submitData);

            if (editingPricing) {
                await adminService.updatePricing(editingPricing.id, submitData);
                alert('Fiyatlandırma başarıyla güncellendi!');
            } else {
                await adminService.createPricing(submitData);
                alert('Fiyatlandırma başarıyla eklendi!');
            }
            setShowModal(false);
            resetForm();
            loadPricings();
        } catch (error) {
            console.error('Error saving pricing:', error);
            alert('Fiyatlandırma kaydedilirken hata oluştu: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleEdit = (pricing) => {
        setEditingPricing(pricing);
        setFormData({
            flightId: pricing.flight?.id || '',
            economyPrice: pricing.economyPrice,
            businessPrice: pricing.businessPrice,
            baggagePricePerKg: pricing.baggagePricePerKg,
            freeBaggageKg: pricing.freeBaggageKg,
            effectiveFrom: pricing.effectiveFrom ? pricing.effectiveFrom.split('T')[0] : '',
            effectiveTo: pricing.effectiveTo ? pricing.effectiveTo.split('T')[0] : '',
            active: pricing.active
        });
        setShowModal(true);
    };

    const resetForm = () => {
        setFormData({
            flightId: '',
            economyPrice: '',
            businessPrice: '',
            baggagePricePerKg: '',
            freeBaggageKg: '',
            effectiveFrom: '',
            effectiveTo: '',
            active: true
        });
        setEditingPricing(null);
    };

    const handleAddNew = () => {
        resetForm();
        setShowModal(true);
    };

    const getFlightInfo = (flightId) => {
        const flight = flights.find(f => f.id === flightId);
        if (!flight) return 'N/A';
        return `${flight.flightNumber} (${flight.departureAirport?.code} → ${flight.arrivalAirport?.code})`;
    };

    const formatDate = (dateString) => {
        if (!dateString) return 'Süresiz';
        return new Date(dateString).toLocaleDateString('tr-TR');
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h2 style={{ fontSize: '24px', margin: 0 }}>💰 Fiyatlandırma Yönetimi</h2>
                <button onClick={handleAddNew} className="btn btn-primary">
                    + Yeni Fiyatlandırma Ekle
                </button>
            </div>

            <div className="card">
                <div style={{ overflowX: 'auto' }}>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Uçuş</th>
                                <th>Economy</th>
                                <th>Business</th>
                                <th>Bagaj (kg başına)</th>
                                <th>Ücretsiz Bagaj</th>
                                <th>Geçerlilik</th>
                                <th>Durum</th>
                                <th>İşlemler</th>
                            </tr>
                        </thead>
                        <tbody>
                            {pricings.length === 0 ? (
                                <tr>
                                    <td colSpan="8" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                                        Henüz fiyatlandırma eklenmemiş
                                    </td>
                                </tr>
                            ) : (
                                pricings.map(pricing => (
                                    <tr key={pricing.id}>
                                        <td><strong>{getFlightInfo(pricing.flight?.id)}</strong></td>
                                        <td>{pricing.economyPrice.toFixed(2)} ₺</td>
                                        <td>{pricing.businessPrice.toFixed(2)} ₺</td>
                                        <td>{pricing.baggagePricePerKg.toFixed(2)} ₺</td>
                                        <td>{pricing.freeBaggageKg} kg</td>
                                        <td>
                                            {formatDate(pricing.effectiveFrom)}
                                            {pricing.effectiveTo && ` - ${formatDate(pricing.effectiveTo)}`}
                                        </td>
                                        <td>
                                            {pricing.active ? (
                                                <span className="flight-status status-scheduled">✅ Aktif</span>
                                            ) : (
                                                <span className="flight-status status-cancelled">❌ Pasif</span>
                                            )}
                                        </td>
                                        <td>
                                            <button
                                                onClick={() => handleEdit(pricing)}
                                                className="btn btn-secondary"
                                                style={{ padding: '6px 12px', fontSize: '14px' }}
                                            >
                                                ✏️ Düzenle
                                            </button>
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
                            {editingPricing ? '✏️ Fiyatlandırma Düzenle' : '➕ Yeni Fiyatlandırma Ekle'}
                        </h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Uçuş *</label>
                                <select
                                    name="flightId"
                                    className="form-input"
                                    value={formData.flightId}
                                    onChange={handleInputChange}
                                    required
                                    disabled={editingPricing !== null}
                                >
                                    <option value="">Uçuş seçin</option>
                                    {flights.map(flight => (
                                        <option key={flight.id} value={flight.id}>
                                            {flight.flightNumber} - {flight.departureAirport?.code} → {flight.arrivalAirport?.code}
                                        </option>
                                    ))}
                                </select>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Economy Fiyatı (₺) *</label>
                                    <input
                                        type="number"
                                        name="economyPrice"
                                        className="form-input"
                                        value={formData.economyPrice}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                        step="0.01"
                                        placeholder="500.00"
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Business Fiyatı (₺) *</label>
                                    <input
                                        type="number"
                                        name="businessPrice"
                                        className="form-input"
                                        value={formData.businessPrice}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                        step="0.01"
                                        placeholder="1500.00"
                                    />
                                </div>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Bagaj Fiyatı (₺/kg) *</label>
                                    <input
                                        type="number"
                                        name="baggagePricePerKg"
                                        className="form-input"
                                        value={formData.baggagePricePerKg}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                        step="0.01"
                                        placeholder="10.00"
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Ücretsiz Bagaj (kg) *</label>
                                    <input
                                        type="number"
                                        name="freeBaggageKg"
                                        className="form-input"
                                        value={formData.freeBaggageKg}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                        placeholder="20"
                                    />
                                </div>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Geçerlilik Başlangıcı *</label>
                                    <input
                                        type="date"
                                        name="effectiveFrom"
                                        className="form-input"
                                        value={formData.effectiveFrom}
                                        onChange={handleInputChange}
                                        required
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Geçerlilik Bitişi</label>
                                    <input
                                        type="date"
                                        name="effectiveTo"
                                        className="form-input"
                                        value={formData.effectiveTo}
                                        onChange={handleInputChange}
                                    />
                                </div>
                            </div>

                            <div style={{ marginBottom: '24px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        name="active"
                                        checked={formData.active}
                                        onChange={handleInputChange}
                                    />
                                    <span>Aktif</span>
                                </label>
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
                                    {editingPricing ? '💾 Güncelle' : '➕ Ekle'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default PricingManagement;
