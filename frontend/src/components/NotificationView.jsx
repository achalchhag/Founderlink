import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { notificationApi } from '../services/api';
import { 
  Bell, 
  CheckCheck, 
  Trash2, 
  Sparkles, 
  MessageSquare, 
  Rocket, 
  Info,
  CheckCircle2 
} from 'lucide-react';

export default function NotificationView({ onOpenAuth }) {
  const { isAuthenticated } = useAuth();
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState('');

  const loadNotifications = async () => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const data = await notificationApi.getMyNotifications();
      setNotifications(data || []);
    } catch (err) {
      setNotifications([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadNotifications();
  }, [isAuthenticated]);

  const handleMarkAsRead = async (id) => {
    try {
      const updated = await notificationApi.markAsRead(id);
      setNotifications(notifications.map((n) => (n.id === id ? updated : n)));
    } catch (err) {
      // quiet fallback
    }
  };

  const handleMarkAllRead = async () => {
    try {
      await notificationApi.markAllAsRead();
      setNotifications(notifications.map((n) => ({ ...n, read: true })));
      setMessage('All notifications marked as read');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      // quiet fallback
    }
  };

  const handleDelete = async (id) => {
    try {
      await notificationApi.delete(id);
      setNotifications(notifications.filter((n) => n.id !== id));
    } catch (err) {
      // quiet fallback
    }
  };

  const getTypeIcon = (type) => {
    switch (type) {
      case 'MATCH':
        return <Sparkles size={18} color="var(--accent-cyan)" />;
      case 'MESSAGE':
        return <MessageSquare size={18} color="var(--primary)" />;
      case 'STARTUP':
        return <Rocket size={18} color="var(--accent-amber)" />;
      default:
        return <Info size={18} color="var(--text-muted)" />;
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
        <Bell size={48} color="var(--primary)" style={{ margin: '0 auto 16px' }} />
        <h2>Founder Activity & Alerts</h2>
        <p style={{ color: 'var(--text-muted)', margin: '12px 0 24px' }}>
          Sign in to receive instant notifications on co-founder interest, matches, and messages.
        </p>
        <button className="btn btn-primary" onClick={onOpenAuth}>
          Sign In / Join
        </button>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="card-header" style={{ marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Notification Center</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem' }}>
            System updates, connection requests, and AI matching alerts.
          </p>
        </div>

        {notifications.length > 0 && (
          <button className="btn btn-secondary btn-sm" onClick={handleMarkAllRead}>
            <CheckCheck size={16} />
            <span>Mark All Read</span>
          </button>
        )}
      </div>

      {message && (
        <div style={{ padding: '12px 16px', background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', borderRadius: 'var(--radius-md)', color: '#6ee7b7', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <CheckCircle2 size={18} />
          <span>{message}</span>
        </div>
      )}

      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>
          Loading notifications...
        </div>
      ) : notifications.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Bell size={48} color="var(--text-dim)" style={{ margin: '0 auto 16px' }} />
          <h3>You’re all caught up!</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px' }}>
            No unread alerts or notifications at this time.
          </p>
        </div>
      ) : (
        <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
          {notifications.map((notif) => (
            <div 
              key={notif.id} 
              className="card"
              style={{
                padding: '16px 20px',
                background: notif.read ? 'rgba(22, 30, 50, 0.4)' : 'rgba(28, 38, 65, 0.85)',
                borderColor: notif.read ? 'var(--border-color)' : 'rgba(99, 102, 241, 0.35)',
                display: 'flex',
                alignItems: 'flex-start',
                gap: '16px'
              }}
            >
              <div style={{ width: '36px', height: '36px', borderRadius: 'var(--radius-md)', background: 'rgba(255, 255, 255, 0.05)', display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                {getTypeIcon(notif.type)}
              </div>

              <div style={{ flex: 1 }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                  <h4 style={{ fontSize: '1rem', fontWeight: 700, color: notif.read ? 'var(--text-main)' : '#fff' }}>
                    {notif.title}
                  </h4>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-dim)' }}>
                    {new Date(notif.createdAt || Date.now()).toLocaleDateString([], { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })}
                  </span>
                </div>

                <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginTop: '4px', lineHeight: 1.4 }}>
                  {notif.message}
                </p>

                <div style={{ display: 'flex', gap: '12px', marginTop: '10px' }}>
                  {!notif.read && (
                    <button 
                      className="btn btn-secondary btn-sm"
                      onClick={() => handleMarkAsRead(notif.id)}
                      style={{ fontSize: '0.76rem', padding: '4px 10px' }}
                    >
                      Mark as read
                    </button>
                  )}
                  <button 
                    className="btn btn-danger btn-sm btn-icon"
                    onClick={() => handleDelete(notif.id)}
                    title="Delete notification"
                    style={{ padding: '4px 8px' }}
                  >
                    <Trash2 size={13} />
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
