import React from "react";
import { Routes, Route, Link } from "react-router-dom";
import Home from "./components/Hone";
import About from "./components/About";
import Contact from "./components/Contact";
import "./App.css";

function App() {
    return (
        <div className="container">
            {/* Navigation menu */}
            <nav>
                <ul>
                    <li><Link to="/">Home</Link></li>
                    <li><Link to="/about">About</Link></li>
                    <li><Link to="/contact">Contact</Link></li>
                </ul>
            </nav>

            {/* Route definitions */}
            <Routes>
                <Route path="/" element={<Home />} />
                <Route path="/about" element={<About />} />
                <Route path="/contact" element={<Contact />} />
            </Routes>
        </div>
    );
}

export default App;