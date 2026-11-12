import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { startupApi } from '../services/api';
import { 
  Rocket, 
  Plus, 
  Trash2, 
  Globe, 
  Layers, 
  Tag, 
  CheckCircle2, 
  AlertCircle,
  ExternalLink 
} from 'lucide-react';

const STAGES = ['IDEA', 'MVP', 'EARLY_STAGE', 'GROWTH', 'SCALING'];

export default function StartupView({ onOpenAuth }) {
  const { isAuthenticated } = useAuth();
  const [startups, setStartups] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [industryFilter, setIndustryFilter] = useState('');
  const [filteredStartups, setFilteredStartups] = useState([]);
  const [isSearchingIndustry, setIsSearchingIndustry] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    description: '',
    industry: '',
    stage: 'IDEA',
    lookingFor: '',
    websiteUrl: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  const loadMyStartups = async () => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }
    try {
      setLoading(true);
      const data = await startupApi.getMyStartups();
      setStartups(data || []);
    } catch (err) {
      setStartups([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadMyStartups();
  }, [isAuthenticated]);

  const handleCreate = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      const created = await startupApi.create(formData);
      setStartups([created, ...startups]);
      setShowCreateModal(false);
      setFormData({
        name: '',
        description: '',
        industry: '',
        stage: 'IDEA',
        lookingFor: '',
        websiteUrl: ''
      });
      setMessage(`Startup "${created.name}" created successfully!`);
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      setError(err.message || 'Failed to create startup');
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this startup?')) return;
    try {
      await startupApi.delete(id);
      setStartups(startups.filter((s) => s.id !== id));
      setMessage('Startup deleted');
      setTimeout(() => setMessage(''), 3000);
    } catch (err) {
      setError(err.message || 'Failed to delete startup');
    }
  };

  const handleIndustrySearch = async (e) => {
    e.preventDefault();
    if (!industryFilter.trim()) {
      setIsSearchingIndustry(false);
      return;
    }
    try {
      setLoading(true);
      setIsSearchingIndustry(true);
      const results = await startupApi.findByIndustry(industryFilter.trim());
      setFilteredStartups(results || []);
    } catch (err) {
      setFilteredStartups([]);
    } finally {
      setLoading(false);
    }
  };

  const clearIndustrySearch = () => {
    setIndustryFilter('');
    setIsSearchingIndustry(false);
    loadMyStartups();
  };

  return (
    <div>
      <div className="card-header" style={{ marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Startup Ventures</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem' }}>
            Manage your venture ideas and connect with co-founders seeking specific roles.
          </p>
        </div>
        {isAuthenticated && (
          <button className="btn btn-primary" onClick={() => setShowCreateModal(true)}>
            <Plus size={16} />
            <span>Launch Startup Idea</span>
          </button>
        )}
      </div>

      {message && (
        <div style={{ padding: '12px 16px', background: 'rgba(16, 185, 129, 0.15)', border: '1px solid rgba(16, 185, 129, 0.3)', borderRadius: 'var(--radius-md)', color: '#6ee7b7', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <CheckCircle2 size={18} />
          <span>{message}</span>
        </div>
      )}

      {error && (
        <div style={{ padding: '12px 16px', background: 'rgba(244, 63, 94, 0.15)', border: '1px solid rgba(244, 63, 94, 0.3)', borderRadius: 'var(--radius-md)', color: '#fda4af', marginBottom: '20px', display: 'flex', alignItems: 'center', gap: '8px' }}>
          <AlertCircle size={18} />
          <span>{error}</span>
        </div>
      )}

      {/* Industry Search Bar */}
      <div className="card" style={{ marginBottom: '24px', padding: '16px 20px' }}>
        <form onSubmit={handleIndustrySearch} style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <div style={{ flex: 1 }}>
            <input 
              type="text"
              className="form-input"
              placeholder="Search startups by industry (e.g. AI, FinTech, HealthTech, SaaS)..."
              value={industryFilter}
              onChange={(e) => setIndustryFilter(e.target.value)}
            />
          </div>
          <button type="submit" className="btn btn-secondary">
            Search Industry
          </button>
          {isSearchingIndustry && (
            <button type="button" className="btn btn-secondary" onClick={clearIndustrySearch}>
              Show My Startups
            </button>
          )}
        </form>
      </div>

      {!isAuthenticated && !isSearchingIndustry ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Rocket size={48} color="var(--primary)" style={{ margin: '0 auto 16px' }} />
          <h2>Manage and showcase your Startup Ventures</h2>
          <p style={{ color: 'var(--text-muted)', margin: '12px 0 24px' }}>
            Sign in to post ideas, indicate open co-founder positions, and track applications.
          </p>
          <button className="btn btn-primary" onClick={onOpenAuth}>
            Sign In / Join
          </button>
        </div>
      ) : loading ? (
        <div style={{ textAlign: 'center', padding: '60px', color: 'var(--text-muted)' }}>
          Loading startups...
        </div>
      ) : (isSearchingIndustry ? filteredStartups : startups).length === 0 ? (
        <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
          <Rocket size={48} color="var(--text-dim)" style={{ margin: '0 auto 16px' }} />
          <h3>{isSearchingIndustry ? 'No startups found in this industry' : 'You haven’t added any startups yet'}</h3>
          <p style={{ color: 'var(--text-muted)', marginTop: '8px' }}>
            {isSearchingIndustry ? 'Try searching for other sectors.' : 'Click "Launch Startup Idea" to publish your first venture.'}
          </p>
        </div>
      ) : (
        <div className="grid-2">
          {(isSearchingIndustry ? filteredStartups : startups).map((startup) => (
            <div key={startup.id} className="card" style={{ display: 'flex', flexDirection: 'column' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '8px' }}>
                <div>
                  <h3 style={{ fontSize: '1.25rem', fontWeight: 700 }}>{startup.name}</h3>
                  <div style={{ display: 'flex', gap: '8px', marginTop: '6px' }}>
                    <span className="badge badge-indigo">
                      <Tag size={12} />
                      {startup.industry || 'General'}
                    </span>
                    <span className="badge badge-cyan">
                      <Layers size={12} />
                      {startup.stage || 'IDEA'}
                    </span>
                  </div>
                </div>
                {!isSearchingIndustry && (
                  <button 
                    onClick={() => handleDelete(startup.id)}
                    className="btn btn-danger btn-sm btn-icon"
                    title="Delete startup"
                  >
                    <Trash2 size={15} />
                  </button>
                )}
              </div>

              {startup.description && (
                <p style={{ fontSize: '0.92rem', color: 'var(--text-muted)', margin: '14px 0', lineHeight: 1.5, flex: 1 }}>
                  {startup.description}
                </p>
              )}

              {startup.lookingFor && (
                <div style={{ background: 'rgba(255, 255, 255, 0.03)', padding: '10px 14px', borderRadius: 'var(--radius-md)', margin: '10px 0', border: '1px solid var(--border-color)' }}>
                  <div style={{ fontSize: '0.78rem', color: 'var(--accent-amber)', fontWeight: 700, textTransform: 'uppercase', marginBottom: '4px' }}>
                    Looking For Co-Founders:
                  </div>
                  <div style={{ fontSize: '0.88rem', color: 'var(--text-main)' }}>
                    {startup.lookingFor}
                  </div>
                </div>
              )}

              {startup.websiteUrl && (
                <div style={{ marginTop: 'auto', paddingTop: '12px' }}>
                  <a href={startup.websiteUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm" style={{ width: '100%' }}>
                    <Globe size={14} />
                    <span>Visit Website</span>
                    <ExternalLink size={13} />
                  </a>
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Create Startup Modal */}
      {showCreateModal && (
        <div className="modal-overlay" onClick={() => setShowCreateModal(false)}>
          <div className="modal-card" onClick={(e) => e.stopPropagation()} style={{ maxWidth: '560px' }}>
            <h2 style={{ fontSize: '1.35rem', fontWeight: 700, marginBottom: '20px' }}>
              Launch a New Startup Idea
            </h2>

            <form onSubmit={handleCreate}>
              <div className="form-group">
                <label className="form-label">Startup Name</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. Nexus AI"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Elevator Pitch & Description</label>
                <textarea 
                  className="form-textarea" 
                  rows={3}
                  placeholder="What problem are you solving and how?"
                  value={formData.description}
                  onChange={(e) => setFormData({ ...formData, description: e.target.value })}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '14px' }}>
                <div className="form-group">
                  <label className="form-label">Industry</label>
                  <input 
                    type="text" 
                    className="form-input" 
                    placeholder="e.g. Artificial Intelligence"
                    value={formData.industry}
                    onChange={(e) => setFormData({ ...formData, industry: e.target.value })}
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Current Stage</label>
                  <select 
                    className="form-select"
                    value={formData.stage}
                    onChange={(e) => setFormData({ ...formData, stage: e.target.value })}
                  >
                    {STAGES.map((st) => (
                      <option key={st} value={st}>{st}</option>
                    ))}
                  </select>
                </div>
              </div>

              <div className="form-group">
                <label className="form-label">Seeking Co-Founder Roles (e.g. CTO, Growth Lead)</label>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="e.g. Full-Stack Developer with AI interest, Head of Sales"
                  value={formData.lookingFor}
                  onChange={(e) => setFormData({ ...formData, lookingFor: e.target.value })}
                />
              </div>

              <div className="form-group">
                <label className="form-label">Demo / Website URL (Optional)</label>
                <input 
                  type="url" 
                  className="form-input" 
                  placeholder="https://nexusai.io"
                  value={formData.websiteUrl}
                  onChange={(e) => setFormData({ ...formData, websiteUrl: e.target.value })}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '10px', marginTop: '24px' }}>
                <button type="button" className="btn btn-secondary" onClick={() => setShowCreateModal(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn btn-primary">
                  Publish Startup
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
