import React, { useState, useEffect } from 'react';
import { airportService, adminService } from '../../services/api';

const AirportManagement = () => {
    const [airports, setAirports] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingAirport, setEditingAirport] = useState(null);
    const [formData, setFormData] = useState({
        code: '',
        name: '',
        city: '',
        country: '',
        address: '',
        active: true
    });

    useEffect(() => {
        loadAirports();
    }, []);

    const loadAirports = async () => {
        try {
            const response = await airportService.getAllAirports();
            setAirports(response.data);
        } catch (error) {
            console.error('Error loading airports:', error);
            alert('Havalimanları yüklenirken hata oluştu');
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
        try {
            if (editingAirport) {
                await adminService.updateAirport(editingAirport.id, formData);
                alert('Havalimanı başarıyla güncellendi!');
            } else {
                await adminService.createAirport(formData);
                alert('Havalimanı başarıyla eklendi!');
            }
            setShowModal(false);
            resetForm();
            loadAirports();
        } catch (error) {
            console.error('Error saving airport:', error);
            alert('Havalimanı kaydedilirken hata oluştu: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleEdit = (airport) => {
        setEditingAirport(airport);
        setFormData(airport);
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bu havalimanını silmek istediğinizden emin misiniz?')) {
            try {
                await adminService.deleteAirport(id);
                alert('Havalimanı başarıyla silindi!');
                loadAirports();
            } catch (error) {
                console.error('Error deleting airport:', error);
                alert('Havalimanı silinirken hata oluştu: ' + (error.response?.data?.message || error.message));
            }
        }
    };

    const resetForm = () => {
        setFormData({
            code: '',
            name: '',
            city: '',
            country: '',
            address: '',
            active: true
        });
        setEditingAirport(null);
    };

    const handleAddNew = () => {
        resetForm();
        setShowModal(true);
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h2 style={{ fontSize: '24px', margin: 0 }}>🏢 Havalimanı Yönetimi</h2>
                <button onClick={handleAddNew} className="btn btn-primary">
                    + Yeni Havalimanı Ekle
                </button>
            </div>

            <div className="card">
                <div style={{ overflowX: 'auto' }}>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Kod</th>
                                <th>Havalimanı Adı</th>
                                <th>Şehir</th>
                                <th>Ülke</th>
                                <th>Adres</th>
                                <th>Durum</th>
                                <th>İşlemler</th>
                            </tr>
                        </thead>
                        <tbody>
                            {airports.length === 0 ? (
                                <tr>
                                    <td colSpan="7" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                                        Henüz havalimanı eklenmemiş
                                    </td>
                                </tr>
                            ) : (
                                airports.map(airport => (
                                    <tr key={airport.id}>
                                        <td><strong>{airport.code}</strong></td>
                                        <td>{airport.name}</td>
                                        <td>{airport.city}</td>
                                        <td>{airport.country}</td>
                                        <td style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>
                                            {airport.address}
                                        </td>
                                        <td>
                                            {airport.active ? (
                                                <span className="flight-status status-scheduled">✅ Aktif</span>
                                            ) : (
                                                <span className="flight-status status-cancelled">❌ Pasif</span>
                                            )}
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '8px' }}>
                                                <button
                                                    onClick={() => handleEdit(airport)}
                                                    className="btn btn-secondary"
                                                    style={{ padding: '6px 12px', fontSize: '14px' }}
                                                >
                                                    ✏️ Düzenle
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(airport.id)}
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
                            {editingAirport ? '✏️ Havalimanı Düzenle' : '➕ Yeni Havalimanı Ekle'}
                        </h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Havalimanı Kodu (IATA) *</label>
                                <input
                                    type="text"
                                    name="code"
                                    className="form-input"
                                    value={formData.code}
                                    onChange={handleInputChange}
                                    required
                                    maxLength="10"
                                    placeholder="Örn: IST"
                                    style={{ textTransform: 'uppercase' }}
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Havalimanı Adı *</label>
                                <input
                                    type="text"
                                    name="name"
                                    className="form-input"
                                    value={formData.name}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Örn: Istanbul Airport"
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Şehir *</label>
                                    <input
                                        type="text"
                                        name="city"
                                        className="form-input"
                                        value={formData.city}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Örn: Istanbul"
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Ülke *</label>
                                    <input
                                        type="text"
                                        name="country"
                                        className="form-input"
                                        value={formData.country}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Örn: Turkey"
                                    />
                                </div>
                            </div>

                            <div className="form-group">
                                <label className="form-label">Adres</label>
                                <textarea
                                    name="address"
                                    className="form-input"
                                    value={formData.address}
                                    onChange={handleInputChange}
                                    rows="3"
                                    placeholder="Havalimanı adresi"
                                />
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
                                    {editingAirport ? '💾 Güncelle' : '➕ Ekle'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AirportManagement;
