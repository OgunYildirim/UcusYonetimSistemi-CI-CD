import React, { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { authService } from '../services/api';
import FlightManagement from '../components/admin/FlightManagement';
import AircraftManagement from '../components/admin/AircraftManagement';
import AirportManagement from '../components/admin/AirportManagement';
import PricingManagement from '../components/admin/PricingManagement';
import SeatManagement from '../components/admin/SeatManagement';
import SeatAssignmentManagement from '../components/admin/SeatAssignmentManagement';

const AdminPanel = () => {
    const [activeTab, setActiveTab] = useState('flights');

    if (!authService.isAdmin()) {
        return <Navigate to="/" />;
    }

    const tabs = [
        { id: 'flights', label: '✈️ Uçuşlar', component: FlightManagement },
        { id: 'aircrafts', label: '🛩️ Uçaklar', component: AircraftManagement },
        { id: 'airports', label: '🏢 Havalimanları', component: AirportManagement },
        { id: 'pricing', label: '💰 Fiyatlandırma', component: PricingManagement },
        { id: 'seats', label: '💺 Koltuklar', component: SeatManagement },
        { id: 'seat-assignment', label: '🎫 Koltuk Atama', component: SeatAssignmentManagement },
    ];

    const ActiveComponent = tabs.find(tab => tab.id === activeTab)?.component;

    return (
        <div className="container" style={{ marginTop: '40px', marginBottom: '80px' }}>
            <h1 style={{ fontSize: '36px', marginBottom: '32px' }}>Admin Panel</h1>

            <div style={{ marginBottom: '32px' }}>
                <div style={{
                    display: 'flex',
                    gap: '16px',
                    borderBottom: '2px solid var(--dark-border)',
                    overflowX: 'auto'
                }}>
                    {tabs.map(tab => (
                        <button
                            key={tab.id}
                            onClick={() => setActiveTab(tab.id)}
                            style={{
                                padding: '16px 24px',
                                background: activeTab === tab.id ? 'var(--gradient-primary)' : 'transparent',
                                border: 'none',
                                borderBottom: activeTab === tab.id ? '3px solid var(--primary-color)' : '3px solid transparent',
                                color: 'var(--text-light)',
                                fontSize: '16px',
                                fontWeight: '600',
                                cursor: 'pointer',
                                transition: 'all 0.3s ease',
                                borderRadius: '8px 8px 0 0',
                                whiteSpace: 'nowrap'
                            }}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
            </div>

            <div>
                {ActiveComponent && <ActiveComponent />}
            </div>
        </div>
    );
};

export default AdminPanel;
