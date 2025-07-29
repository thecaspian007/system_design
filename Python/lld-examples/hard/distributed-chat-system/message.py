"""
Message class and related enums for the distributed chat system
"""

import uuid
from datetime import datetime
from enum import Enum
from typing import Optional
from user import User


class MessageType(Enum):
    """Enum for message types"""
    PRIVATE_MESSAGE = "private_message"
    ROOM_MESSAGE = "room_message"
    SYSTEM_MESSAGE = "system_message"
    NOTIFICATION = "notification"


class MessageStatus(Enum):
    """Enum for message delivery status"""
    PENDING = "pending"
    SENT = "sent"
    DELIVERED = "delivered"
    READ = "read"
    FAILED = "failed"


class Message:
    """Message class representing a chat message"""
    
    def __init__(self, sender: User, content: str, message_type: MessageType, 
                 recipient_id: Optional[str] = None, room_id: Optional[str] = None):
        self.message_id = str(uuid.uuid4())
        self.sender = sender
        self.content = content
        self.message_type = message_type
        self.recipient_id = recipient_id
        self.room_id = room_id
        self.timestamp = datetime.now()
        self.status = MessageStatus.PENDING
        self.edited_at: Optional[datetime] = None
        self.is_deleted = False
        
        # Message metadata
        self.server_id: Optional[str] = None
        self.delivery_attempts = 0
        self.max_delivery_attempts = 3
        
        # Reply information
        self.reply_to_message_id: Optional[str] = None
        self.thread_id: Optional[str] = None
        
        # Media attachments
        self.attachments: list = []
        
        # Reactions
        self.reactions: dict = {}  # user_id -> reaction_emoji
        
        # Recipient reference (for private messages)
        self.recipient: Optional[User] = None
    
    def set_recipient(self, recipient: User):
        """Set message recipient"""
        self.recipient = recipient
        self.recipient_id = recipient.user_id
    
    def mark_as_sent(self, server_id: str):
        """Mark message as sent"""
        self.status = MessageStatus.SENT
        self.server_id = server_id
    
    def mark_as_delivered(self):
        """Mark message as delivered"""
        self.status = MessageStatus.DELIVERED
    
    def mark_as_read(self):
        """Mark message as read"""
        self.status = MessageStatus.READ
    
    def mark_as_failed(self):
        """Mark message as failed"""
        self.status = MessageStatus.FAILED
    
    def increment_delivery_attempts(self):
        """Increment delivery attempts"""
        self.delivery_attempts += 1
    
    def can_retry_delivery(self) -> bool:
        """Check if message can be retried for delivery"""
        return (self.status == MessageStatus.PENDING and 
                self.delivery_attempts < self.max_delivery_attempts)
    
    def edit_content(self, new_content: str):
        """Edit message content"""
        self.content = new_content
        self.edited_at = datetime.now()
    
    def delete_message(self):
        """Delete message"""
        self.is_deleted = True
        self.content = "[Message deleted]"
    
    def add_reaction(self, user_id: str, emoji: str):
        """Add reaction to message"""
        self.reactions[user_id] = emoji
    
    def remove_reaction(self, user_id: str):
        """Remove reaction from message"""
        if user_id in self.reactions:
            del self.reactions[user_id]
    
    def get_reaction_count(self) -> int:
        """Get total reaction count"""
        return len(self.reactions)
    
    def add_attachment(self, attachment: dict):
        """Add attachment to message"""
        self.attachments.append(attachment)
    
    def is_private_message(self) -> bool:
        """Check if message is a private message"""
        return self.message_type == MessageType.PRIVATE_MESSAGE
    
    def is_room_message(self) -> bool:
        """Check if message is a room message"""
        return self.message_type == MessageType.ROOM_MESSAGE
    
    def is_system_message(self) -> bool:
        """Check if message is a system message"""
        return self.message_type == MessageType.SYSTEM_MESSAGE
    
    def get_formatted_timestamp(self) -> str:
        """Get formatted timestamp"""
        return self.timestamp.strftime("%Y-%m-%d %H:%M:%S")
    
    def get_display_content(self) -> str:
        """Get display content (truncated if too long)"""
        if self.is_deleted:
            return "[Message deleted]"
        
        max_length = 100
        if len(self.content) > max_length:
            return self.content[:max_length] + "..."
        return self.content
    
    def __str__(self):
        return f"Message(sender={self.sender.display_name}, type={self.message_type.value}, content={self.get_display_content()})"
    
    def __repr__(self):
        return self.__str__()
    
    def to_dict(self):
        """Convert message to dictionary"""
        return {
            'message_id': self.message_id,
            'sender': self.sender.to_dict(),
            'content': self.content,
            'message_type': self.message_type.value,
            'recipient_id': self.recipient_id,
            'room_id': self.room_id,
            'timestamp': self.timestamp.isoformat(),
            'status': self.status.value,
            'edited_at': self.edited_at.isoformat() if self.edited_at else None,
            'is_deleted': self.is_deleted,
            'server_id': self.server_id,
            'delivery_attempts': self.delivery_attempts,
            'reply_to_message_id': self.reply_to_message_id,
            'thread_id': self.thread_id,
            'attachments': self.attachments,
            'reactions': self.reactions,
            'recipient': self.recipient.to_dict() if self.recipient else None
        } 