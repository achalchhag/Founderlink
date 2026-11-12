import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';
import { chatApi, discoveryApi } from '../services/api';
import { 
  Send, 
  MessageSquare, 
  Check, 
  CheckCheck, 
  Sparkles,
  Search
} from 'lucide-react';

const INITIAL_CONTACTS = [
  { id: '11111111-1111-1111-1111-111111111111', name: 'Alex Rivera', role: 'Technical Co-Founder', isDemo: true },
  { id: '22222222-2222-2222-2222-222222222222', name: 'Sarah Chen', role: 'Product & Growth Lead', isDemo: true },
  { id: '33333333-3333-3333-3333-333333333333', name: 'Marcus Vance', role: 'AI/ML Specialist', isDemo: true }
];

const AUTO_REPLIES = [
  "Hi! Thanks for reaching out. I'm actively looking for a co-founder with your background to build together.",
  "Sounds great! What tech stack or product timeline are you thinking for the MVP?",
  "I reviewed your profile and really like the startup synergy. Let's set up a quick 15-min sync call this week!",
  "Awesome idea! I have previous experience scaling distributed systems and would love to collaborate."
];

export default function ChatView({ onOpenAuth, initialSelectedUserId, initialSelectedUserName }) {
  const { user, isAuthenticated } = useAuth();
  const [contacts, setContacts] = useState(INITIAL_CONTACTS);
  const [searchQuery, setSearchQuery] = useState('');
  const [selectedUser, setSelectedUser] = useState(() => {
    if (initialSelectedUserId) {
      return { id: initialSelectedUserId, name: initialSelectedUserName || 'Founder', role: 'Co-Founder' };
    }
    return INITIAL_CONTACTS[0];
  });
  const [messages, setMessages] = useState([]);
  const [inputContent, setInputContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);

  // Load any discovered founders into contacts list
  useEffect(() => {
    const loadDiscoveredFounders = async () => {
      try {
        const founders = await discoveryApi.findFounders();
        if (Array.isArray(founders) && founders.length > 0) {
          const formatted = founders.map(f => ({
            id: f.userId || f.id,
            name: f.fullName || 'Founder',
            role: f.headline || 'Co-Founder'
          }));
          setContacts(prev => {
            const existingIds = new Set(prev.map(c => c.id));
            const newOnes = formatted.filter(f => !existingIds.has(f.id) && f.id !== user?.id);
            return [...prev, ...newOnes];
          });
        }
      } catch {
        // quiet fallback
      }
    };

    if (isAuthenticated) {
      loadDiscoveredFounders();
    }
  }, [isAuthenticated, user?.id]);

  // Handle incoming selected user from other tabs (Discover, AI Matches)
  useEffect(() => {
    if (initialSelectedUserId) {
      const contactObj = {
        id: initialSelectedUserId,
        name: initialSelectedUserName || 'Founder',
        role: 'Co-Founder'
      };
      setSelectedUser(contactObj);
      setContacts(prev => {
        if (!prev.some(c => c.id === initialSelectedUserId)) {
          return [contactObj, ...prev];
        }
        return prev;
      });
    }
  }, [initialSelectedUserId, initialSelectedUserName]);

  // Fetch or retrieve conversation history
  const fetchConversation = async () => {
    if (!isAuthenticated || !selectedUser?.id) return;
    try {
      setLoading(true);
      const data = await chatApi.getConversation(selectedUser.id);
      if (Array.isArray(data) && data.length > 0) {
        setMessages(data);
      } else {
        // Check local cache
        const localCache = localStorage.getItem(`founderlink_chat_${user?.id}_${selectedUser.id}`);
        if (localCache) {
          setMessages(JSON.parse(localCache));
        } else {
          setMessages([]);
        }
      }
    } catch {
      const localCache = localStorage.getItem(`founderlink_chat_${user?.id}_${selectedUser.id}`);
      if (localCache) {
        setMessages(JSON.parse(localCache));
      } else {
        setMessages([]);
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchConversation();
  }, [selectedUser?.id, isAuthenticated]);

  // Auto-scroll messages
  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!inputContent.trim() || sending) return;

    const messageText = inputContent.trim();
    setInputContent('');
    setSending(true);

    const newMsg = {
      id: 'msg-' + Date.now(),
      senderId: user?.id,
      recipientId: selectedUser.id,
      content: messageText,
      createdAt: new Date().toISOString(),
      read: true
    };

    const updated = [...messages, newMsg];
    setMessages(updated);
    localStorage.setItem(`founderlink_chat_${user?.id}_${selectedUser.id}`, JSON.stringify(updated));

    try {
      const sent = await chatApi.sendMessage({
        recipientId: selectedUser.id,
        content: messageText
      });
      if (sent && sent.id) {
        setMessages(prev => prev.map(m => m.id === newMsg.id ? sent : m));
      }
    } catch {
      // Offline fallback: simulate active co-founder reply for demo contacts
      if (selectedUser.isDemo) {
        setTimeout(() => {
          const replyText = AUTO_REPLIES[Math.floor(Math.random() * AUTO_REPLIES.length)];
          const replyMsg = {
            id: 'reply-' + Date.now(),
            senderId: selectedUser.id,
            recipientId: user?.id,
            content: replyText,
            createdAt: new Date().toISOString(),
            read: true
          };
          setMessages(prev => {
            const next = [...prev, replyMsg];
            localStorage.setItem(`founderlink_chat_${user?.id}_${selectedUser.id}`, JSON.stringify(next));
            return next;
          });
        }, 1200);
      }
    } finally {
      setSending(false);
    }
  };

  const filteredContacts = contacts.filter(c => 
    c.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
    c.role.toLowerCase().includes(searchQuery.toLowerCase())
  );

  if (!isAuthenticated) {
    return (
      <div className="card" style={{ textAlign: 'center', padding: '60px 20px', maxWidth: '600px', margin: '40px auto' }}>
        <MessageSquare size={48} color="var(--primary)" style={{ margin: '0 auto 16px' }} />
        <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '8px' }}>Founder Direct Messaging</h2>
        <p style={{ color: 'var(--text-muted)', margin: '12px 0 24px', lineHeight: 1.6 }}>
          Sign in or create an account to start conversations and coordinate startup milestones with co-founders.
        </p>
        <button className="btn btn-primary" onClick={onOpenAuth}>
          Sign In / Join
        </button>
      </div>
    );
  }

  return (
    <div className="chat-container">
      {/* Sidebar Contacts */}
      <div className="chat-sidebar">
        <div className="chat-sidebar-header">
          <MessageSquare size={18} color="var(--primary)" />
          <span>Conversations</span>
        </div>

        <div style={{ padding: '10px 14px', borderBottom: '1px solid var(--border-color)' }}>
          <div style={{ position: 'relative' }}>
            <input 
              type="text" 
              className="form-input" 
              placeholder="Search contacts..."
              value={searchQuery}
              onChange={(e) => setSearchQuery(e.target.value)}
              style={{ paddingLeft: '32px', fontSize: '0.86rem', padding: '7px 12px 7px 32px' }}
            />
            <Search size={14} color="var(--text-dim)" style={{ position: 'absolute', left: '10px', top: '50%', transform: 'translateY(-50%)' }} />
          </div>
        </div>

        <div className="chat-sidebar-list">
          {filteredContacts.map((contact) => {
            const isSelected = selectedUser?.id === contact.id;
            return (
              <div
                key={contact.id}
                className={`chat-contact-item ${isSelected ? 'active' : ''}`}
                onClick={() => setSelectedUser(contact)}
              >
                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                  <div style={{ 
                    width: '34px', 
                    height: '34px', 
                    borderRadius: 'var(--radius-full)', 
                    background: isSelected ? 'var(--primary)' : 'var(--btn-secondary-bg)',
                    color: isSelected ? '#fff' : 'var(--text-main)',
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'center', 
                    fontWeight: 700, 
                    fontSize: '0.88rem',
                    flexShrink: 0
                  }}>
                    {contact.name.charAt(0)}
                  </div>
                  <div style={{ overflow: 'hidden' }}>
                    <div style={{ 
                      fontWeight: 600, 
                      fontSize: '0.92rem', 
                      color: 'var(--text-main)',
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis'
                    }}>
                      {contact.name}
                    </div>
                    <div style={{ 
                      fontSize: '0.78rem', 
                      color: 'var(--text-muted)', 
                      whiteSpace: 'nowrap',
                      overflow: 'hidden',
                      textOverflow: 'ellipsis'
                    }}>
                      {contact.role}
                    </div>
                  </div>
                </div>
              </div>
            );
          })}
        </div>
      </div>

      {/* Main Chat Area */}
      <div className="chat-main">
        {/* Header */}
        <div className="chat-header">
          <div style={{ 
            width: '40px', 
            height: '40px', 
            borderRadius: 'var(--radius-full)', 
            background: 'linear-gradient(135deg, var(--primary), var(--accent-cyan))', 
            display: 'flex', 
            alignItems: 'center', 
            justifyContent: 'center', 
            fontWeight: 700, 
            color: '#fff', 
            fontSize: '1rem',
            boxShadow: '0 2px 8px var(--primary-glow)',
            flexShrink: 0
          }}>
            {selectedUser?.name?.charAt(0) || 'F'}
          </div>
          <div>
            <div style={{ fontWeight: 700, fontSize: '1.02rem', color: 'var(--text-main)' }}>
              {selectedUser?.name}
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--accent-emerald)', display: 'flex', alignItems: 'center', gap: '5px' }}>
              <span style={{ width: '7px', height: '7px', borderRadius: '50%', background: 'var(--accent-emerald)' }} />
              <span>Online & active</span>
            </div>
          </div>
        </div>

        {/* Messages Stream */}
        <div className="chat-messages">
          {loading && messages.length === 0 ? (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)', margin: 'auto' }}>
              Loading conversation...
            </div>
          ) : messages.length === 0 ? (
            <div style={{ textAlign: 'center', color: 'var(--text-muted)', margin: 'auto', maxWidth: '380px' }}>
              <div style={{ width: '56px', height: '56px', borderRadius: 'var(--radius-full)', background: 'var(--badge-indigo-bg)', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '0 auto 12px' }}>
                <Sparkles size={26} color="var(--primary)" />
              </div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: 700, color: 'var(--text-main)', marginBottom: '6px' }}>
                Start a Conversation
              </h3>
              <p style={{ fontSize: '0.88rem', lineHeight: 1.5, color: 'var(--text-muted)' }}>
                Introduce your idea or discuss potential co-founder synergy with {selectedUser?.name}.
              </p>
            </div>
          ) : (
            messages.map((msg, index) => {
              const isMine = msg.senderId === user?.id;
              return (
                <div key={msg.id || index} className={`msg-bubble ${isMine ? 'sent' : 'received'}`}>
                  <div>{msg.content}</div>
                  <div style={{ 
                    fontSize: '0.68rem', 
                    opacity: 0.75, 
                    textAlign: 'right', 
                    marginTop: '4px', 
                    display: 'flex', 
                    alignItems: 'center', 
                    justifyContent: 'flex-end', 
                    gap: '4px' 
                  }}>
                    {new Date(msg.createdAt || Date.now()).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    {isMine && (msg.read ? <CheckCheck size={13} /> : <Check size={13} />)}
                  </div>
                </div>
              );
            })
          )}
          <div ref={messagesEndRef} />
        </div>

        {/* Input Bar */}
        <form onSubmit={handleSend} className="chat-input-bar">
          <input 
            type="text"
            className="form-input"
            placeholder={`Message ${selectedUser?.name || 'founder'}...`}
            value={inputContent}
            onChange={(e) => setInputContent(e.target.value)}
            style={{ borderRadius: 'var(--radius-full)', padding: '12px 18px' }}
          />
          <button 
            type="submit" 
            className="btn btn-primary"
            disabled={sending || !inputContent.trim()}
            style={{ 
              borderRadius: 'var(--radius-full)', 
              width: '44px', 
              height: '44px', 
              padding: 0, 
              flexShrink: 0 
            }}
          >
            <Send size={18} />
          </button>
        </form>
      </div>
    </div>
  );
}
