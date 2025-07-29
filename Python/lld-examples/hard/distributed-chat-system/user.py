"""
User class for the distributed chat system
"""

import uuid
from datetime import datetime
from typing import Optional


class User:
    """User class representing a chat user"""
    
    def __init__(self, username: str, email: str, display_name: str):
        self.user_id = str(uuid.uuid4())
        self.username = username
        self.email = email
        self.display_name = display_name
        self.is_online = False
        self.created_at = datetime.now()
        self.last_seen = datetime.now()
        self.status_message = ""
        self.profile_picture = ""
        
        # Connection information
        self.server_id: Optional[str] = None
        self.session_id: Optional[str] = None
        self.connection_time: Optional[datetime] = None
    
    def set_online(self, server_id: str, session_id: str):
        """Set user as online"""
        self.is_online = True
        self.server_id = server_id
        self.session_id = session_id
        self.connection_time = datetime.now()
        self.last_seen = datetime.now()
    
    def set_offline(self):
        """Set user as offline"""
        self.is_online = False
        self.server_id = None
        self.session_id = None
        self.connection_time = None
        self.last_seen = datetime.now()
    
    def update_last_seen(self):
        """Update last seen timestamp"""
        self.last_seen = datetime.now()
    
    def set_status_message(self, message: str):
        """Set user status message"""
        self.status_message = message
    
    def set_profile_picture(self, picture_url: str):
        """Set user profile picture"""
        self.profile_picture = picture_url
    
    def get_connection_duration(self) -> Optional[int]:
        """Get connection duration in seconds"""
        if self.is_online and self.connection_time:
            return int((datetime.now() - self.connection_time).total_seconds())
        return None
    
    def __str__(self):
        return f"User(username={self.username}, display_name={self.display_name}, online={self.is_online})"
    
    def __repr__(self):
        return self.__str__()
    
    def to_dict(self):
        """Convert user to dictionary"""
        return {
            'user_id': self.user_id,
            'username': self.username,
            'email': self.email,
            'display_name': self.display_name,
            'is_online': self.is_online,
            'created_at': self.created_at.isoformat(),
            'last_seen': self.last_seen.isoformat(),
            'status_message': self.status_message,
            'profile_picture': self.profile_picture,
            'server_id': self.server_id,
            'session_id': self.session_id,
            'connection_time': self.connection_time.isoformat() if self.connection_time else None
        } 