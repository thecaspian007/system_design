from datetime import datetime
from typing import List, Set, Optional
import uuid

class User:
    """User class representing a social media user"""
    
    def __init__(self, username: str, email: str, display_name: str):
        self.user_id = str(uuid.uuid4())[:8].upper()
        self.username = username
        self.email = email
        self.display_name = display_name
        self.bio = ""
        self.profile_picture = ""
        self.created_at = datetime.now()
        self.is_active = True
        
        # Social connections
        self.followers: Set[str] = set()  # Set of user IDs
        self.following: Set[str] = set()  # Set of user IDs
        
        # Content
        self.posts: List = []  # List of posts
        
        # Reference to social network for operations
        self.social_network = None
    
    def set_social_network(self, social_network):
        """Set reference to social network"""
        self.social_network = social_network
    
    def create_post(self, content: str):
        """Create a new post"""
        if self.social_network:
            return self.social_network.create_post(self, content)
        return None
    
    def follow_user(self, username: str) -> bool:
        """Follow another user"""
        if self.social_network:
            return self.social_network.follow_user(self, username)
        return False
    
    def unfollow_user(self, username: str) -> bool:
        """Unfollow a user"""
        if self.social_network:
            return self.social_network.unfollow_user(self, username)
        return False
    
    def get_followers(self) -> List['User']:
        """Get list of followers"""
        if self.social_network:
            return [self.social_network.get_user_by_id(user_id) for user_id in self.followers if self.social_network.get_user_by_id(user_id)]
        return []
    
    def get_following(self) -> List['User']:
        """Get list of users being followed"""
        if self.social_network:
            return [self.social_network.get_user_by_id(user_id) for user_id in self.following if self.social_network.get_user_by_id(user_id)]
        return []
    
    def get_news_feed(self, limit: int = 10) -> List:
        """Get news feed"""
        if self.social_network:
            return self.social_network.get_news_feed(self, limit)
        return []
    
    def get_follower_count(self) -> int:
        """Get follower count"""
        return len(self.followers)
    
    def get_following_count(self) -> int:
        """Get following count"""
        return len(self.following)
    
    def get_post_count(self) -> int:
        """Get post count"""
        return len(self.posts)
    
    def update_bio(self, bio: str):
        """Update user bio"""
        self.bio = bio
    
    def update_profile_picture(self, picture_url: str):
        """Update profile picture"""
        self.profile_picture = picture_url
    
    def deactivate(self):
        """Deactivate user account"""
        self.is_active = False
    
    def activate(self):
        """Activate user account"""
        self.is_active = True
    
    def __str__(self):
        return f"@{self.username} - {self.display_name}" 