import React from 'react';
import { Link } from 'react-router-dom';

const Home = () => {
    return (
        <div>
            <section className="hero">
                <div className="container">
                    <h1 className="hero-title fade-in">
                        Uçak Bileti Satış ve Yönetim Sistemi
                    </h1>
                    <p className="hero-subtitle fade-in">
                        En uygun fiyatlarla uçak biletinizi hemen satın alın!
                    </p>
                    <div style={{ display: 'flex', gap: '16px', justifyContent: 'center', marginTop: '32px' }}>
                        <Link to="/flights" className="btn btn-primary">
                            Uçuş Ara
                        </Link>
                        <Link to="/register" className="btn btn-secondary">
                            Hemen Kayıt Ol
                        </Link>
                    </div>
                </div>
            </section>

            <section style={{ padding: '60px 0' }}>
                <div className="container">
                    <h2 style={{ textAlign: 'center', fontSize: '36px', marginBottom: '48px' }}>
                        Neden Bizi Seçmelisiniz?
                    </h2>

                    <div className="grid grid-3">
                        <div className="card fade-in">
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>🎫</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Kolay Rezervasyon</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                Birkaç tıklama ile uçak biletinizi satın alın. Hızlı ve güvenli ödeme sistemi.
                            </p>
                        </div>

                        <div className="card fade-in" style={{ animationDelay: '0.1s' }}>
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>💰</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>En İyi Fiyatlar</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                Rekabetçi fiyatlarımız ile bütçenize uygun uçuşları bulun.
                            </p>
                        </div>

                        <div className="card fade-in" style={{ animationDelay: '0.2s' }}>
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>🛡️</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Güvenli Ödeme</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                SSL sertifikalı güvenli ödeme altyapısı ile verileriniz korunur.
                            </p>
                        </div>

                        <div className="card fade-in" style={{ animationDelay: '0.3s' }}>
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>📱</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Mobil Uyumlu</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                Tüm cihazlardan kolayca erişim sağlayın. Responsive tasarım.
                            </p>
                        </div>

                        <div className="card fade-in" style={{ animationDelay: '0.4s' }}>
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>⚡</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Hızlı İşlem</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                Anında onay ve e-bilet gönderimi. Zamanınızı değerli tutuyoruz.
                            </p>
                        </div>

                        <div className="card fade-in" style={{ animationDelay: '0.5s' }}>
                            <div style={{ fontSize: '48px', marginBottom: '16px' }}>🎯</div>
                            <h3 style={{ fontSize: '24px', marginBottom: '12px' }}>Koltuk Seçimi</h3>
                            <p style={{ color: 'var(--text-gray)' }}>
                                İstediğiniz koltuğu seçin. Economy veya Business sınıfı seçenekleri.
                            </p>
                        </div>
                    </div>
                </div>
            </section>

            <section style={{ padding: '60px 0', background: 'var(--dark-card)' }}>
                <div className="container" style={{ textAlign: 'center' }}>
                    <h2 style={{ fontSize: '36px', marginBottom: '24px' }}>
                        Hemen Başlayın!
                    </h2>
                    <p style={{ fontSize: '18px', color: 'var(--text-gray)', marginBottom: '32px' }}>
                        Ücretsiz hesap oluşturun ve uçuşları keşfetmeye başlayın.
                    </p>
                    <Link to="/register" className="btn btn-primary" style={{ fontSize: '18px', padding: '16px 32px' }}>
                        Ücretsiz Kayıt Ol
                    </Link>
                </div>
            </section>
        </div>
    );
};

export default Home;
