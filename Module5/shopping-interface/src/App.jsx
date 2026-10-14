import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Layout from './components/layout/Layout';
import HomePage from './pages/HomePage';
import ProductDetailPage from './pages/ProductDetailPage';
import CartPage from './pages/CartPage';
import CheckoutPage from './pages/CheckoutPage';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Layout />}>
        {/* 1. HomePage */}
        <Route index element={<HomePage />} />

        {/* 2. ProductDetailPage */}
        <Route path="product/:id" element={<ProductDetailPage />} />

        {/* 3. CartPage */}
        <Route path="cart" element={<CartPage />} />

        {/* 4. CheckoutPage */}
        <Route path="checkout" element={<CheckoutPage />} />
      </Route>
    </Routes>
  );
}
