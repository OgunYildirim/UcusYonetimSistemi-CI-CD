import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import Home from './pages/Home';
import Login from './pages/Login';
import Register from './pages/Register';
import Flights from './pages/Flights';
import BookFlight from './pages/BookFlight';
import MyBookings from './pages/MyBookings';
import AdminPanel from './pages/AdminPanel';

function App() {
    return (
        <Router>
            <div className="App">
                <Navbar />
                <Routes>
                    <Route path="/" element={<Home />} />
                    <Route path="/login" element={<Login />} />
                    <Route path="/register" element={<Register />} />
                    <Route path="/flights" element={<Flights />} />
                    <Route path="/book/:flightId" element={<BookFlight />} />
                    <Route path="/my-bookings" element={<MyBookings />} />
                    <Route path="/admin" element={<AdminPanel />} />
                </Routes>
            </div>
        </Router>
    );
}

export default App;
    