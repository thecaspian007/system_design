from typing import Dict, Optional
import uuid
from user import User

class UserManager:
    """User manager class for handling user operations"""
    
    def __init__(self):
        self.users: Dict[str, User] = {}
        self.users_by_email: Dict[str, User] = {}
    
    def register_user(self, email: str, name: str, address: str, password: str) -> Optional[User]:
        """Register a new user"""
        if email in self.users_by_email:
            return None  # User already exists
        
        user_id = str(uuid.uuid4())[:8].upper()
        user = User(user_id, email, name, address, password)
        
        self.users[user_id] = user
        self.users_by_email[email] = user
        
        return user
    
    def authenticate_user(self, email: str, password: str) -> Optional[User]:
        """Authenticate user with email and password"""
        user = self.users_by_email.get(email)
        if user and user.validate_password(password):
            return user
        return None
    
    def get_user_by_id(self, user_id: str) -> Optional[User]:
        """Get user by user ID"""
        return self.users.get(user_id)
    
    def get_user_by_email(self, email: str) -> Optional[User]:
        """Get user by email"""
        return self.users_by_email.get(email)
    
    def update_user_profile(self, user_id: str, name: str = None, address: str = None) -> bool:
        """Update user profile"""
        user = self.get_user_by_id(user_id)
        if user:
            if name:
                user.name = name
            if address:
                user.address = address
            return True
        return False
    
    def get_total_users(self) -> int:
        """Get total number of users"""
        return len(self.users)
    
    def user_exists(self, email: str) -> bool:
        """Check if user exists"""
        return email in self.users_by_email 