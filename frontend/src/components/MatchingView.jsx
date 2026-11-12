import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { matchingApi, notificationApi } from '../services/api';
import { 
  Sparkles, 
  Check, 
  X, 
  MessageSquare, 
  RefreshCw, 
  TrendingUp, 
  Briefcase, 
  UserCheck 
} from 'lucide-react';

export default function MatchingView({ onOpenAuth, onSelectChatUser }) {
  const { isAuthenticated } = useAuth();
  const [matches, setMatches] = useState([]);
  const [loading, setLoading] = useState(true);
  const [generating, setGenerating] = useState(false);
  const [activeFilter, setActiveFilter] = useState('ALL'); // ALL, FOUNDER, STARTUP

  const loadMatches = async (typeFilter = activeFilter) => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const queryType = typeFilter === 'ALL' ? '' : typeFilter;
      const data = await matchingApi.getMyMatches(queryType);
      setMatches(data || []);
    } catch (err) {
      setMatches([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMatches(activeFilter);
  }, [isAuthenticated, activeFilter]);

  const handleGenerate = async () => {
    try {
      setGenerating(true);
      const generated = await matchingApi.generate('Founder', 'Java, React, AI');
      setMatches(generated || []);
      // also send a test notification
      await notificationApi.create({
        userId: generated[0]?.userId,
        title: 'New AI Synergy Match Calculated',
        message: 'Your recommendations have been refreshed based on your skill synergy profile.',
        type: 'MATCH'
      }).catch(() => null);
    } catch (err) {
      // quiet fallback
    } finally {
      setGenerating(false);
    }
  };

  const handleStatusUpdate = async (matchId, newStatus) => {
    try {
      const updated = await matchingApi.updateStatus(matchId, newStatus);
      setMatches(matches.map((m) => (m.id === matchId ? updated : m)));
    } catch (err) {
      // quiet fallback
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
        <Sparkles size={48} color="var(--accent-cyan)" style={{ margin: '0 auto 16px' }} />
        <h2>AI Co-Founder & Startup Synergy Engine</h2>
        <p style={{ color: 'var(--text-muted)', margin: '12px 0 24px' }}>
          Sign in to analyze role complementarity, skill overlaps, and get match scores.
        </p>
        <button className="btn btn-primary" onClick={onOpenAuth}>
          Sign In / Join
        </button>
      </div>
    );
  }

  return (
    <div>
      {/* Header */}
      <div className="card-header" style={{ marginBottom: '24px' }}>
        <div>
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>AI Match Recommendations</h1>
            <span className="badge badge-cyan">
              <Sparkles size={12} />
              Algorithm v2.0
            </span>
          </div>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem', marginTop: '4px' }}>
            Curated synergy recommendations based on complementary skill sets and startup focus.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '10px' }}>
          <button 
            className="btn btn-primary"
            onClick={handleGenerate}
            disabled={generating}
          >
            <RefreshCw size={15} className={generating ? 'spin' : ''} />
            <span>{generating ? 'Calculating...' : 'Recalculate Matches'}</span>
          </button>
        </div>
      </div>

      {/* Filter Tabs */}
      <div style={{ display: 'flex', gap: '8px', marginBottom: '24px' }}>
        <button 
          className={`btn btn-sm ${activeFilter === 'ALL' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveFilter('ALL')}
        >
          All Matches ({matches.length})
        </button>
        <button 
          className={`btn btn-sm ${activeFilter === 'FOUNDER' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveFilter('FOUNDER')}
        >
          Founders
        </button>
        <button 
          className={`btn btn-sm ${activeFilter === 'STARTUP' ? 'btn-primary' : 'btn-secondary'}`}
          onClick={() => setActiveFilter('STARTUP')}
        >
          Startups
        </button>
      </div>

      {/* Match Cards */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>
          Computing synergy scores...
        </div>
      ) : matches.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Sparkles size={48} color="var(--text-dim)" style={{ margin: '0 auto 16px' }} />
          <h3>No match records found</h3>
          <p style={{ color: 'var(--text-muted)', margin: '8px 0 20px' }}>
            Click below to calculate curated AI matches for your profile.
          </p>
          <button className="btn btn-primary" onClick={handleGenerate}>
            Generate AI Matches
          </button>
        </div>
      ) : (
        <div className="grid-2">
          {matches.map((match) => (
            <div key={match.id} className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <div>
                  <span className={`badge ${match.matchType === 'STARTUP' ? 'badge-amber' : 'badge-indigo'}`} style={{ marginBottom: '6px' }}>
                    {match.matchType}
                  </span>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700 }}>
                    {match.targetName}
                  </h3>
                  {match.targetRole && (
                    <div style={{ fontSize: '0.86rem', color: 'var(--text-muted)', marginTop: '2px' }}>
                      {match.targetRole}
                    </div>
                  )}
                </div>

                {/* Match Score Badge */}
                <div style={{ textAlign: 'right' }}>
                  <div style={{ fontSize: '1.35rem', fontWeight: 800, color: match.matchScore >= 85 ? '#34d399' : '#60a5fa' }}>
                    {match.matchScore}%
                  </div>
                  <div style={{ fontSize: '0.72rem', color: 'var(--text-dim)', textTransform: 'uppercase', letterSpacing: '0.04em' }}>
                    Synergy Match
                  </div>
                </div>
              </div>

              {/* Progress Synergy bar */}
              <div className="synergy-bar">
                <div className="synergy-fill" style={{ width: `${match.matchScore}%` }} />
              </div>

              {match.targetBio && (
                <p style={{ fontSize: '0.88rem', color: 'var(--text-muted)', margin: '14px 0 8px', lineHeight: 1.5 }}>
                  {match.targetBio}
                </p>
              )}

              {match.synergySummary && (
                <div style={{ background: 'rgba(99, 102, 241, 0.08)', border: '1px solid rgba(99, 102, 241, 0.2)', padding: '10px 14px', borderRadius: 'var(--radius-md)', margin: '10px 0', fontSize: '0.85rem', color: '#c7d2fe', lineHeight: 1.4 }}>
                  <span style={{ fontWeight: 700, display: 'flex', alignItems: 'center', gap: '4px', marginBottom: '2px' }}>
                    <TrendingUp size={13} /> Synergy Analysis:
                  </span>
                  {match.synergySummary}
                </div>
              )}

              {/* Status and Action Buttons */}
              <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: '10px', marginTop: 'auto', paddingTop: '14px', borderTop: '1px solid var(--border-color)' }}>
                <div>
                  <span className={`badge ${match.status === 'ACCEPTED' ? 'badge-emerald' : match.status === 'REJECTED' ? 'badge-rose' : 'badge-indigo'}`}>
                    {match.status}
                  </span>
                </div>

                <div style={{ display: 'flex', gap: '8px' }}>
                  {match.status !== 'ACCEPTED' && (
                    <button 
                      className="btn btn-success btn-sm btn-icon"
                      title="Accept & Connect"
                      onClick={() => handleStatusUpdate(match.id, 'ACCEPTED')}
                    >
                      <Check size={16} />
                    </button>
                  )}
                  {match.status !== 'REJECTED' && (
                    <button 
                      className="btn btn-danger btn-sm btn-icon"
                      title="Decline"
                      onClick={() => handleStatusUpdate(match.id, 'REJECTED')}
                    >
                      <X size={16} />
                    </button>
                  )}
                  <button 
                    className="btn btn-primary btn-sm"
                    onClick={() => onSelectChatUser(match.targetId, match.targetName)}
                  >
                    <MessageSquare size={14} />
                    <span>Chat</span>
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
