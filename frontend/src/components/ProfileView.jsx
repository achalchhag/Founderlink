import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { profileApi } from '../services/api';
import { 
  User, 
  MapPin, 
  Briefcase, 
  Globe, 
  Linkedin, 
  Github, 
  Edit3, 
  Save, 
  CheckCircle2, 
  AlertCircle 
} from 'lucide-react';

export default function ProfileView({ onOpenAuth }) {
  const { user, isAuthenticated } = useAuth();
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [isEditing, setIsEditing] = useState(false);
  const [formData, setFormData] = useState({
    headline: '',
    bio: '',
    location: '',
    skills: '',
    experienceYears: 0,
    websiteUrl: '',
    linkedinUrl: '',
    githubUrl: ''
  });
  const [message, setMessage] = useState('');
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isAuthenticated) {
      setLoading(false);
      return;
    }

    const loadProfile = async () => {
      try {
        setLoading(true);
        const data = await profileApi.getMine();
        if (data) {
          setProfile(data);
          setFormData({
            headline: data.headline || '',
            bio: data.bio || '',
            location: data.location || '',
            skills: data.skills || '',
            experienceYears: data.experienceYears || 0,
            websiteUrl: data.websiteUrl || '',
            linkedinUrl: data.linkedinUrl || '',
            githubUrl: data.githubUrl || ''
          });
        }
      } catch (err) {
        // If 404, profile doesn't exist yet -> open edit mode
        setIsEditing(true);
      } finally {
        setLoading(false);
      }
    };

    loadProfile();
  }, [isAuthenticated]);

  const handleSave = async (e) => {
    e.preventDefault();
    setMessage('');
    setError('');

    try {
      let saved;
      if (profile) {
        saved = await profileApi.update(formData);
      } else {
        saved = await profileApi.create(formData);
      }
      setProfile(saved);
      setIsEditing(false);
      setMessage('Profile successfully saved!');
      setTimeout(() => setMessage(''), 4000);
    } catch (err) {
      setError(err.message || 'Failed to save profile');
    }
  };

  if (!isAuthenticated) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px 20px' }}>
        <User size={48} color="var(--primary)" style={{ margin: '0 auto 16px' }} />
        <h2>Sign in to view and manage your Founder Profile</h2>
        <p style={{ color: 'var(--text-muted)', margin: '12px 0 24px' }}>
          Showcase your expertise, venture ideas, and connect with prospective co-founders.
        </p>
        <button className="btn btn-primary" onClick={onOpenAuth}>
          Sign In / Join FounderLink
        </button>
      </div>
    );
  }

  if (loading) {
    return <div style={{ textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>Loading founder profile...</div>;
  }

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div className="card-header" style={{ marginBottom: '24px' }}>
        <div>
          <h1 style={{ fontSize: '1.75rem', fontWeight: 800 }}>Founder Profile</h1>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.92rem' }}>
            Your public identity for matching and founder networking.
          </p>
        </div>
        {!isEditing && (
          <button className="btn btn-secondary btn-sm" onClick={() => setIsEditing(true)}>
            <Edit3 size={15} />
            <span>Edit Profile</span>
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

      {isEditing ? (
        <form onSubmit={handleSave} className="card">
          <div className="form-group">
            <label className="form-label">Professional Headline / Role</label>
            <input 
              type="text" 
              className="form-input" 
              placeholder="e.g. Technical Co-Founder | Full Stack & AI Engineer"
              value={formData.headline}
              onChange={(e) => setFormData({ ...formData, headline: e.target.value })}
              required
            />
          </div>

          <div className="form-group">
            <label className="form-label">Bio & Track Record</label>
            <textarea 
              className="form-textarea" 
              rows={4}
              placeholder="Share what you are building, past exits, technical stack, or domain focus..."
              value={formData.bio}
              onChange={(e) => setFormData({ ...formData, bio: e.target.value })}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Location (City, Country)</label>
              <input 
                type="text" 
                className="form-input" 
                placeholder="e.g. San Francisco, CA"
                value={formData.location}
                onChange={(e) => setFormData({ ...formData, location: e.target.value })}
              />
            </div>

            <div className="form-group">
              <label className="form-label">Years of Experience</label>
              <input 
                type="number" 
                min="0"
                className="form-input" 
                value={formData.experienceYears}
                onChange={(e) => setFormData({ ...formData, experienceYears: parseInt(e.target.value) || 0 })}
              />
            </div>
          </div>

          <div className="form-group">
            <label className="form-label">Key Skills & Tech Stack (comma separated)</label>
            <input 
              type="text" 
              className="form-input" 
              placeholder="e.g. React, Java, Spring Boot, PyTorch, System Architecture"
              value={formData.skills}
              onChange={(e) => setFormData({ ...formData, skills: e.target.value })}
            />
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '16px' }}>
            <div className="form-group">
              <label className="form-label">Website / Portfolio URL</label>
              <input 
                type="url" 
                className="form-input" 
                placeholder="https://mywebsite.com"
                value={formData.websiteUrl}
                onChange={(e) => setFormData({ ...formData, websiteUrl: e.target.value })}
              />
            </div>

            <div className="form-group">
              <label className="form-label">LinkedIn URL</label>
              <input 
                type="url" 
                className="form-input" 
                placeholder="https://linkedin.com/in/username"
                value={formData.linkedinUrl}
                onChange={(e) => setFormData({ ...formData, linkedinUrl: e.target.value })}
              />
            </div>

            <div className="form-group">
              <label className="form-label">GitHub URL</label>
              <input 
                type="url" 
                className="form-input" 
                placeholder="https://github.com/username"
                value={formData.githubUrl}
                onChange={(e) => setFormData({ ...formData, githubUrl: e.target.value })}
              />
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '20px' }}>
            {profile && (
              <button type="button" className="btn btn-secondary" onClick={() => setIsEditing(false)}>
                Cancel
              </button>
            )}
            <button type="submit" className="btn btn-primary">
              <Save size={16} />
              <span>Save Profile</span>
            </button>
          </div>
        </form>
      ) : (
        <div className="card">
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: '20px', marginBottom: '20px' }}>
            <div style={{ width: '64px', height: '64px', borderRadius: 'var(--radius-md)', background: 'linear-gradient(135deg, var(--primary), var(--accent-cyan))', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem', fontWeight: 700, color: '#fff' }}>
              {user?.email?.charAt(0).toUpperCase() || 'F'}
            </div>
            <div style={{ flex: 1 }}>
              <h2 style={{ fontSize: '1.4rem', fontWeight: 700 }}>{profile?.headline || 'Founder Profile'}</h2>
              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '16px', color: 'var(--text-muted)', fontSize: '0.88rem', marginTop: '6px' }}>
                {profile?.location && (
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <MapPin size={15} color="var(--accent-rose)" />
                    {profile.location}
                  </span>
                )}
                {profile?.experienceYears !== undefined && (
                  <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                    <Briefcase size={15} color="var(--accent-cyan)" />
                    {profile.experienceYears} years exp
                  </span>
                )}
              </div>
            </div>
          </div>

          {profile?.bio && (
            <div style={{ margin: '20px 0', lineHeight: 1.6, color: 'var(--text-main)', fontSize: '0.95rem' }}>
              {profile.bio}
            </div>
          )}

          {profile?.skills && (
            <div style={{ margin: '20px 0' }}>
              <h4 style={{ fontSize: '0.86rem', color: 'var(--text-dim)', marginBottom: '8px', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                Skills & Technologies
              </h4>
              <div className="tag-list">
                {profile.skills.split(',').map((skill, idx) => (
                  <span key={idx} className="badge badge-indigo">
                    {skill.trim()}
                  </span>
                ))}
              </div>
            </div>
          )}

          <div style={{ display: 'flex', gap: '12px', marginTop: '24px', paddingTop: '16px', borderTop: '1px solid var(--border-color)' }}>
            {profile?.websiteUrl && (
              <a href={profile.websiteUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                <Globe size={15} />
                <span>Website</span>
              </a>
            )}
            {profile?.linkedinUrl && (
              <a href={profile.linkedinUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                <Linkedin size={15} color="#0a66c2" />
                <span>LinkedIn</span>
              </a>
            )}
            {profile?.githubUrl && (
              <a href={profile.githubUrl} target="_blank" rel="noreferrer" className="btn btn-secondary btn-sm">
                <Github size={15} />
                <span>GitHub</span>
              </a>
            )}
          </div>
        </div>
      )}
    </div>
  );
}
