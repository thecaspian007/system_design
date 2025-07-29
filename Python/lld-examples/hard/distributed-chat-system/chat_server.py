"""
ChatServer class for the distributed chat system
"""

import uuid
from datetime import datetime
from typing import Dict, Set, List, Optional
from user import User


class ChatServer:
    """ChatServer class representing a chat server in the distributed system"""
    
    def __init__(self, server_id: str, region: str):
        self.server_id = server_id
        self.region = region
        self.started_at = datetime.now()
        self.is_active = True
        
        # Users on this server
        self.users: Dict[str, User] = {}  # user_id -> User
        self.user_sessions: Dict[str, str] = {}  # user_id -> session_id
        self.session_users: Dict[str, str] = {}  # session_id -> user_id
        
        # Connection tracking
        self.online_users: Set[str] = set()  # Set of online user IDs
        self.user_connections: Dict[str, datetime] = {}  # user_id -> connection_time
        
        # Server statistics
        self.total_connections = 0
        self.total_disconnections = 0
        self.peak_concurrent_users = 0
        self.last_heartbeat = datetime.now()
        
        # Load balancing
        self.load_factor = 0.0
        self.max_connections = 10000
        
        print(f"Chat server {server_id} started in region {region}")
    
    def add_user(self, user: User) -> bool:
        """Add user to this server"""
        if user.user_id in self.users:
            return False  # User already on this server
        
        self.users[user.user_id] = user
        return True
    
    def remove_user(self, user_id: str) -> bool:
        """Remove user from this server"""
        if user_id in self.users:
            # Disconnect if online
            if user_id in self.online_users:
                self.disconnect_user(user_id)
            
            del self.users[user_id]
            return True
        return False
    
    def has_user(self, user_id: str) -> bool:
        """Check if user is registered on this server"""
        return user_id in self.users
    
    def connect_user(self, user_id: str) -> Optional[str]:
        """Connect user to server"""
        if user_id not in self.users:
            return None
        
        if len(self.online_users) >= self.max_connections:
            return None  # Server at capacity
        
        user = self.users[user_id]
        session_id = str(uuid.uuid4())
        
        # Update user status
        user.set_online(self.server_id, session_id)
        
        # Track connection
        self.online_users.add(user_id)
        self.user_sessions[user_id] = session_id
        self.session_users[session_id] = user_id
        self.user_connections[user_id] = datetime.now()
        
        # Update statistics
        self.total_connections += 1
        if len(self.online_users) > self.peak_concurrent_users:
            self.peak_concurrent_users = len(self.online_users)
        
        self._update_load_factor()
        
        print(f"User {user.username} connected to server {self.server_id}")
        return session_id
    
    def disconnect_user(self, user_id: str) -> bool:
        """Disconnect user from server"""
        if user_id not in self.online_users:
            return False
        
        user = self.users.get(user_id)
        if user:
            user.set_offline()
        
        # Clean up tracking
        session_id = self.user_sessions.get(user_id)
        if session_id:
            del self.user_sessions[user_id]
            del self.session_users[session_id]
        
        self.online_users.remove(user_id)
        if user_id in self.user_connections:
            del self.user_connections[user_id]
        
        # Update statistics
        self.total_disconnections += 1
        self._update_load_factor()
        
        if user:
            print(f"User {user.username} disconnected from server {self.server_id}")
        
        return True
    
    def get_online_users(self) -> List[str]:
        """Get list of online user IDs"""
        return list(self.online_users)
    
    def get_user_count(self) -> int:
        """Get total user count on this server"""
        return len(self.users)
    
    def get_online_count(self) -> int:
        """Get online user count"""
        return len(self.online_users)
    
    def is_user_online(self, user_id: str) -> bool:
        """Check if user is online"""
        return user_id in self.online_users
    
    def get_user_session(self, user_id: str) -> Optional[str]:
        """Get user's session ID"""
        return self.user_sessions.get(user_id)
    
    def get_session_user(self, session_id: str) -> Optional[str]:
        """Get user ID for session"""
        return self.session_users.get(session_id)
    
    def validate_session(self, user_id: str, session_id: str) -> bool:
        """Validate user session"""
        return (user_id in self.user_sessions and 
                self.user_sessions[user_id] == session_id)
    
    def update_user_presence(self):
        """Update user presence (heartbeat)"""
        current_time = datetime.now()
        self.last_heartbeat = current_time
        
        # Update last seen for online users
        for user_id in self.online_users:
            user = self.users.get(user_id)
            if user:
                user.update_last_seen()
    
    def get_server_stats(self) -> dict:
        """Get server statistics"""
        uptime = datetime.now() - self.started_at
        
        return {
            'server_id': self.server_id,
            'region': self.region,
            'is_active': self.is_active,
            'started_at': self.started_at.isoformat(),
            'uptime_seconds': int(uptime.total_seconds()),
            'total_users': len(self.users),
            'online_users': len(self.online_users),
            'total_connections': self.total_connections,
            'total_disconnections': self.total_disconnections,
            'peak_concurrent_users': self.peak_concurrent_users,
            'load_factor': self.load_factor,
            'max_connections': self.max_connections,
            'last_heartbeat': self.last_heartbeat.isoformat()
        }
    
    def get_user_list(self) -> List[dict]:
        """Get list of users on this server"""
        user_list = []
        for user in self.users.values():
            user_info = user.to_dict()
            user_info['is_online_on_server'] = user.user_id in self.online_users
            if user.user_id in self.user_connections:
                user_info['connection_time'] = self.user_connections[user.user_id].isoformat()
            user_list.append(user_info)
        
        return user_list
    
    def broadcast_message(self, message: str, exclude_user_id: Optional[str] = None):
        """Broadcast message to all online users"""
        for user_id in self.online_users:
            if exclude_user_id and user_id == exclude_user_id:
                continue
            
            user = self.users.get(user_id)
            if user:
                print(f"[Broadcast to {user.username}] {message}")
    
    def send_to_user(self, user_id: str, message: str) -> bool:
        """Send message to specific user"""
        if user_id not in self.online_users:
            return False
        
        user = self.users.get(user_id)
        if user:
            print(f"[Message to {user.username}] {message}")
            return True
        
        return False
    
    def can_accept_connections(self) -> bool:
        """Check if server can accept new connections"""
        return (self.is_active and 
                len(self.online_users) < self.max_connections and
                self.load_factor < 0.9)
    
    def _update_load_factor(self):
        """Update server load factor"""
        if self.max_connections > 0:
            self.load_factor = len(self.online_users) / self.max_connections
        else:
            self.load_factor = 0.0
    
    def shutdown(self):
        """Shutdown server gracefully"""
        print(f"Shutting down server {self.server_id}...")
        
        # Disconnect all users
        for user_id in list(self.online_users):
            self.disconnect_user(user_id)
        
        self.is_active = False
        print(f"Server {self.server_id} shutdown complete")
    
    def get_connection_duration(self, user_id: str) -> Optional[int]:
        """Get user connection duration in seconds"""
        if user_id in self.user_connections:
            duration = datetime.now() - self.user_connections[user_id]
            return int(duration.total_seconds())
        return None
    
    def migrate_user(self, user_id: str, target_server: 'ChatServer') -> bool:
        """Migrate user to another server"""
        if user_id not in self.users:
            return False
        
        user = self.users[user_id]
        was_online = user_id in self.online_users
        session_id = self.user_sessions.get(user_id) if was_online else None
        
        # Remove from current server
        self.remove_user(user_id)
        
        # Add to target server
        if target_server.add_user(user):
            if was_online and session_id:
                target_server.connect_user(user_id)
            print(f"User {user.username} migrated from {self.server_id} to {target_server.server_id}")
            return True
        
        return False
    
    def __str__(self):
        return f"ChatServer(id={self.server_id}, region={self.region}, users={len(self.users)}, online={len(self.online_users)})"
    
    def __repr__(self):
        return self.__str__() 