import sys
import os
from datetime import datetime

sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from user import User
from post import Post
from social_network import SocialNetwork

class SocialMediaPlatform:
    """
    Social Media Platform - Main Application
    
    This system demonstrates:
    - User management and authentication
    - Post creation and management
    - Following/follower relationships
    - News feed generation
    - Comment and like functionality
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.social_network = SocialNetwork()
        self.current_user = None
        self._initialize_platform()
    
    def _initialize_platform(self):
        """Initialize platform with sample data"""
        # Create sample users
        user1 = self.social_network.create_user("john_doe", "john@example.com", "John Doe")
        user2 = self.social_network.create_user("jane_smith", "jane@example.com", "Jane Smith")
        user3 = self.social_network.create_user("bob_wilson", "bob@example.com", "Bob Wilson")
        
        # Create sample posts
        if user1:
            user1.create_post("Hello world! This is my first post.")
            user1.create_post("Beautiful sunset today!")
        
        if user2:
            user2.create_post("Just finished reading an amazing book!")
            user2.create_post("Coffee and coding - perfect combination!")
        
        print("Social media platform initialized!")
    
    def start(self):
        """Start the social media platform"""
        print("=== Welcome to Social Media Platform ===")
        
        while True:
            try:
                if self.current_user is None:
                    self._show_guest_menu()
                else:
                    self._show_user_menu()
                
                choice = self._get_int_input()
                
                if self.current_user is None:
                    self._handle_guest_choice(choice)
                else:
                    self._handle_user_choice(choice)
                    
            except KeyboardInterrupt:
                print("\nThank you for using the social media platform!")
                break
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_guest_menu(self):
        """Show guest menu"""
        print("\n--- Guest Menu ---")
        print("1. Login")
        print("2. Register")
        print("3. Exit")
        print("Choose an option: ", end="")
    
    def _show_user_menu(self):
        """Show user menu"""
        print(f"\n--- Welcome, {self.current_user.display_name} ---")
        print("1. View Profile")
        print("2. Create Post")
        print("3. View News Feed")
        print("4. Search Users")
        print("5. Follow User")
        print("6. View Followers")
        print("7. View Following")
        print("8. Logout")
        print("Choose an option: ", end="")
    
    def _handle_guest_choice(self, choice: int):
        """Handle guest menu choice"""
        if choice == 1:
            self._login()
        elif choice == 2:
            self._register()
        elif choice == 3:
            print("Thank you for visiting!")
            exit(0)
        else:
            print("Invalid choice. Please try again.")
    
    def _handle_user_choice(self, choice: int):
        """Handle user menu choice"""
        if choice == 1:
            self._view_profile()
        elif choice == 2:
            self._create_post()
        elif choice == 3:
            self._view_news_feed()
        elif choice == 4:
            self._search_users()
        elif choice == 5:
            self._follow_user()
        elif choice == 6:
            self._view_followers()
        elif choice == 7:
            self._view_following()
        elif choice == 8:
            self._logout()
        else:
            print("Invalid choice. Please try again.")
    
    def _login(self):
        """Handle user login"""
        print("\n--- Login ---")
        username = input("Enter username: ")
        
        user = self.social_network.get_user(username)
        if user:
            self.current_user = user
            print(f"Login successful! Welcome, {user.display_name}")
        else:
            print("User not found.")
    
    def _register(self):
        """Handle user registration"""
        print("\n--- Register ---")
        username = input("Enter username: ")
        email = input("Enter email: ")
        display_name = input("Enter display name: ")
        
        user = self.social_network.create_user(username, email, display_name)
        if user:
            print("Registration successful! You can now login.")
        else:
            print("Registration failed. Username might already exist.")
    
    def _view_profile(self):
        """View user profile"""
        print("\n--- Profile ---")
        print(f"Username: {self.current_user.username}")
        print(f"Display Name: {self.current_user.display_name}")
        print(f"Email: {self.current_user.email}")
        print(f"Posts: {len(self.current_user.posts)}")
        print(f"Followers: {len(self.current_user.followers)}")
        print(f"Following: {len(self.current_user.following)}")
    
    def _create_post(self):
        """Create a new post"""
        print("\n--- Create Post ---")
        content = input("Enter post content: ")
        
        post = self.current_user.create_post(content)
        if post:
            print("Post created successfully!")
        else:
            print("Failed to create post.")
    
    def _view_news_feed(self):
        """View news feed"""
        print("\n--- News Feed ---")
        feed = self.current_user.get_news_feed()
        
        if not feed:
            print("No posts in your news feed.")
            return
        
        for post in feed:
            print(f"@{post.author.username}: {post.content}")
            print(f"Posted: {post.get_formatted_timestamp()}")
            print(f"Likes: {len(post.likes)}")
            print("---")
    
    def _search_users(self):
        """Search for users"""
        print("\n--- Search Users ---")
        query = input("Enter search query: ")
        
        users = self.social_network.search_users(query)
        
        if not users:
            print("No users found.")
            return
        
        print("Found users:")
        for user in users:
            print(f"@{user.username} - {user.display_name}")
    
    def _follow_user(self):
        """Follow a user"""
        print("\n--- Follow User ---")
        username = input("Enter username to follow: ")
        
        if self.current_user.follow_user(username):
            print(f"Successfully followed @{username}")
        else:
            print("Failed to follow user.")
    
    def _view_followers(self):
        """View followers"""
        print("\n--- Followers ---")
        followers = self.current_user.get_followers()
        
        if not followers:
            print("No followers.")
            return
        
        for follower in followers:
            print(f"@{follower.username} - {follower.display_name}")
    
    def _view_following(self):
        """View following"""
        print("\n--- Following ---")
        following = self.current_user.get_following()
        
        if not following:
            print("Not following anyone.")
            return
        
        for user in following:
            print(f"@{user.username} - {user.display_name}")
    
    def _logout(self):
        """Logout user"""
        self.current_user = None
        print("Logged out successfully!")
    
    def _get_int_input(self, prompt="") -> int:
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number: ", end="")

if __name__ == "__main__":
    platform = SocialMediaPlatform()
    platform.start()
