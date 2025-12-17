import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';

const Register = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        firstName: '',
        lastName: '',
        phoneNumber: '',
    });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value,
        });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
            await authService.register(formData);
            navigate('/login');
        } catch (err) {
            setError(err.response?.data?.message || 'Kayıt başarısız. Lütfen bilgilerinizi kontrol edin.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container" style={{ maxWidth: '600px', marginTop: '80px', marginBottom: '80px' }}>
            <div className="card fade-in">
                <h2 style={{ fontSize: '32px', marginBottom: '8px', textAlign: 'center' }}>
                    Kayıt Ol
                </h2>
                <p style={{ color: 'var(--text-gray)', textAlign: 'center', marginBottom: '32px' }}>
                    Yeni hesap oluşturun
                </p>

                {error && (
                    <div className="alert alert-error">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="grid grid-2">
                        <div className="form-group">
                            <label className="form-label">Ad</label>
                            <input
                                type="text"
                                name="firstName"
                                className="form-input"
                                value={formData.firstName}
                                onChange={handleChange}
                                required
                                placeholder="Adınız"
                            />
                        </div>

                        <div className="form-group">
                            <label className="form-label">Soyad</label>
                            <input
                                type="text"
                                name="lastName"
                                className="form-input"
                                value={formData.lastName}
                                onChange={handleChange}
                                required
                                placeholder="Soyadınız"
                            />
                        </div>
                    </div>

                    <div className="form-group">
                        <label className="form-label">Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="username"
                            className="form-input"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            placeholder="Kullanıcı adı seçin"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">E-posta</label>
                        <input
                            type="email"
                            name="email"
                            className="form-input"
                            value={formData.email}
                            onChange={handleChange}
                            required
                            placeholder="ornek@email.com"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Telefon</label>
                        <input
                            type="tel"
                            name="phoneNumber"
                            className="form-input"
                            value={formData.phoneNumber}
                            onChange={handleChange}
                            placeholder="05XX XXX XX XX"
                        />
                    </div>

                    <div className="form-group">
                        <label className="form-label">Şifre</label>
                        <input
                            type="password"
                            name="password"
                            className="form-input"
                            value={formData.password}
                            onChange={handleChange}
                            required
                            minLength="6"
                            placeholder="En az 6 karakter"
                        />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={loading}>
                        {loading ? 'Kayıt yapılıyor...' : 'Kayıt Ol'}
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '24px', color: 'var(--text-gray)' }}>
                    Zaten hesabınız var mı?{' '}
                    <Link to="/login" style={{ color: 'var(--primary-light)', textDecoration: 'none' }}>
                        Giriş Yap
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Register;
