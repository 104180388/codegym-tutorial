import React from 'react';
import { Outlet } from 'react-router-dom';
import Header from './Header';
import Footer from './Footer';
import NotificationModal from '../NotificationModal';
import Toast from '../Toast';

/**
 * Layout Component
 * Combines Header, Page Content (Outlet / children), and Footer.
 */
export default function Layout({ children }) {
  return (
    <div className="app-layout">
      {/* 1. Header (Logo, Search, Account, Cart) */}
      <Header />

      {/* 2. Global Notification Modal & Toast */}
      <NotificationModal />
      <Toast />

      {/* 3. Page Content */}
      <main className="main-content">
        {children || <Outlet />}
      </main>

      {/* 4. Footer */}
      <Footer />
    </div>
  );
}
