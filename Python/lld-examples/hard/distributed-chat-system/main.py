"""
Distributed Chat System - Main Application

This is a comprehensive distributed chat system that demonstrates:
- User registration and authentication
- Real-time messaging simulation
- Chat rooms and private messaging
- Message history and persistence
- User presence and status management
- Message broadcasting and delivery
- Load balancing and scalability concepts

Features:
- Multi-server architecture simulation
- Message queuing and delivery
- User session management
- Chat room management
- Private messaging
- Message history
- User presence tracking
- Notification system

@author System Design Repository
"""

import sys
import os
from datetime import datetime
from typing import Dict, List, Optional
import threading
import time
import uuid

# Add the current directory to the Python path
sys.path.append(os.path.dirname(os.path.abspath(__file__)))

from user_manager import UserManager
from chat_server import ChatServer
from message_service import MessageService
from notification_service import NotificationService
from room_manager import RoomManager
from user import User
from message import Message, MessageType
from chat_room import ChatRoom


class DistributedChatSystem:
    """
    Distributed Chat System - Main Application
    
    This system demonstrates:
    - Multi-server chat architecture
    - Real-time message delivery
    - Chat room management
    - User presence tracking
    - Message history persistence
    - Notification system
    
    @author System Design Repository
    """
    
    def __init__(self):
        self.user_manager = UserManager()
        self.room_manager = RoomManager()
        self.message_service = MessageService()
        self.notification_service = NotificationService()
        
        # Simulate multiple chat servers
        self.chat_servers = {
            'server1': ChatServer('server1', 'us-east-1'),
            'server2': ChatServer('server2', 'us-west-1'),
            'server3': ChatServer('server3', 'eu-west-1')
        }
        
        # Load balancer simulation
        self.current_server_index = 0
        self.server_list = list(self.chat_servers.keys())
        
        # Current user session
        self.current_user = None
        self.current_server = None
        
        # Initialize system
        self._initialize_system()
        
        # Start background services
        self._start_background_services()
    
    def _initialize_system(self):
        """Initialize system with sample data"""
        # Create sample users
        sample_users = [
            ("alice", "alice@example.com", "Alice Johnson"),
            ("bob", "bob@example.com", "Bob Smith"),
            ("charlie", "charlie@example.com", "Charlie Brown"),
            ("diana", "diana@example.com", "Diana Prince"),
            ("eve", "eve@example.com", "Eve Wilson")
        ]
        
        for username, email, display_name in sample_users:
            user = self.user_manager.create_user(username, email, display_name)
            # Assign user to a server (load balancing)
            server = self._get_next_server()
            self.chat_servers[server].add_user(user)
        
        # Create sample chat rooms
        sample_rooms = [
            ("general", "General Discussion", "alice"),
            ("tech", "Technology Talk", "bob"),
            ("random", "Random Chat", "charlie")
        ]
        
        for room_name, description, creator_username in sample_rooms:
            creator = self.user_manager.get_user(creator_username)
            if creator:
                room = self.room_manager.create_room(room_name, description, creator)
                
                # Add some users to rooms
                for user in self.user_manager.get_all_users()[:3]:
                    self.room_manager.join_room(room.room_id, user.user_id)
        
        print("Distributed Chat System initialized!")
        print(f"Servers: {list(self.chat_servers.keys())}")
        print(f"Users: {len(self.user_manager.get_all_users())}")
        print(f"Rooms: {len(self.room_manager.get_all_rooms())}")
    
    def _start_background_services(self):
        """Start background services"""
        # Start message delivery service
        message_thread = threading.Thread(target=self._message_delivery_loop, daemon=True)
        message_thread.start()
        
        # Start presence update service
        presence_thread = threading.Thread(target=self._presence_update_loop, daemon=True)
        presence_thread.start()
        
        # Start notification service
        notification_thread = threading.Thread(target=self._notification_loop, daemon=True)
        notification_thread.start()
    
    def _message_delivery_loop(self):
        """Background message delivery loop"""
        while True:
            try:
                # Process pending messages
                self.message_service.process_pending_messages()
                time.sleep(1)
            except Exception as e:
                print(f"Message delivery error: {e}")
    
    def _presence_update_loop(self):
        """Background presence update loop"""
        while True:
            try:
                # Update user presence
                for server in self.chat_servers.values():
                    server.update_user_presence()
                time.sleep(5)
            except Exception as e:
                print(f"Presence update error: {e}")
    
    def _notification_loop(self):
        """Background notification loop"""
        while True:
            try:
                # Process notifications
                self.notification_service.process_notifications()
                time.sleep(2)
            except Exception as e:
                print(f"Notification error: {e}")
    
    def _get_next_server(self) -> str:
        """Get next server for load balancing"""
        server = self.server_list[self.current_server_index]
        self.current_server_index = (self.current_server_index + 1) % len(self.server_list)
        return server
    
    def start(self):
        """Start the chat system"""
        print("=== Welcome to Distributed Chat System ===")
        
        while True:
            try:
                if self.current_user is None:
                    self._show_login_menu()
                else:
                    self._show_main_menu()
                
                choice = self._get_int_input()
                
                if self.current_user is None:
                    self._handle_login_choice(choice)
                else:
                    self._handle_main_choice(choice)
                    
            except KeyboardInterrupt:
                print("\nGoodbye!")
                break
            except Exception as e:
                print(f"Error: {e}")
    
    def _show_login_menu(self):
        """Show login menu"""
        print("\n--- Login Menu ---")
        print("1. Login")
        print("2. Register")
        print("3. View System Status")
        print("4. Exit")
        print("Choose an option: ", end="")
    
    def _show_main_menu(self):
        """Show main menu"""
        print(f"\n--- Welcome, {self.current_user.display_name} ---")
        print(f"Server: {self.current_server}")
        print("1. Join Chat Room")
        print("2. Create Chat Room")
        print("3. Send Private Message")
        print("4. View Messages")
        print("5. View Online Users")
        print("6. View Chat Rooms")
        print("7. User Profile")
        print("8. System Status")
        print("9. Logout")
        print("Choose an option: ", end="")
    
    def _handle_login_choice(self, choice: int):
        """Handle login menu choice"""
        if choice == 1:
            self._login()
        elif choice == 2:
            self._register()
        elif choice == 3:
            self._view_system_status()
        elif choice == 4:
            print("Goodbye!")
            sys.exit(0)
        else:
            print("Invalid choice. Please try again.")
    
    def _handle_main_choice(self, choice: int):
        """Handle main menu choice"""
        if choice == 1:
            self._join_chat_room()
        elif choice == 2:
            self._create_chat_room()
        elif choice == 3:
            self._send_private_message()
        elif choice == 4:
            self._view_messages()
        elif choice == 5:
            self._view_online_users()
        elif choice == 6:
            self._view_chat_rooms()
        elif choice == 7:
            self._user_profile()
        elif choice == 8:
            self._view_system_status()
        elif choice == 9:
            self._logout()
        else:
            print("Invalid choice. Please try again.")
    
    def _login(self):
        """Handle user login"""
        print("\n--- Login ---")
        username = input("Enter username: ")
        
        user = self.user_manager.get_user(username)
        if user:
            # Find which server the user is on
            server_name = None
            for name, server in self.chat_servers.items():
                if server.has_user(user.user_id):
                    server_name = name
                    break
            
            if server_name:
                self.current_user = user
                self.current_server = server_name
                self.chat_servers[server_name].connect_user(user.user_id)
                print(f"Login successful! Connected to {server_name}")
            else:
                print("User not found on any server.")
        else:
            print("User not found.")
    
    def _register(self):
        """Handle user registration"""
        print("\n--- Register ---")
        username = input("Enter username: ")
        email = input("Enter email: ")
        display_name = input("Enter display name: ")
        
        if self.user_manager.get_user(username):
            print("Username already exists.")
            return
        
        user = self.user_manager.create_user(username, email, display_name)
        if user:
            # Assign to a server
            server = self._get_next_server()
            self.chat_servers[server].add_user(user)
            print(f"Registration successful! Assigned to {server}")
        else:
            print("Registration failed.")
    
    def _join_chat_room(self):
        """Join a chat room"""
        print("\n--- Join Chat Room ---")
        
        # Show available rooms
        rooms = self.room_manager.get_all_rooms()
        if not rooms:
            print("No chat rooms available.")
            return
        
        print("Available rooms:")
        for i, room in enumerate(rooms, 1):
            member_count = len(room.members)
            print(f"{i}. {room.name} - {room.description} ({member_count} members)")
        
        choice = self._get_int_input("Select room number: ")
        if 1 <= choice <= len(rooms):
            selected_room = rooms[choice - 1]
            
            if self.room_manager.join_room(selected_room.room_id, self.current_user.user_id):
                print(f"Joined room: {selected_room.name}")
                self._chat_in_room(selected_room)
            else:
                print("Failed to join room.")
        else:
            print("Invalid choice.")
    
    def _chat_in_room(self, room: ChatRoom):
        """Chat in a room"""
        print(f"\n--- Chat in {room.name} ---")
        print("Type 'exit' to leave the room")
        print("Type 'users' to see online users")
        print("Type 'history' to see message history")
        print("---")
        
        # Show recent messages
        messages = self.message_service.get_room_messages(room.room_id, limit=10)
        for msg in messages:
            print(f"[{msg.timestamp.strftime('%H:%M')}] {msg.sender.display_name}: {msg.content}")
        
        while True:
            try:
                message_text = input(f"{self.current_user.display_name}: ")
                
                if message_text.lower() == 'exit':
                    break
                elif message_text.lower() == 'users':
                    self._show_room_users(room)
                elif message_text.lower() == 'history':
                    self._show_room_history(room)
                elif message_text.strip():
                    # Send message
                    message = Message(
                        sender=self.current_user,
                        content=message_text,
                        message_type=MessageType.ROOM_MESSAGE,
                        room_id=room.room_id
                    )
                    
                    self.message_service.send_message(message)
                    
                    # Broadcast to room members
                    self._broadcast_to_room(room, message)
                    
            except KeyboardInterrupt:
                break
    
    def _show_room_users(self, room: ChatRoom):
        """Show users in the room"""
        print(f"\n--- Users in {room.name} ---")
        for user_id in room.members:
            user = self.user_manager.get_user_by_id(user_id)
            if user:
                status = "Online" if user.is_online else "Offline"
                print(f"- {user.display_name} ({status})")
    
    def _show_room_history(self, room: ChatRoom):
        """Show room message history"""
        print(f"\n--- Message History for {room.name} ---")
        messages = self.message_service.get_room_messages(room.room_id, limit=20)
        
        for msg in messages:
            print(f"[{msg.timestamp.strftime('%Y-%m-%d %H:%M')}] {msg.sender.display_name}: {msg.content}")
    
    def _broadcast_to_room(self, room: ChatRoom, message: Message):
        """Broadcast message to room members"""
        for user_id in room.members:
            if user_id != self.current_user.user_id:  # Don't send to sender
                user = self.user_manager.get_user_by_id(user_id)
                if user and user.is_online:
                    self.notification_service.add_notification(
                        user_id, 
                        f"[{room.name}] {message.sender.display_name}: {message.content}"
                    )
    
    def _create_chat_room(self):
        """Create a new chat room"""
        print("\n--- Create Chat Room ---")
        room_name = input("Enter room name: ")
        description = input("Enter room description: ")
        
        if self.room_manager.get_room(room_name):
            print("Room name already exists.")
            return
        
        room = self.room_manager.create_room(room_name, description, self.current_user)
        if room:
            print(f"Chat room '{room_name}' created successfully!")
            self.room_manager.join_room(room.room_id, self.current_user.user_id)
        else:
            print("Failed to create room.")
    
    def _send_private_message(self):
        """Send private message"""
        print("\n--- Send Private Message ---")
        
        # Show online users
        online_users = [user for user in self.user_manager.get_all_users() 
                       if user.is_online and user.user_id != self.current_user.user_id]
        
        if not online_users:
            print("No online users available.")
            return
        
        print("Online users:")
        for i, user in enumerate(online_users, 1):
            print(f"{i}. {user.display_name} (@{user.username})")
        
        choice = self._get_int_input("Select user number: ")
        if 1 <= choice <= len(online_users):
            recipient = online_users[choice - 1]
            
            message_text = input(f"Message to {recipient.display_name}: ")
            if message_text.strip():
                message = Message(
                    sender=self.current_user,
                    content=message_text,
                    message_type=MessageType.PRIVATE_MESSAGE,
                    recipient_id=recipient.user_id
                )
                
                self.message_service.send_message(message)
                
                # Notify recipient
                self.notification_service.add_notification(
                    recipient.user_id,
                    f"Private message from {self.current_user.display_name}: {message_text}"
                )
                
                print("Message sent!")
        else:
            print("Invalid choice.")
    
    def _view_messages(self):
        """View messages"""
        print("\n--- View Messages ---")
        print("1. Private Messages")
        print("2. Room Messages")
        print("3. All Messages")
        
        choice = self._get_int_input("Choose option: ")
        
        if choice == 1:
            messages = self.message_service.get_private_messages(self.current_user.user_id)
            print("\n--- Private Messages ---")
        elif choice == 2:
            print("\n--- Room Messages ---")
            # Show user's rooms
            user_rooms = self.room_manager.get_user_rooms(self.current_user.user_id)
            if not user_rooms:
                print("You are not in any rooms.")
                return
            
            for i, room in enumerate(user_rooms, 1):
                print(f"{i}. {room.name}")
            
            room_choice = self._get_int_input("Select room: ")
            if 1 <= room_choice <= len(user_rooms):
                selected_room = user_rooms[room_choice - 1]
                messages = self.message_service.get_room_messages(selected_room.room_id)
                print(f"\n--- Messages in {selected_room.name} ---")
            else:
                print("Invalid choice.")
                return
        elif choice == 3:
            messages = self.message_service.get_user_messages(self.current_user.user_id)
            print("\n--- All Messages ---")
        else:
            print("Invalid choice.")
            return
        
        # Display messages
        for msg in messages[-20:]:  # Show last 20 messages
            if msg.message_type == MessageType.PRIVATE_MESSAGE:
                if msg.sender.user_id == self.current_user.user_id:
                    print(f"[{msg.timestamp.strftime('%Y-%m-%d %H:%M')}] To {msg.recipient.display_name}: {msg.content}")
                else:
                    print(f"[{msg.timestamp.strftime('%Y-%m-%d %H:%M')}] From {msg.sender.display_name}: {msg.content}")
            else:
                room = self.room_manager.get_room_by_id(msg.room_id)
                room_name = room.name if room else "Unknown"
                print(f"[{msg.timestamp.strftime('%Y-%m-%d %H:%M')}] [{room_name}] {msg.sender.display_name}: {msg.content}")
    
    def _view_online_users(self):
        """View online users"""
        print("\n--- Online Users ---")
        
        for server_name, server in self.chat_servers.items():
            online_users = server.get_online_users()
            if online_users:
                print(f"\n{server_name}:")
                for user_id in online_users:
                    user = self.user_manager.get_user_by_id(user_id)
                    if user:
                        status = "🟢" if user.is_online else "🔴"
                        print(f"  {status} {user.display_name} (@{user.username})")
    
    def _view_chat_rooms(self):
        """View chat rooms"""
        print("\n--- Chat Rooms ---")
        
        rooms = self.room_manager.get_all_rooms()
        if not rooms:
            print("No chat rooms available.")
            return
        
        for room in rooms:
            member_count = len(room.members)
            creator = self.user_manager.get_user_by_id(room.creator_id)
            creator_name = creator.display_name if creator else "Unknown"
            
            print(f"\n📍 {room.name}")
            print(f"   Description: {room.description}")
            print(f"   Creator: {creator_name}")
            print(f"   Members: {member_count}")
            print(f"   Created: {room.created_at.strftime('%Y-%m-%d %H:%M')}")
            
            # Show if user is a member
            if self.current_user.user_id in room.members:
                print(f"   Status: ✅ Member")
            else:
                print(f"   Status: ⏸️ Not a member")
    
    def _user_profile(self):
        """View user profile"""
        print(f"\n--- User Profile ---")
        print(f"Username: {self.current_user.username}")
        print(f"Display Name: {self.current_user.display_name}")
        print(f"Email: {self.current_user.email}")
        print(f"Status: {'Online' if self.current_user.is_online else 'Offline'}")
        print(f"Server: {self.current_server}")
        print(f"Created: {self.current_user.created_at.strftime('%Y-%m-%d %H:%M')}")
        print(f"Last Seen: {self.current_user.last_seen.strftime('%Y-%m-%d %H:%M')}")
        
        # Show user's rooms
        user_rooms = self.room_manager.get_user_rooms(self.current_user.user_id)
        print(f"\nRooms Joined: {len(user_rooms)}")
        for room in user_rooms:
            print(f"  - {room.name}")
        
        # Show message count
        message_count = len(self.message_service.get_user_messages(self.current_user.user_id))
        print(f"\nMessages Sent: {message_count}")
    
    def _view_system_status(self):
        """View system status"""
        print("\n--- System Status ---")
        
        # Overall statistics
        total_users = len(self.user_manager.get_all_users())
        online_users = len([u for u in self.user_manager.get_all_users() if u.is_online])
        total_rooms = len(self.room_manager.get_all_rooms())
        total_messages = len(self.message_service.get_all_messages())
        
        print(f"Total Users: {total_users}")
        print(f"Online Users: {online_users}")
        print(f"Total Rooms: {total_rooms}")
        print(f"Total Messages: {total_messages}")
        
        # Server statistics
        print(f"\n--- Server Statistics ---")
        for server_name, server in self.chat_servers.items():
            server_users = len(server.users)
            server_online = len(server.get_online_users())
            print(f"{server_name} ({server.region}): {server_users} users, {server_online} online")
        
        # Recent activity
        print(f"\n--- Recent Activity ---")
        recent_messages = self.message_service.get_all_messages()[-5:]
        for msg in recent_messages:
            print(f"[{msg.timestamp.strftime('%H:%M')}] {msg.sender.display_name}: {msg.content[:50]}...")
    
    def _logout(self):
        """Logout user"""
        if self.current_user and self.current_server:
            self.chat_servers[self.current_server].disconnect_user(self.current_user.user_id)
            print(f"Logged out from {self.current_server}")
        
        self.current_user = None
        self.current_server = None
    
    def _get_int_input(self, prompt="Enter number: "):
        """Get integer input with validation"""
        while True:
            try:
                return int(input(prompt))
            except ValueError:
                print("Please enter a valid number.")


if __name__ == "__main__":
    chat_system = DistributedChatSystem()
    chat_system.start() 