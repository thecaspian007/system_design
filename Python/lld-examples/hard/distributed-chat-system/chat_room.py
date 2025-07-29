"""
ChatRoom class for the distributed chat system
"""

import uuid
from datetime import datetime
from typing import Set, Optional
from user import User


class ChatRoom:
    """ChatRoom class representing a chat room"""
    
    def __init__(self, name: str, description: str, creator: User):
        self.room_id = str(uuid.uuid4())
        self.name = name
        self.description = description
        self.creator_id = creator.user_id
        self.created_at = datetime.now()
        self.is_active = True
        self.is_private = False
        self.max_members = 1000
        
        # Members and roles
        self.members: Set[str] = set()  # Set of user IDs
        self.admins: Set[str] = set()  # Set of admin user IDs
        self.banned_users: Set[str] = set()  # Set of banned user IDs
        
        # Room settings
        self.is_read_only = False
        self.message_retention_days = 30
        self.allow_file_sharing = True
        self.allow_reactions = True
        
        # Statistics
        self.total_messages = 0
        self.last_activity = datetime.now()
        
        # Add creator as admin
        self.admins.add(creator.user_id)
        self.members.add(creator.user_id)
    
    def add_member(self, user_id: str) -> bool:
        """Add member to room"""
        if user_id in self.banned_users:
            return False
        
        if len(self.members) >= self.max_members:
            return False
        
        self.members.add(user_id)
        self.last_activity = datetime.now()
        return True
    
    def remove_member(self, user_id: str) -> bool:
        """Remove member from room"""
        if user_id in self.members:
            self.members.remove(user_id)
            # Remove from admins if they were admin
            if user_id in self.admins:
                self.admins.remove(user_id)
            self.last_activity = datetime.now()
            return True
        return False
    
    def add_admin(self, user_id: str) -> bool:
        """Add admin to room"""
        if user_id in self.members:
            self.admins.add(user_id)
            return True
        return False
    
    def remove_admin(self, user_id: str) -> bool:
        """Remove admin from room"""
        if user_id in self.admins and user_id != self.creator_id:
            self.admins.remove(user_id)
            return True
        return False
    
    def ban_user(self, user_id: str) -> bool:
        """Ban user from room"""
        if user_id == self.creator_id:
            return False
        
        self.banned_users.add(user_id)
        self.remove_member(user_id)
        return True
    
    def unban_user(self, user_id: str) -> bool:
        """Unban user from room"""
        if user_id in self.banned_users:
            self.banned_users.remove(user_id)
            return True
        return False
    
    def is_member(self, user_id: str) -> bool:
        """Check if user is a member"""
        return user_id in self.members
    
    def is_admin(self, user_id: str) -> bool:
        """Check if user is an admin"""
        return user_id in self.admins
    
    def is_creator(self, user_id: str) -> bool:
        """Check if user is the creator"""
        return user_id == self.creator_id
    
    def is_banned(self, user_id: str) -> bool:
        """Check if user is banned"""
        return user_id in self.banned_users
    
    def can_send_message(self, user_id: str) -> bool:
        """Check if user can send messages"""
        return (self.is_member(user_id) and 
                not self.is_banned(user_id) and 
                (not self.is_read_only or self.is_admin(user_id)))
    
    def get_member_count(self) -> int:
        """Get member count"""
        return len(self.members)
    
    def get_admin_count(self) -> int:
        """Get admin count"""
        return len(self.admins)
    
    def update_activity(self):
        """Update last activity"""
        self.last_activity = datetime.now()
    
    def increment_message_count(self):
        """Increment message count"""
        self.total_messages += 1
        self.update_activity()
    
    def set_read_only(self, read_only: bool):
        """Set room as read-only"""
        self.is_read_only = read_only
    
    def set_private(self, private: bool):
        """Set room as private"""
        self.is_private = private
    
    def update_description(self, description: str):
        """Update room description"""
        self.description = description
    
    def deactivate(self):
        """Deactivate room"""
        self.is_active = False
    
    def activate(self):
        """Activate room"""
        self.is_active = True
    
    def get_room_info(self) -> dict:
        """Get room information"""
        return {
            'room_id': self.room_id,
            'name': self.name,
            'description': self.description,
            'creator_id': self.creator_id,
            'created_at': self.created_at.isoformat(),
            'is_active': self.is_active,
            'is_private': self.is_private,
            'is_read_only': self.is_read_only,
            'member_count': self.get_member_count(),
            'admin_count': self.get_admin_count(),
            'total_messages': self.total_messages,
            'last_activity': self.last_activity.isoformat(),
            'max_members': self.max_members,
            'message_retention_days': self.message_retention_days,
            'allow_file_sharing': self.allow_file_sharing,
            'allow_reactions': self.allow_reactions
        }
    
    def __str__(self):
        return f"ChatRoom(name={self.name}, members={len(self.members)}, active={self.is_active})"
    
    def __repr__(self):
        return self.__str__() 