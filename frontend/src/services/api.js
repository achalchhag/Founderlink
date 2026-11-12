const BASE_URL = '';

export async function request(endpoint, options = {}) {
  const token = localStorage.getItem('founderlink_token');
  const headers = {
    'Content-Type': 'application/json',
    ...(options.headers || {})
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${BASE_URL}${endpoint}`, {
    ...options,
    headers
  });

  if (response.status === 204) {
    return null;
  }

  const data = await response.json().catch(() => null);

  if (!response.ok) {
    let errorMsg = `HTTP ${response.status}`;
    if (data) {
      if (data.details && typeof data.details === 'object' && Object.keys(data.details).length > 0) {
        errorMsg = Object.values(data.details).join(' | ');
      } else if (data.fieldErrors && typeof data.fieldErrors === 'object' && Object.keys(data.fieldErrors).length > 0) {
        errorMsg = Object.values(data.fieldErrors).join(' | ');
      } else if (data.message && data.message !== 'Validation failed') {
        errorMsg = data.message;
      } else if (data.error) {
        errorMsg = data.error;
      } else if (data.message) {
        errorMsg = data.message;
      }
    }
    throw new Error(errorMsg);
  }

  return data;
}

// 1. Auth Service
export const authApi = {
  login: (credentials) => request('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify(credentials)
  }),
  register: (userData) => request('/api/auth/register', {
    method: 'POST',
    body: JSON.stringify(userData)
  })
};

// 2. Profile Service
export const profileApi = {
  getMine: () => request('/api/profiles/me'),
  create: (profileData) => request('/api/profiles', {
    method: 'POST',
    body: JSON.stringify(profileData)
  }),
  update: (profileData) => request('/api/profiles/me', {
    method: 'PUT',
    body: JSON.stringify(profileData)
  }),
  getByUserId: (userId) => request(`/api/profiles/${userId}`),
  search: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return request(`/api/profiles${query ? `?${query}` : ''}`);
  }
};

// 3. Discovery Service
export const discoveryApi = {
  findFounders: (params = {}) => {
    const query = new URLSearchParams(params).toString();
    return request(`/api/discovery/founders${query ? `?${query}` : ''}`);
  },
  findFounderByUserId: (userId) => request(`/api/discovery/founders/${userId}`)
};

// 4. Startup Service
export const startupApi = {
  getMyStartups: () => request('/api/startups/me'),
  getById: (id) => request(`/api/startups/${id}`),
  create: (startupData) => request('/api/startups', {
    method: 'POST',
    body: JSON.stringify(startupData)
  }),
  update: (id, updates) => request(`/api/startups/${id}`, {
    method: 'PUT',
    body: JSON.stringify(updates)
  }),
  delete: (id) => request(`/api/startups/${id}`, {
    method: 'DELETE'
  }),
  findByIndustry: (industry) => request(`/api/startups?industry=${encodeURIComponent(industry)}`)
};

// 5. Matching Service
export const matchingApi = {
  getMyMatches: (type = '') => request(`/api/matching${type ? `?type=${type}` : ''}`),
  getById: (id) => request(`/api/matching/${id}`),
  create: (matchData) => request('/api/matching', {
    method: 'POST',
    body: JSON.stringify(matchData)
  }),
  generate: (role = '', skills = '') => request(`/api/matching/generate?role=${encodeURIComponent(role)}&skills=${encodeURIComponent(skills)}`, {
    method: 'POST'
  }),
  updateStatus: (id, status) => request(`/api/matching/${id}/status`, {
    method: 'PATCH',
    body: JSON.stringify({ status })
  }),
  delete: (id) => request(`/api/matching/${id}`, {
    method: 'DELETE'
  })
};

// 6. Chat Service
export const chatApi = {
  sendMessage: (messageData) => request('/api/chat/messages', {
    method: 'POST',
    body: JSON.stringify(messageData)
  }),
  getConversation: (otherUserId) => request(`/api/chat/messages/${otherUserId}`),
  getMyMessages: () => request('/api/chat/my-messages'),
  markAsRead: (messageId) => request(`/api/chat/messages/${messageId}/read`, {
    method: 'PATCH'
  }),
  getUnreadCount: () => request('/api/chat/unread-count')
};

// 7. Notification Service
export const notificationApi = {
  getMyNotifications: () => request('/api/notifications'),
  getUnreadCount: () => request('/api/notifications/unread-count'),
  markAsRead: (id) => request(`/api/notifications/${id}/read`, {
    method: 'PATCH'
  }),
  markAllAsRead: () => request('/api/notifications/read-all', {
    method: 'PATCH'
  }),
  create: (notificationData) => request('/api/notifications', {
    method: 'POST',
    body: JSON.stringify(notificationData)
  }),
  delete: (id) => request(`/api/notifications/${id}`, {
    method: 'DELETE'
  })
};
