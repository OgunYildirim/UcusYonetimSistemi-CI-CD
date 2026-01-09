import axios from 'axios';

// Docker container içinde nginx proxy kullanılır (/api -> backend:8080)
// Local development'ta localhost:8081 kullanılır
const API_BASE_URL = process.env.REACT_APP_API_URL ||
  (process.env.NODE_ENV === 'production' ? '/api' : 'http://localhost:8081/api');

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Add token to requests
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Auth Service
export const authService = {
  register: (data) => api.post('/auth/register', data),
  login: (data) => api.post('/auth/login', data),
  logout: () => {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  },
  getCurrentUser: () => {
    const user = localStorage.getItem('user');
    return user ? JSON.parse(user) : null;
  },
  isAuthenticated: () => {
    return !!localStorage.getItem('token');
  },
  isAdmin: () => {
    const user = authService.getCurrentUser();
    return user && user.roles && user.roles.includes('ROLE_ADMIN');
  },
};

// Flight Service
export const flightService = {
  getAllFlights: () => api.get('/flights/all'),
  searchFlights: (departureAirportId, arrivalAirportId, departureDate) =>
    api.get('/flights/search', {
      params: { departureAirportId, arrivalAirportId, departureDate },
    }),
  getFlightById: (id) => api.get(`/flights/${id}`),
  createFlight: (data) => api.post('/flights', data),
  updateFlight: (id, data) => api.put(`/flights/${id}`, data),
  deleteFlight: (id) => api.delete(`/flights/${id}`),
};

// Airport Service
export const airportService = {
  getAllAirports: () => api.get('/airports'),
  getAirportById: (id) => api.get(`/airports/${id}`),
};

// Booking Service
export const bookingService = {
  createBooking: (data) => api.post('/bookings', data),
  getUserBookings: () => api.get('/bookings/my-bookings'),
  getBookingsByFlight: (flightId) => api.get(`/bookings/flight/${flightId}`),
  cancelBooking: (id) => api.put(`/bookings/${id}/cancel`),
  autoAssignSeat: (ticketId, seatClass) => api.post(`/bookings/auto-assign-seat/${ticketId}`, { seatClass }),
};

// Admin Services
export const adminService = {
  // Airports
  createAirport: (data) => api.post('/admin/airports', data),
  updateAirport: (id, data) => api.put(`/admin/airports/${id}`, data),
  deleteAirport: (id) => api.delete(`/admin/airports/${id}`),

  // Aircrafts
  getAllAircrafts: () => api.get('/admin/aircrafts'),
  createAircraft: (data) => api.post('/admin/aircrafts', data),
  updateAircraft: (id, data) => api.put(`/admin/aircrafts/${id}`, data),
  deleteAircraft: (id) => api.delete(`/admin/aircrafts/${id}`),

  // Maintenance
  getAllMaintenance: () => api.get('/admin/maintenance'),
  getMaintenanceByAircraft: (aircraftId) => api.get(`/admin/maintenance/aircraft/${aircraftId}`),
  createMaintenance: (data) => api.post('/admin/maintenance', data),
  updateMaintenance: (id, data) => api.put(`/admin/maintenance/${id}`, data),

  // Pricing
  getAllPricing: () => api.get('/admin/pricing'),
  getPricingByFlight: (flightId) => api.get(`/admin/pricing/flight/${flightId}`),
  createPricing: (data) => api.post('/admin/pricing', data),
  updatePricing: (id, data) => api.put(`/admin/pricing/${id}`, data),

  // Seats
  getSeatsByAircraft: (aircraftId) => api.get(`/admin/seats/aircraft/${aircraftId}`),
  createSeat: (data) => api.post('/admin/seats', data),
  updateSeat: (id, data) => api.put(`/admin/seats/${id}`, data),
  deleteSeat: (id) => api.delete(`/admin/seats/${id}`),
  generateSeats: (aircraftId) => api.post(`/admin/seats/generate/${aircraftId}`)
};

export default api;
