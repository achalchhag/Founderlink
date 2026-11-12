import React, { useState } from 'react';
import { AuthProvider, useAuth } from './context/AuthContext';
import { ThemeProvider } from './context/ThemeContext';
import Navbar from './components/Navbar';
import AuthModal from './components/AuthModal';
import DiscoveryView from './components/DiscoveryView';
import StartupView from './components/StartupView';
import MatchingView from './components/MatchingView';
import ChatView from './components/ChatView';
import NotificationView from './components/NotificationView';
import ProfileView from './components/ProfileView';

function MainApp() {
  const [activeTab, setActiveTab] = useState('discovery');
  const [authModalOpen, setAuthModalOpen] = useState(false);
  const [chatTargetUser, setChatTargetUser] = useState(null);

  const handleSelectChatUser = (userId, userName) => {
    setChatTargetUser({ id: userId, name: userName });
    setActiveTab('chat');
  };

  const handleGenerateMatch = (userId, headline, skills) => {
    setActiveTab('matching');
  };

  return (
    <div className="app-container">
      <Navbar 
        activeTab={activeTab} 
        setActiveTab={setActiveTab} 
        onOpenAuth={() => setAuthModalOpen(true)} 
      />

      <main className="main-content">
        {activeTab === 'discovery' && (
          <DiscoveryView 
            onSelectChatUser={handleSelectChatUser} 
            onGenerateMatch={handleGenerateMatch} 
          />
        )}

        {activeTab === 'startups' && (
          <StartupView 
            onOpenAuth={() => setAuthModalOpen(true)} 
          />
        )}

        {activeTab === 'matching' && (
          <MatchingView 
            onOpenAuth={() => setAuthModalOpen(true)} 
            onSelectChatUser={handleSelectChatUser} 
          />
        )}

        {activeTab === 'chat' && (
          <ChatView 
            onOpenAuth={() => setAuthModalOpen(true)} 
            initialSelectedUserId={chatTargetUser?.id}
            initialSelectedUserName={chatTargetUser?.name}
          />
        )}

        {activeTab === 'notifications' && (
          <NotificationView 
            onOpenAuth={() => setAuthModalOpen(true)} 
          />
        )}

        {activeTab === 'profile' && (
          <ProfileView 
            onOpenAuth={() => setAuthModalOpen(true)} 
          />
        )}
      </main>

      <AuthModal 
        isOpen={authModalOpen} 
        onClose={() => setAuthModalOpen(false)} 
      />
    </div>
  );
}

export default function App() {
  return (
    <ThemeProvider>
      <AuthProvider>
        <MainApp />
      </AuthProvider>
    </ThemeProvider>
  );
}
