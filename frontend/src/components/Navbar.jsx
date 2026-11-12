import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useTheme } from '../context/ThemeContext';
import { chatApi, notificationApi } from '../services/api';
import { 
  Compass, 
  Rocket, 
  Sparkles, 
  MessageSquare, 
  Bell, 
  User, 
  LogOut, 
  LogIn, 
  Sun, 
  Moon 
} from 'lucide-react';

export default function Navbar({ activeTab, setActiveTab, onOpenAuth }) {
  const { user, isAuthenticated, logout } = useAuth();
  const { theme, isDark, toggleTheme } = useTheme();
  const [unreadMsgCount, setUnreadMsgCount] = useState(0);
  const [unreadNotifCount, setUnreadNotifCount] = useState(0);

  useEffect(() => {
    if (!isAuthenticated) return;

    const fetchCounts = async () => {
      try {
        const chatRes = await chatApi.getUnreadCount();
        if (chatRes && chatRes.unreadCount !== undefined) {
          setUnreadMsgCount(chatRes.unreadCount);
        }
        const notifRes = await notificationApi.getUnreadCount();
        if (notifRes && notifRes.unreadCount !== undefined) {
          setUnreadNotifCount(notifRes.unreadCount);
        }
      } catch (err) {
        // quiet fallback
      }
    };

    fetchCounts();
    const interval = setInterval(fetchCounts, 8000);
    return () => clearInterval(interval);
  }, [isAuthenticated]);

  return (
    <nav className="navbar">
      <div className="nav-inner">
        <a href="#" className="brand" onClick={(e) => { e.preventDefault(); setActiveTab('discovery'); }}>
          <img 
            src="/logo.png" 
            alt="FounderLink Logo" 
            className="brand-logo-img" 
          />
          <span className="brand-text">FounderLink</span>
        </a>

        <div className="nav-links">
          <button 
            className={`nav-btn ${activeTab === 'discovery' ? 'active' : ''}`}
            onClick={() => setActiveTab('discovery')}
          >
            <Compass size={17} />
            <span>Discover</span>
          </button>

          <button 
            className={`nav-btn ${activeTab === 'startups' ? 'active' : ''}`}
            onClick={() => setActiveTab('startups')}
          >
            <Rocket size={17} />
            <span>Startups</span>
          </button>

          <button 
            className={`nav-btn ${activeTab === 'matching' ? 'active' : ''}`}
            onClick={() => setActiveTab('matching')}
          >
            <Sparkles size={17} />
            <span>AI Matches</span>
          </button>

          <button 
            className={`nav-btn ${activeTab === 'chat' ? 'active' : ''}`}
            onClick={() => setActiveTab('chat')}
          >
            <MessageSquare size={17} />
            <span>Chat</span>
            {unreadMsgCount > 0 && <span className="badge-count">{unreadMsgCount}</span>}
          </button>

          <button 
            className={`nav-btn ${activeTab === 'notifications' ? 'active' : ''}`}
            onClick={() => setActiveTab('notifications')}
          >
            <Bell size={17} />
            <span>Alerts</span>
            {unreadNotifCount > 0 && <span className="badge-count">{unreadNotifCount}</span>}
          </button>

          <button 
            className={`nav-btn ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            <User size={17} />
            <span>Profile</span>
          </button>
        </div>

        <div className="nav-user">
          {/* Day / Night Theme Toggle Button */}
          <button 
            className="theme-toggle-btn"
            onClick={toggleTheme}
            title={isDark ? "Switch to Day mode (Light)" : "Switch to Night mode (Dark)"}
            aria-label="Toggle day and night theme"
          >
            <div className="theme-toggle-icon">
              {isDark ? (
                <Sun size={18} className="icon-sun" />
              ) : (
                <Moon size={18} className="icon-moon" />
              )}
            </div>
            <span className="theme-toggle-label">{isDark ? 'Day' : 'Night'}</span>
          </button>

          {isAuthenticated ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <span className="nav-user-email">
                {user?.email}
              </span>
              <button className="btn btn-secondary btn-sm" onClick={logout}>
                <LogOut size={15} />
                <span>Sign Out</span>
              </button>
            </div>
          ) : (
            <button className="btn btn-primary btn-sm" onClick={onOpenAuth}>
              <LogIn size={15} />
              <span>Sign In / Join</span>
            </button>
          )}
        </div>
      </div>
    </nav>
  );
}
