from datetime import datetime
from typing import List, Set
import uuid

class Post:
    """Post class representing a social media post"""
    
    def __init__(self, author, content: str):
        self.post_id = str(uuid.uuid4())[:8].upper()
        self.author = author
        self.content = content
        self.timestamp = datetime.now()
        self.likes: Set[str] = set()  # Set of user IDs who liked
        self.comments: List = []  # List of comments
        self.shares: Set[str] = set()  # Set of user IDs who shared
        self.is_deleted = False
        self.edited_at = None
        self.media_attachments: List[str] = []  # List of media URLs
    
    def like_post(self, user_id: str) -> bool:
        """Like the post"""
        if user_id not in self.likes:
            self.likes.add(user_id)
            return True
        return False
    
    def unlike_post(self, user_id: str) -> bool:
        """Unlike the post"""
        if user_id in self.likes:
            self.likes.remove(user_id)
            return True
        return False
    
    def is_liked_by(self, user_id: str) -> bool:
        """Check if post is liked by user"""
        return user_id in self.likes
    
    def add_comment(self, comment):
        """Add comment to post"""
        self.comments.append(comment)
    
    def remove_comment(self, comment_id: str):
        """Remove comment from post"""
        self.comments = [c for c in self.comments if c.comment_id != comment_id]
    
    def share_post(self, user_id: str) -> bool:
        """Share the post"""
        if user_id not in self.shares:
            self.shares.add(user_id)
            return True
        return False
    
    def edit_content(self, new_content: str):
        """Edit post content"""
        self.content = new_content
        self.edited_at = datetime.now()
    
    def delete_post(self):
        """Delete the post"""
        self.is_deleted = True
    
    def restore_post(self):
        """Restore deleted post"""
        self.is_deleted = False
    
    def add_media(self, media_url: str):
        """Add media attachment"""
        self.media_attachments.append(media_url)
    
    def get_like_count(self) -> int:
        """Get like count"""
        return len(self.likes)
    
    def get_comment_count(self) -> int:
        """Get comment count"""
        return len(self.comments)
    
    def get_share_count(self) -> int:
        """Get share count"""
        return len(self.shares)
    
    def get_formatted_timestamp(self) -> str:
        """Get formatted timestamp"""
        return self.timestamp.strftime("%Y-%m-%d %H:%M:%S")
    
    def get_engagement_score(self) -> int:
        """Get engagement score (likes + comments + shares)"""
        return self.get_like_count() + self.get_comment_count() + self.get_share_count()
    
    def __str__(self):
        return f"Post by @{self.author.username}: {self.content[:50]}..."

class Comment:
    """Comment class representing a comment on a post"""
    
    def __init__(self, author, content: str, post_id: str):
        self.comment_id = str(uuid.uuid4())[:8].upper()
        self.author = author
        self.content = content
        self.post_id = post_id
        self.timestamp = datetime.now()
        self.likes: Set[str] = set()
        self.replies: List = []  # List of reply comments
        self.is_deleted = False
    
    def like_comment(self, user_id: str) -> bool:
        """Like the comment"""
        if user_id not in self.likes:
            self.likes.add(user_id)
            return True
        return False
    
    def unlike_comment(self, user_id: str) -> bool:
        """Unlike the comment"""
        if user_id in self.likes:
            self.likes.remove(user_id)
            return True
        return False
    
    def add_reply(self, reply):
        """Add reply to comment"""
        self.replies.append(reply)
    
    def delete_comment(self):
        """Delete the comment"""
        self.is_deleted = True
    
    def get_like_count(self) -> int:
        """Get like count"""
        return len(self.likes)
    
    def get_formatted_timestamp(self) -> str:
        """Get formatted timestamp"""
        return self.timestamp.strftime("%Y-%m-%d %H:%M:%S")
    
    def __str__(self):
        return f"Comment by @{self.author.username}: {self.content[:30]}..." 