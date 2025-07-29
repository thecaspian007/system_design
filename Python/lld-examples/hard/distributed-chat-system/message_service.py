"""
MessageService class for the distributed chat system
"""

from typing import Dict, List, Optional, Set
from datetime import datetime, timedelta
import threading
import time
from collections import defaultdict, deque

from message import Message, MessageType, MessageStatus
from user import User


class MessageService:
    """MessageService class for managing messages"""
    
    def __init__(self):
        # Message storage
        self.messages: Dict[str, Message] = {}  # message_id -> Message
        self.user_messages: Dict[str, List[str]] = defaultdict(list)  # user_id -> [message_ids]
        self.room_messages: Dict[str, List[str]] = defaultdict(list)  # room_id -> [message_ids]
        self.private_messages: Dict[str, List[str]] = defaultdict(list)  # user_id -> [message_ids]
        
        # Message queues for delivery
        self.pending_messages: deque = deque()
        self.failed_messages: List[Message] = []
        
        # Message threading
        self.threads: Dict[str, List[str]] = defaultdict(list)  # thread_id -> [message_ids]
        
        # Message history limits
        self.max_messages_per_room = 10000
        self.max_messages_per_user = 5000
        self.message_retention_days = 30
        
        # Statistics
        self.total_messages_sent = 0
        self.total_messages_delivered = 0
        self.total_messages_failed = 0
        
        # Lock for thread safety
        self._lock = threading.Lock()
        
        print("Message service initialized")
    
    def send_message(self, message: Message) -> bool:
        """Send a message"""
        try:
            with self._lock:
                # Store message
                self.messages[message.message_id] = message
                
                # Add to pending queue for delivery
                self.pending_messages.append(message)
                
                # Update statistics
                self.total_messages_sent += 1
                
                # Add to indexes
                self._index_message(message)
                
                print(f"Message queued for delivery: {message.message_id}")
                return True
                
        except Exception as e:
            print(f"Error sending message: {e}")
            return False
    
    def _index_message(self, message: Message):
        """Index message for quick retrieval"""
        # Index by sender
        self.user_messages[message.sender.user_id].append(message.message_id)
        
        # Index by type
        if message.message_type == MessageType.ROOM_MESSAGE and message.room_id:
            self.room_messages[message.room_id].append(message.message_id)
        elif message.message_type == MessageType.PRIVATE_MESSAGE and message.recipient_id:
            # Index for both sender and recipient
            self.private_messages[message.sender.user_id].append(message.message_id)
            self.private_messages[message.recipient_id].append(message.message_id)
        
        # Thread indexing
        if message.thread_id:
            self.threads[message.thread_id].append(message.message_id)
    
    def process_pending_messages(self):
        """Process pending messages for delivery"""
        processed_count = 0
        
        with self._lock:
            # Process up to 100 messages per batch
            for _ in range(min(100, len(self.pending_messages))):
                if not self.pending_messages:
                    break
                
                message = self.pending_messages.popleft()
                
                if self._deliver_message(message):
                    message.mark_as_delivered()
                    self.total_messages_delivered += 1
                    processed_count += 1
                else:
                    message.increment_delivery_attempts()
                    
                    if message.can_retry_delivery():
                        # Re-queue for retry
                        self.pending_messages.append(message)
                    else:
                        # Mark as failed
                        message.mark_as_failed()
                        self.failed_messages.append(message)
                        self.total_messages_failed += 1
        
        if processed_count > 0:
            print(f"Processed {processed_count} messages")
    
    def _deliver_message(self, message: Message) -> bool:
        """Attempt to deliver a message"""
        try:
            # Simulate message delivery
            if message.message_type == MessageType.ROOM_MESSAGE:
                return self._deliver_room_message(message)
            elif message.message_type == MessageType.PRIVATE_MESSAGE:
                return self._deliver_private_message(message)
            elif message.message_type == MessageType.SYSTEM_MESSAGE:
                return self._deliver_system_message(message)
            
            return False
            
        except Exception as e:
            print(f"Error delivering message {message.message_id}: {e}")
            return False
    
    def _deliver_room_message(self, message: Message) -> bool:
        """Deliver room message"""
        # In a real system, this would send to all room members
        print(f"Delivering room message {message.message_id} to room {message.room_id}")
        return True
    
    def _deliver_private_message(self, message: Message) -> bool:
        """Deliver private message"""
        # In a real system, this would send to the recipient
        print(f"Delivering private message {message.message_id} to user {message.recipient_id}")
        return True
    
    def _deliver_system_message(self, message: Message) -> bool:
        """Deliver system message"""
        # In a real system, this would broadcast to all users
        print(f"Delivering system message {message.message_id}")
        return True
    
    def get_message(self, message_id: str) -> Optional[Message]:
        """Get message by ID"""
        return self.messages.get(message_id)
    
    def get_user_messages(self, user_id: str, limit: int = 50) -> List[Message]:
        """Get messages for a user"""
        message_ids = self.user_messages.get(user_id, [])
        
        # Get recent messages
        recent_ids = message_ids[-limit:] if limit > 0 else message_ids
        
        messages = []
        for msg_id in recent_ids:
            message = self.messages.get(msg_id)
            if message:
                messages.append(message)
        
        return sorted(messages, key=lambda m: m.timestamp)
    
    def get_room_messages(self, room_id: str, limit: int = 50) -> List[Message]:
        """Get messages for a room"""
        message_ids = self.room_messages.get(room_id, [])
        
        # Get recent messages
        recent_ids = message_ids[-limit:] if limit > 0 else message_ids
        
        messages = []
        for msg_id in recent_ids:
            message = self.messages.get(msg_id)
            if message:
                messages.append(message)
        
        return sorted(messages, key=lambda m: m.timestamp)
    
    def get_private_messages(self, user_id: str, limit: int = 50) -> List[Message]:
        """Get private messages for a user"""
        message_ids = self.private_messages.get(user_id, [])
        
        # Get recent messages
        recent_ids = message_ids[-limit:] if limit > 0 else message_ids
        
        messages = []
        for msg_id in recent_ids:
            message = self.messages.get(msg_id)
            if message and message.message_type == MessageType.PRIVATE_MESSAGE:
                messages.append(message)
        
        return sorted(messages, key=lambda m: m.timestamp)
    
    def get_conversation_messages(self, user1_id: str, user2_id: str, limit: int = 50) -> List[Message]:
        """Get conversation between two users"""
        user1_private = self.get_private_messages(user1_id)
        conversation = []
        
        for message in user1_private:
            if ((message.sender.user_id == user1_id and message.recipient_id == user2_id) or
                (message.sender.user_id == user2_id and message.recipient_id == user1_id)):
                conversation.append(message)
        
        # Sort by timestamp and limit
        conversation.sort(key=lambda m: m.timestamp)
        return conversation[-limit:] if limit > 0 else conversation
    
    def get_thread_messages(self, thread_id: str) -> List[Message]:
        """Get messages in a thread"""
        message_ids = self.threads.get(thread_id, [])
        
        messages = []
        for msg_id in message_ids:
            message = self.messages.get(msg_id)
            if message:
                messages.append(message)
        
        return sorted(messages, key=lambda m: m.timestamp)
    
    def search_messages(self, query: str, user_id: Optional[str] = None, 
                       room_id: Optional[str] = None, limit: int = 50) -> List[Message]:
        """Search messages by content"""
        query = query.lower()
        results = []
        
        for message in self.messages.values():
            # Filter by user or room if specified
            if user_id and message.sender.user_id != user_id:
                continue
            if room_id and message.room_id != room_id:
                continue
            
            # Search in content
            if query in message.content.lower():
                results.append(message)
        
        # Sort by timestamp (most recent first) and limit
        results.sort(key=lambda m: m.timestamp, reverse=True)
        return results[:limit] if limit > 0 else results
    
    def edit_message(self, message_id: str, new_content: str, editor_user_id: str) -> bool:
        """Edit a message"""
        message = self.messages.get(message_id)
        if not message:
            return False
        
        # Check if user can edit this message
        if message.sender.user_id != editor_user_id:
            return False
        
        # Check edit time limit (e.g., 15 minutes)
        edit_deadline = message.timestamp + timedelta(minutes=15)
        if datetime.now() > edit_deadline:
            return False
        
        message.edit_content(new_content)
        print(f"Message {message_id} edited by user {editor_user_id}")
        return True
    
    def delete_message(self, message_id: str, deleter_user_id: str) -> bool:
        """Delete a message"""
        message = self.messages.get(message_id)
        if not message:
            return False
        
        # Check if user can delete this message
        if message.sender.user_id != deleter_user_id:
            return False
        
        message.delete_message()
        print(f"Message {message_id} deleted by user {deleter_user_id}")
        return True
    
    def add_reaction(self, message_id: str, user_id: str, emoji: str) -> bool:
        """Add reaction to a message"""
        message = self.messages.get(message_id)
        if not message:
            return False
        
        message.add_reaction(user_id, emoji)
        print(f"User {user_id} reacted to message {message_id} with {emoji}")
        return True
    
    def remove_reaction(self, message_id: str, user_id: str) -> bool:
        """Remove reaction from a message"""
        message = self.messages.get(message_id)
        if not message:
            return False
        
        message.remove_reaction(user_id)
        print(f"User {user_id} removed reaction from message {message_id}")
        return True
    
    def mark_as_read(self, message_id: str, user_id: str) -> bool:
        """Mark message as read by user"""
        message = self.messages.get(message_id)
        if not message:
            return False
        
        # In a real system, you'd track read status per user
        if message.recipient_id == user_id or message.room_id:
            message.mark_as_read()
            return True
        
        return False
    
    def get_unread_count(self, user_id: str) -> int:
        """Get unread message count for user"""
        count = 0
        
        # Check private messages
        for msg_id in self.private_messages.get(user_id, []):
            message = self.messages.get(msg_id)
            if (message and 
                message.recipient_id == user_id and 
                message.status != MessageStatus.READ):
                count += 1
        
        return count
    
    def cleanup_old_messages(self):
        """Clean up old messages based on retention policy"""
        cutoff_date = datetime.now() - timedelta(days=self.message_retention_days)
        messages_to_delete = []
        
        for message_id, message in self.messages.items():
            if message.timestamp < cutoff_date:
                messages_to_delete.append(message_id)
        
        # Delete old messages
        for message_id in messages_to_delete:
            self._delete_message_completely(message_id)
        
        if messages_to_delete:
            print(f"Cleaned up {len(messages_to_delete)} old messages")
    
    def _delete_message_completely(self, message_id: str):
        """Completely delete a message from all indexes"""
        message = self.messages.get(message_id)
        if not message:
            return
        
        # Remove from all indexes
        self.user_messages[message.sender.user_id].remove(message_id)
        
        if message.message_type == MessageType.ROOM_MESSAGE and message.room_id:
            self.room_messages[message.room_id].remove(message_id)
        elif message.message_type == MessageType.PRIVATE_MESSAGE and message.recipient_id:
            self.private_messages[message.sender.user_id].remove(message_id)
            self.private_messages[message.recipient_id].remove(message_id)
        
        if message.thread_id:
            self.threads[message.thread_id].remove(message_id)
        
        # Remove from main storage
        del self.messages[message_id]
    
    def get_all_messages(self) -> List[Message]:
        """Get all messages (for admin/debugging)"""
        return list(self.messages.values())
    
    def get_statistics(self) -> dict:
        """Get message service statistics"""
        return {
            'total_messages': len(self.messages),
            'total_messages_sent': self.total_messages_sent,
            'total_messages_delivered': self.total_messages_delivered,
            'total_messages_failed': self.total_messages_failed,
            'pending_messages': len(self.pending_messages),
            'failed_messages': len(self.failed_messages),
            'threads_count': len(self.threads),
            'rooms_with_messages': len(self.room_messages),
            'users_with_messages': len(self.user_messages)
        }
    
    def __str__(self):
        return f"MessageService(messages={len(self.messages)}, pending={len(self.pending_messages)})"
    
    def __repr__(self):
        return self.__str__() 