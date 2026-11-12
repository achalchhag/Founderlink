import React, { useState, useEffect } from 'react';
import { discoveryApi } from '../services/api';
import { 
  Search, 
  MapPin, 
  Briefcase, 
  MessageSquare, 
  Sparkles, 
  ExternalLink,
  Users,
  Filter
} from 'lucide-react';

export default function DiscoveryView({ onSelectChatUser, onGenerateMatch }) {
  const [founders, setFounders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filters, setFilters] = useState({
    location: '',
    skill: '',
    headline: ''
  });

  const fetchFounders = async (currentFilters = filters) => {
    try {
      setLoading(true);
      const params = {};
      if (currentFilters.location) params.location = currentFilters.location;
      if (currentFilters.skill) params.skill = currentFilters.skill;
      if (currentFilters.headline) params.headline = currentFilters.headline;

      const data = await discoveryApi.findFounders(params);
      setFounders(data || []);
    } catch (err) {
      setFounders([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchFounders();
  }, []);

  const handleSearch = (e) => {
    e.preventDefault();
    fetchFounders();
  };

  const handleReset = () => {
    const emptyFilters = { location: '', skill: '', headline: '' };
    setFilters(emptyFilters);
    fetchFounders(emptyFilters);
  };

  return (
    <div>
      {/* Hero Banner */}
      <div className="hero-banner">
        <div>
          <span className="badge badge-indigo" style={{ marginBottom: '10px' }}>
            <Sparkles size={13} />
            Co-Founder Discovery Network
          </span>
          <h1 style={{ fontSize: '1.9rem', fontWeight: 800, margin: '6px 0 10px' }}>
            Find your ideal Co-Founder & Partner
          </h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.96rem', maxWidth: '600px' }}>
            Browse verified technical, product, and commercial founders looking to build next-generation companies.
          </p>
        </div>
      </div>

      {/* Filter Bar */}
      <div className="card" style={{ marginBottom: '24px', padding: '18px 24px' }}>
        <form onSubmit={handleSearch} style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr)) auto', gap: '14px', alignItems: 'flex-end' }}>
          <div>
            <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Search size={14} /> Role / Headline
            </label>
            <input 
              type="text"
              className="form-input"
              placeholder="e.g. CTO, Product, AI"
              value={filters.headline}
              onChange={(e) => setFilters({ ...filters, headline: e.target.value })}
            />
          </div>

          <div>
            <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <Filter size={14} /> Skill / Tech Stack
            </label>
            <input 
              type="text"
              className="form-input"
              placeholder="e.g. Java, React, Python"
              value={filters.skill}
              onChange={(e) => setFilters({ ...filters, skill: e.target.value })}
            />
          </div>

          <div>
            <label className="form-label" style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
              <MapPin size={14} /> Location
            </label>
            <input 
              type="text"
              className="form-input"
              placeholder="e.g. San Francisco, India"
              value={filters.location}
              onChange={(e) => setFilters({ ...filters, location: e.target.value })}
            />
          </div>

          <div style={{ display: 'flex', gap: '8px' }}>
            <button type="submit" className="btn btn-primary" style={{ height: '42px' }}>
              Filter
            </button>
            <button type="button" className="btn btn-secondary" onClick={handleReset} style={{ height: '42px' }}>
              Reset
            </button>
          </div>
        </form>
      </div>

      {/* Founders List */}
      {loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>
          Searching founder network...
        </div>
      ) : founders.length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Users size={48} color="var(--text-dim)" style={{ margin: '0 auto 16px' }} />
          <h3>No founders found matching current filters</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px' }}>
            Try expanding your search parameters or reset filters.
          </p>
        </div>
      ) : (
        <div className="grid-2">
          {founders.map((founder) => (
            <div key={founder.id || founder.userId} className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '12px' }}>
                <div>
                  <h3 style={{ fontSize: '1.2rem', fontWeight: 700 }}>
                    {founder.headline || 'Founder'}
                  </h3>
                  <div style={{ display: 'flex', gap: '14px', color: 'var(--text-muted)', fontSize: '0.86rem', marginTop: '4px' }}>
                    {founder.location && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <MapPin size={14} color="var(--accent-rose)" />
                        {founder.location}
                      </span>
                    )}
                    {founder.experienceYears !== undefined && (
                      <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                        <Briefcase size={14} color="var(--accent-cyan)" />
                        {founder.experienceYears}y exp
                      </span>
                    )}
                  </div>
                </div>
              </div>

              {founder.bio && (
                <p style={{ fontSize: '0.9rem', color: 'var(--text-muted)', lineHeight: 1.5, margin: '8px 0 16px', flex: 1 }}>
                  {founder.bio}
                </p>
              )}

              {founder.skills && (
                <div className="tag-list" style={{ marginBottom: '18px' }}>
                  {founder.skills.split(',').map((s, idx) => (
                    <span key={idx} className="badge badge-indigo">
                      {s.trim()}
                    </span>
                  ))}
                </div>
              )}

              <div style={{ display: 'flex', gap: '10px', marginTop: 'auto', paddingTop: '14px', borderTop: '1px solid var(--border-color)' }}>
                <button 
                  className="btn btn-primary btn-sm"
                  style={{ flex: 1 }}
                  onClick={() => onSelectChatUser(founder.userId, founder.headline || 'Founder')}
                >
                  <MessageSquare size={14} />
                  <span>Message</span>
                </button>
                <button 
                  className="btn btn-secondary btn-sm"
                  onClick={() => onGenerateMatch(founder.userId, founder.headline || '', founder.skills || '')}
                  title="Generate AI synergy match"
                >
                  <Sparkles size={14} color="var(--accent-cyan)" />
                  <span>Match Score</span>
                </button>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
