import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { authService } from '../services/api';

const Login = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        username: '',
        password: '',
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
            const response = await authService.login(formData);
            const { token, id, username, email, roles } = response.data;

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify({ id, username, email, roles }));

            navigate('/flights');
        } catch (err) {
            setError(err.response?.data?.message || 'Giriş başarısız. Lütfen bilgilerinizi kontrol edin.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="container" style={{ maxWidth: '500px', marginTop: '80px' }}>
            <div className="card fade-in">
                <h2 style={{ fontSize: '32px', marginBottom: '8px', textAlign: 'center' }}>
                    Giriş Yap
                </h2>
                <p style={{ color: 'var(--text-gray)', textAlign: 'center', marginBottom: '32px' }}>
                    Hesabınıza giriş yapın
                </p>

                {error && (
                    <div className="alert alert-error">
                        {error}
                    </div>
                )}

                <form onSubmit={handleSubmit}>
                    <div className="form-group">
                        <label className="form-label">Kullanıcı Adı</label>
                        <input
                            type="text"
                            name="username"
                            className="form-input"
                            value={formData.username}
                            onChange={handleChange}
                            required
                            placeholder="Kullanıcı adınızı girin"
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
                            placeholder="Şifrenizi girin"
                        />
                    </div>

                    <button type="submit" className="btn btn-primary" style={{ width: '100%' }} disabled={loading}>
                        {loading ? 'Giriş yapılıyor...' : 'Giriş Yap'}
                    </button>
                </form>

                <p style={{ textAlign: 'center', marginTop: '24px', color: 'var(--text-gray)' }}>
                    Hesabınız yok mu?{' '}
                    <Link to="/register" style={{ color: 'var(--primary-light)', textDecoration: 'none' }}>
                        Kayıt Ol
                    </Link>
                </p>
            </div>
        </div>
    );
};

export default Login;
