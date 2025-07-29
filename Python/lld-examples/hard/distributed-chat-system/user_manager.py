"""
UserManager class for the distributed chat system
"""

from typing import Dict, List, Optional
from user import User


class UserManager:
    """UserManager class for managing users"""
    
    def __init__(self):
        self.users: Dict[str, User] = {}  # username -> User
        self.users_by_id: Dict[str, User] = {}  # user_id -> User
        self.users_by_email: Dict[str, User] = {}  # email -> User
    
    def create_user(self, username: str, email: str, display_name: str) -> Optional[User]:
        """Create a new user"""
        if username in self.users:
            return None  # Username already exists
        
        if email in self.users_by_email:
            return None  # Email already exists
        
        user = User(username, email, display_name)
        
        # Store user in all indexes
        self.users[username] = user
        self.users_by_id[user.user_id] = user
        self.users_by_email[email] = user
        
        return user
    
    def get_user(self, username: str) -> Optional[User]:
        """Get user by username"""
        return self.users.get(username)
    
    def get_user_by_id(self, user_id: str) -> Optional[User]:
        """Get user by user ID"""
        return self.users_by_id.get(user_id)
    
    def get_user_by_email(self, email: str) -> Optional[User]:
        """Get user by email"""
        return self.users_by_email.get(email)
    
    def get_all_users(self) -> List[User]:
        """Get all users"""
        return list(self.users.values())
    
    def get_online_users(self) -> List[User]:
        """Get online users"""
        return [user for user in self.users.values() if user.is_online]
    
    def get_offline_users(self) -> List[User]:
        """Get offline users"""
        return [user for user in self.users.values() if not user.is_online]
    
    def update_user_status(self, user_id: str, is_online: bool) -> bool:
        """Update user online status"""
        user = self.get_user_by_id(user_id)
        if user:
            if is_online:
                user.set_online("", "")  # Will be set properly by server
            else:
                user.set_offline()
            return True
        return False
    
    def search_users(self, query: str) -> List[User]:
        """Search users by username or display name"""
        query = query.lower()
        results = []
        
        for user in self.users.values():
            if (query in user.username.lower() or 
                query in user.display_name.lower()):
                results.append(user)
        
        return results
    
    def get_user_statistics(self) -> dict:
        """Get user statistics"""
        total_users = len(self.users)
        online_users = len(self.get_online_users())
        offline_users = total_users - online_users
        
        return {
            'total_users': total_users,
            'online_users': online_users,
            'offline_users': offline_users,
            'online_percentage': (online_users / total_users * 100) if total_users > 0 else 0
        }
    
    def delete_user(self, user_id: str) -> bool:
        """Delete a user"""
        user = self.get_user_by_id(user_id)
        if user:
            # Remove from all indexes
            del self.users[user.username]
            del self.users_by_id[user.user_id]
            del self.users_by_email[user.email]
            return True
        return False
    
    def update_user_display_name(self, user_id: str, display_name: str) -> bool:
        """Update user display name"""
        user = self.get_user_by_id(user_id)
        if user:
            user.display_name = display_name
            return True
        return False
    
    def update_user_status_message(self, user_id: str, status_message: str) -> bool:
        """Update user status message"""
        user = self.get_user_by_id(user_id)
        if user:
            user.set_status_message(status_message)
            return True
        return False
    
    def user_exists(self, username: str) -> bool:
        """Check if user exists"""
        return username in self.users
    
    def email_exists(self, email: str) -> bool:
        """Check if email exists"""
        return email in self.users_by_email
    
    def get_users_by_server(self, server_id: str) -> List[User]:
        """Get users connected to a specific server"""
        return [user for user in self.users.values() if user.server_id == server_id]
    
    def __str__(self):
        return f"UserManager(users={len(self.users)}, online={len(self.get_online_users())})"
    
    def __repr__(self):
        return self.__str__() 