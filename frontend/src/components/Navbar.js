import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { authService } from '../services/api';

const Navbar = () => {
    const navigate = useNavigate();
    const isAuthenticated = authService.isAuthenticated();
    const isAdmin = authService.isAdmin();
    const user = authService.getCurrentUser();

    const handleLogout = () => {
        authService.logout();
        navigate('/login');
    };

    return (
        <nav className="navbar">
            <div className="container">
                <div className="navbar-content">
                    <Link to="/" className="navbar-brand">
                        ✈️ Flight Management
                    </Link>

                    <ul className="navbar-menu">
                        <li>
                            <Link to="/" className="navbar-link">Ana Sayfa</Link>
                        </li>
                        <li>
                            <Link to="/flights" className="navbar-link">Uçuşlar</Link>
                        </li>

                        {isAuthenticated ? (
                            <>
                                <li>
                                    <Link to="/my-bookings" className="navbar-link">Biletlerim</Link>
                                </li>
                                {isAdmin && (
                                    <li>
                                        <Link to="/admin" className="navbar-link">Admin Panel</Link>
                                    </li>
                                )}
                                <li>
                                    <span className="navbar-link" style={{ color: 'var(--primary-light)' }}>
                                        {user?.username}
                                    </span>
                                </li>
                                <li>
                                    <button onClick={handleLogout} className="btn btn-secondary">
                                        Çıkış Yap
                                    </button>
                                </li>
                            </>
                        ) : (
                            <>
                                <li>
                                    <Link to="/login" className="btn btn-primary">Giriş Yap</Link>
                                </li>
                                <li>
                                    <Link to="/register" className="btn btn-secondary">Kayıt Ol</Link>
                                </li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </nav>
    );
};

export default Navbar;
