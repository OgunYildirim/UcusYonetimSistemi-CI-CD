import React, { useState, useEffect } from 'react';
import { adminService } from '../../services/api';

const AircraftManagement = () => {
    const [aircrafts, setAircrafts] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [editingAircraft, setEditingAircraft] = useState(null);
    const [formData, setFormData] = useState({
        registrationNumber: '',
        model: '',
        manufacturer: '',
        totalSeats: '',
        economySeats: '',
        businessSeats: '',
        yearOfManufacture: '',
        active: true,
        underMaintenance: false
    });

    useEffect(() => {
        loadAircrafts();
    }, []);

    const loadAircrafts = async () => {
        try {
            const response = await adminService.getAllAircrafts();
            setAircrafts(response.data);
        } catch (error) {
            console.error('Error loading aircrafts:', error);
            alert('Uçaklar yüklenirken hata oluştu');
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
            if (editingAircraft) {
                await adminService.updateAircraft(editingAircraft.id, formData);
                alert('Uçak başarıyla güncellendi!');
            } else {
                await adminService.createAircraft(formData);
                alert('Uçak başarıyla eklendi!');
            }
            setShowModal(false);
            resetForm();
            loadAircrafts();
        } catch (error) {
            console.error('Error saving aircraft:', error);
            alert('Uçak kaydedilirken hata oluştu: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleEdit = (aircraft) => {
        setEditingAircraft(aircraft);
        setFormData(aircraft);
        setShowModal(true);
    };

    const handleDelete = async (id) => {
        if (window.confirm('Bu uçağı silmek istediğinizden emin misiniz?')) {
            try {
                await adminService.deleteAircraft(id);
                alert('Uçak başarıyla silindi!');
                loadAircrafts();
            } catch (error) {
                console.error('Error deleting aircraft:', error);
                alert('Uçak silinirken hata oluştu: ' + (error.response?.data?.message || error.message));
            }
        }
    };

    const toggleMaintenance = async (aircraft) => {
        try {
            await adminService.updateAircraft(aircraft.id, {
                ...aircraft,
                underMaintenance: !aircraft.underMaintenance
            });
            alert('Bakım durumu güncellendi!');
            loadAircrafts();
        } catch (error) {
            console.error('Error updating maintenance status:', error);
            alert('Bakım durumu güncellenirken hata oluştu');
        }
    };

    const resetForm = () => {
        setFormData({
            registrationNumber: '',
            model: '',
            manufacturer: '',
            totalSeats: '',
            economySeats: '',
            businessSeats: '',
            yearOfManufacture: '',
            active: true,
            underMaintenance: false
        });
        setEditingAircraft(null);
    };

    const handleAddNew = () => {
        resetForm();
        setShowModal(true);
    };

    return (
        <div>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
                <h2 style={{ fontSize: '24px', margin: 0 }}>🛩️ Uçak Yönetimi</h2>
                <button onClick={handleAddNew} className="btn btn-primary">
                    + Yeni Uçak Ekle
                </button>
            </div>

            <div className="card">
                <div style={{ overflowX: 'auto' }}>
                    <table className="table">
                        <thead>
                            <tr>
                                <th>Kayıt No</th>
                                <th>Model</th>
                                <th>Üretici</th>
                                <th>Yıl</th>
                                <th>Toplam Koltuk</th>
                                <th>Economy</th>
                                <th>Business</th>
                                <th>Durum</th>
                                <th>İşlemler</th>
                            </tr>
                        </thead>
                        <tbody>
                            {aircrafts.length === 0 ? (
                                <tr>
                                    <td colSpan="9" style={{ textAlign: 'center', padding: '40px', color: 'var(--text-gray)' }}>
                                        Henüz uçak eklenmemiş
                                    </td>
                                </tr>
                            ) : (
                                aircrafts.map(aircraft => (
                                    <tr key={aircraft.id}>
                                        <td><strong>{aircraft.registrationNumber}</strong></td>
                                        <td>{aircraft.model}</td>
                                        <td>{aircraft.manufacturer}</td>
                                        <td>{aircraft.yearOfManufacture}</td>
                                        <td>{aircraft.totalSeats}</td>
                                        <td>{aircraft.economySeats}</td>
                                        <td>{aircraft.businessSeats}</td>
                                        <td>
                                            {aircraft.underMaintenance ? (
                                                <span className="flight-status status-cancelled">🔧 Bakımda</span>
                                            ) : (
                                                <span className="flight-status status-scheduled">✅ Aktif</span>
                                            )}
                                        </td>
                                        <td>
                                            <div style={{ display: 'flex', gap: '8px' }}>
                                                <button
                                                    onClick={() => handleEdit(aircraft)}
                                                    className="btn btn-secondary"
                                                    style={{ padding: '6px 12px', fontSize: '14px' }}
                                                >
                                                    ✏️ Düzenle
                                                </button>
                                                <button
                                                    onClick={() => toggleMaintenance(aircraft)}
                                                    className="btn"
                                                    style={{
                                                        padding: '6px 12px',
                                                        fontSize: '14px',
                                                        background: aircraft.underMaintenance ? 'var(--success-color)' : 'var(--warning-color)'
                                                    }}
                                                >
                                                    {aircraft.underMaintenance ? '✅ Aktif Et' : '🔧 Bakıma Al'}
                                                </button>
                                                <button
                                                    onClick={() => handleDelete(aircraft.id)}
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
                            {editingAircraft ? '✏️ Uçak Düzenle' : '➕ Yeni Uçak Ekle'}
                        </h3>
                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label className="form-label">Kayıt Numarası *</label>
                                <input
                                    type="text"
                                    name="registrationNumber"
                                    className="form-input"
                                    value={formData.registrationNumber}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Örn: TC-JRO"
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Model *</label>
                                <input
                                    type="text"
                                    name="model"
                                    className="form-input"
                                    value={formData.model}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Örn: Boeing 737-800"
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Üretici *</label>
                                <input
                                    type="text"
                                    name="manufacturer"
                                    className="form-input"
                                    value={formData.manufacturer}
                                    onChange={handleInputChange}
                                    required
                                    placeholder="Örn: Boeing"
                                />
                            </div>

                            <div className="form-group">
                                <label className="form-label">Üretim Yılı *</label>
                                <input
                                    type="number"
                                    name="yearOfManufacture"
                                    className="form-input"
                                    value={formData.yearOfManufacture}
                                    onChange={handleInputChange}
                                    required
                                    min="1950"
                                    max={new Date().getFullYear()}
                                />
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
                                <div className="form-group">
                                    <label className="form-label">Toplam Koltuk *</label>
                                    <input
                                        type="number"
                                        name="totalSeats"
                                        className="form-input"
                                        value={formData.totalSeats}
                                        onChange={handleInputChange}
                                        required
                                        min="1"
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Economy *</label>
                                    <input
                                        type="number"
                                        name="economySeats"
                                        className="form-input"
                                        value={formData.economySeats}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                    />
                                </div>

                                <div className="form-group">
                                    <label className="form-label">Business *</label>
                                    <input
                                        type="number"
                                        name="businessSeats"
                                        className="form-input"
                                        value={formData.businessSeats}
                                        onChange={handleInputChange}
                                        required
                                        min="0"
                                    />
                                </div>
                            </div>

                            <div style={{ display: 'flex', gap: '16px', marginBottom: '24px' }}>
                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        name="active"
                                        checked={formData.active}
                                        onChange={handleInputChange}
                                    />
                                    <span>Aktif</span>
                                </label>

                                <label style={{ display: 'flex', alignItems: 'center', gap: '8px', cursor: 'pointer' }}>
                                    <input
                                        type="checkbox"
                                        name="underMaintenance"
                                        checked={formData.underMaintenance}
                                        onChange={handleInputChange}
                                    />
                                    <span>Bakımda</span>
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
                                    {editingAircraft ? '💾 Güncelle' : '➕ Ekle'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
};

export default AircraftManagement;
