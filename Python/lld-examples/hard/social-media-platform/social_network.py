from typing import List, Dict, Optional
from user import User
from post import Post, Comment

class SocialNetwork:
    """Social network class managing users and posts"""
    
    def __init__(self):
        self.users: Dict[str, User] = {}  # username -> User
        self.users_by_id: Dict[str, User] = {}  # user_id -> User
        self.posts: Dict[str, Post] = {}  # post_id -> Post
        self.user_posts: Dict[str, List[Post]] = {}  # user_id -> List of posts
    
    def create_user(self, username: str, email: str, display_name: str) -> Optional[User]:
        """Create a new user"""
        if username in self.users:
            return None  # User already exists
        
        user = User(username, email, display_name)
        user.set_social_network(self)
        
        self.users[username] = user
        self.users_by_id[user.user_id] = user
        self.user_posts[user.user_id] = []
        
        return user
    
    def get_user(self, username: str) -> Optional[User]:
        """Get user by username"""
        return self.users.get(username)
    
    def get_user_by_id(self, user_id: str) -> Optional[User]:
        """Get user by user ID"""
        return self.users_by_id.get(user_id)
    
    def create_post(self, author: User, content: str) -> Optional[Post]:
        """Create a new post"""
        if not author.is_active:
            return None
        
        post = Post(author, content)
        self.posts[post.post_id] = post
        self.user_posts[author.user_id].append(post)
        author.posts.append(post)
        
        return post
    
    def get_post(self, post_id: str) -> Optional[Post]:
        """Get post by ID"""
        return self.posts.get(post_id)
    
    def delete_post(self, post_id: str, user: User) -> bool:
        """Delete a post"""
        post = self.get_post(post_id)
        if post and post.author.user_id == user.user_id:
            post.delete_post()
            return True
        return False
    
    def follow_user(self, follower: User, username: str) -> bool:
        """Follow a user"""
        user_to_follow = self.get_user(username)
        
        if (user_to_follow and 
            user_to_follow.user_id != follower.user_id and 
            user_to_follow.user_id not in follower.following):
            
            follower.following.add(user_to_follow.user_id)
            user_to_follow.followers.add(follower.user_id)
            return True
        
        return False
    
    def unfollow_user(self, follower: User, username: str) -> bool:
        """Unfollow a user"""
        user_to_unfollow = self.get_user(username)
        
        if (user_to_unfollow and 
            user_to_unfollow.user_id in follower.following):
            
            follower.following.remove(user_to_unfollow.user_id)
            user_to_unfollow.followers.remove(follower.user_id)
            return True
        
        return False
    
    def get_news_feed(self, user: User, limit: int = 10) -> List[Post]:
        """Get news feed for user"""
        feed_posts = []
        
        # Get posts from followed users
        for followed_user_id in user.following:
            user_posts = self.user_posts.get(followed_user_id, [])
            for post in user_posts:
                if not post.is_deleted:
                    feed_posts.append(post)
        
        # Add user's own posts
        user_posts = self.user_posts.get(user.user_id, [])
        for post in user_posts:
            if not post.is_deleted:
                feed_posts.append(post)
        
        # Sort by timestamp (most recent first)
        feed_posts.sort(key=lambda p: p.timestamp, reverse=True)
        
        return feed_posts[:limit]
    
    def search_users(self, query: str) -> List[User]:
        """Search users by username or display name"""
        query = query.lower()
        results = []
        
        for user in self.users.values():
            if (query in user.username.lower() or 
                query in user.display_name.lower()):
                results.append(user)
        
        return results
    
    def get_trending_posts(self, limit: int = 10) -> List[Post]:
        """Get trending posts based on engagement"""
        all_posts = [post for post in self.posts.values() if not post.is_deleted]
        
        # Sort by engagement score
        all_posts.sort(key=lambda p: p.get_engagement_score(), reverse=True)
        
        return all_posts[:limit]
    
    def get_user_posts(self, user: User, limit: int = 20) -> List[Post]:
        """Get posts by user"""
        user_posts = self.user_posts.get(user.user_id, [])
        active_posts = [post for post in user_posts if not post.is_deleted]
        
        # Sort by timestamp (most recent first)
        active_posts.sort(key=lambda p: p.timestamp, reverse=True)
        
        return active_posts[:limit]
    
    def get_total_users(self) -> int:
        """Get total number of users"""
        return len(self.users)
    
    def get_total_posts(self) -> int:
        """Get total number of posts"""
        return len([post for post in self.posts.values() if not post.is_deleted])
    
    def get_user_statistics(self, user: User) -> Dict:
        """Get user statistics"""
        return {
            'posts': len(self.user_posts.get(user.user_id, [])),
            'followers': len(user.followers),
            'following': len(user.following),
            'total_likes': sum(post.get_like_count() for post in self.user_posts.get(user.user_id, []))
        } 