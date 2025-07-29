"""
NotificationService class for the distributed chat system
"""

import uuid
from typing import Dict, List, Optional, Set
from datetime import datetime, timedelta
from collections import defaultdict, deque
from enum import Enum
import threading


class NotificationType(Enum):
    """Enum for notification types"""
    MESSAGE = "message"
    MENTION = "mention"
    ROOM_INVITE = "room_invite"
    FRIEND_REQUEST = "friend_request"
    SYSTEM = "system"
    WARNING = "warning"
    INFO = "info"


class NotificationPriority(Enum):
    """Enum for notification priority"""
    LOW = "low"
    NORMAL = "normal"
    HIGH = "high"
    URGENT = "urgent"


class Notification:
    """Notification class"""
    
    def __init__(self, user_id: str, content: str, 
                 notification_type: NotificationType = NotificationType.MESSAGE,
                 priority: NotificationPriority = NotificationPriority.NORMAL):
        self.notification_id = str(uuid.uuid4())
        self.user_id = user_id
        self.content = content
        self.notification_type = notification_type
        self.priority = priority
        self.timestamp = datetime.now()
        self.is_read = False
        self.is_delivered = False
        self.delivery_attempts = 0
        self.max_delivery_attempts = 3
        
        # Additional metadata
        self.source_id: Optional[str] = None  # message_id, room_id, etc.
        self.action_url: Optional[str] = None
        self.expires_at: Optional[datetime] = None
    
    def mark_as_read(self):
        """Mark notification as read"""
        self.is_read = True
    
    def mark_as_delivered(self):
        """Mark notification as delivered"""
        self.is_delivered = True
    
    def increment_delivery_attempts(self):
        """Increment delivery attempts"""
        self.delivery_attempts += 1
    
    def can_retry_delivery(self) -> bool:
        """Check if notification can be retried for delivery"""
        return (not self.is_delivered and 
                self.delivery_attempts < self.max_delivery_attempts and
                (self.expires_at is None or datetime.now() < self.expires_at))
    
    def is_expired(self) -> bool:
        """Check if notification is expired"""
        return self.expires_at is not None and datetime.now() > self.expires_at
    
    def to_dict(self) -> dict:
        """Convert notification to dictionary"""
        return {
            'notification_id': self.notification_id,
            'user_id': self.user_id,
            'content': self.content,
            'notification_type': self.notification_type.value,
            'priority': self.priority.value,
            'timestamp': self.timestamp.isoformat(),
            'is_read': self.is_read,
            'is_delivered': self.is_delivered,
            'delivery_attempts': self.delivery_attempts,
            'source_id': self.source_id,
            'action_url': self.action_url,
            'expires_at': self.expires_at.isoformat() if self.expires_at else None
        }
    
    def __str__(self):
        return f"Notification(id={self.notification_id}, user={self.user_id}, type={self.notification_type.value})"


class NotificationService:
    """NotificationService class for managing notifications"""
    
    def __init__(self):
        # Notification storage
        self.notifications: Dict[str, Notification] = {}  # notification_id -> Notification
        self.user_notifications: Dict[str, List[str]] = defaultdict(list)  # user_id -> [notification_ids]
        
        # Delivery queues
        self.pending_notifications: deque = deque()
        self.failed_notifications: List[Notification] = []
        
        # User preferences
        self.user_preferences: Dict[str, dict] = defaultdict(dict)  # user_id -> preferences
        self.muted_users: Dict[str, Set[str]] = defaultdict(set)  # user_id -> set of muted user_ids
        self.muted_rooms: Dict[str, Set[str]] = defaultdict(set)  # user_id -> set of muted room_ids
        
        # Notification limits
        self.max_notifications_per_user = 1000
        self.notification_retention_days = 7
        
        # Statistics
        self.total_notifications_sent = 0
        self.total_notifications_delivered = 0
        self.total_notifications_failed = 0
        
        # Lock for thread safety
        self._lock = threading.Lock()
        
        print("Notification service initialized")
    
    def add_notification(self, user_id: str, content: str, 
                        notification_type: NotificationType = NotificationType.MESSAGE,
                        priority: NotificationPriority = NotificationPriority.NORMAL,
                        source_id: Optional[str] = None,
                        action_url: Optional[str] = None,
                        expires_in_minutes: Optional[int] = None) -> str:
        """Add a notification"""
        try:
            with self._lock:
                # Check if user wants this type of notification
                if not self._should_notify_user(user_id, notification_type, source_id):
                    return ""
                
                # Create notification
                notification = Notification(user_id, content, notification_type, priority)
                notification.source_id = source_id
                notification.action_url = action_url
                
                if expires_in_minutes:
                    notification.expires_at = datetime.now() + timedelta(minutes=expires_in_minutes)
                
                # Store notification
                self.notifications[notification.notification_id] = notification
                
                # Add to user's notification list
                self.user_notifications[user_id].append(notification.notification_id)
                
                # Enforce notification limit per user
                self._enforce_notification_limit(user_id)
                
                # Add to delivery queue
                self.pending_notifications.append(notification)
                
                # Update statistics
                self.total_notifications_sent += 1
                
                print(f"Notification added for user {user_id}: {content[:50]}...")
                return notification.notification_id
                
        except Exception as e:
            print(f"Error adding notification: {e}")
            return ""
    
    def _should_notify_user(self, user_id: str, notification_type: NotificationType, 
                           source_id: Optional[str] = None) -> bool:
        """Check if user should receive this notification"""
        # Check user preferences
        preferences = self.user_preferences.get(user_id, {})
        
        # Check if notification type is enabled
        type_key = f"enable_{notification_type.value}"
        if not preferences.get(type_key, True):  # Default to enabled
            return False
        
        # Check muted users/rooms
        if source_id:
            if source_id in self.muted_users.get(user_id, set()):
                return False
            if source_id in self.muted_rooms.get(user_id, set()):
                return False
        
        # Check do not disturb mode
        if preferences.get('do_not_disturb', False):
            # Only allow urgent notifications during DND
            return notification_type == NotificationType.URGENT
        
        return True
    
    def _enforce_notification_limit(self, user_id: str):
        """Enforce notification limit per user"""
        user_notif_ids = self.user_notifications[user_id]
        
        if len(user_notif_ids) > self.max_notifications_per_user:
            # Remove oldest notifications
            excess_count = len(user_notif_ids) - self.max_notifications_per_user
            for _ in range(excess_count):
                old_notif_id = user_notif_ids.pop(0)
                if old_notif_id in self.notifications:
                    del self.notifications[old_notif_id]
    
    def process_notifications(self):
        """Process pending notifications for delivery"""
        processed_count = 0
        
        with self._lock:
            # Process up to 50 notifications per batch
            for _ in range(min(50, len(self.pending_notifications))):
                if not self.pending_notifications:
                    break
                
                notification = self.pending_notifications.popleft()
                
                # Skip expired notifications
                if notification.is_expired():
                    continue
                
                if self._deliver_notification(notification):
                    notification.mark_as_delivered()
                    self.total_notifications_delivered += 1
                    processed_count += 1
                else:
                    notification.increment_delivery_attempts()
                    
                    if notification.can_retry_delivery():
                        # Re-queue for retry
                        self.pending_notifications.append(notification)
                    else:
                        # Mark as failed
                        self.failed_notifications.append(notification)
                        self.total_notifications_failed += 1
        
        if processed_count > 0:
            print(f"Processed {processed_count} notifications")
    
    def _deliver_notification(self, notification: Notification) -> bool:
        """Attempt to deliver a notification"""
        try:
            # In a real system, this would send via various channels:
            # - WebSocket to online users
            # - Push notification to mobile devices
            # - Email for important notifications
            # - SMS for urgent notifications
            
            # Simulate delivery based on priority
            if notification.priority == NotificationPriority.URGENT:
                print(f"🚨 URGENT notification delivered to user {notification.user_id}")
            elif notification.priority == NotificationPriority.HIGH:
                print(f"📢 HIGH priority notification delivered to user {notification.user_id}")
            else:
                print(f"📬 Notification delivered to user {notification.user_id}")
            
            return True
            
        except Exception as e:
            print(f"Error delivering notification {notification.notification_id}: {e}")
            return False
    
    def get_user_notifications(self, user_id: str, limit: int = 50, 
                              unread_only: bool = False) -> List[Notification]:
        """Get notifications for a user"""
        notification_ids = self.user_notifications.get(user_id, [])
        
        # Get recent notifications
        recent_ids = notification_ids[-limit:] if limit > 0 else notification_ids
        
        notifications = []
        for notif_id in reversed(recent_ids):  # Most recent first
            notification = self.notifications.get(notif_id)
            if notification:
                if unread_only and notification.is_read:
                    continue
                notifications.append(notification)
        
        return notifications
    
    def mark_notification_as_read(self, notification_id: str, user_id: str) -> bool:
        """Mark a notification as read"""
        notification = self.notifications.get(notification_id)
        if not notification or notification.user_id != user_id:
            return False
        
        notification.mark_as_read()
        print(f"Notification {notification_id} marked as read by user {user_id}")
        return True
    
    def mark_all_as_read(self, user_id: str) -> int:
        """Mark all notifications as read for a user"""
        count = 0
        
        for notif_id in self.user_notifications.get(user_id, []):
            notification = self.notifications.get(notif_id)
            if notification and not notification.is_read:
                notification.mark_as_read()
                count += 1
        
        if count > 0:
            print(f"Marked {count} notifications as read for user {user_id}")
        
        return count
    
    def delete_notification(self, notification_id: str, user_id: str) -> bool:
        """Delete a notification"""
        notification = self.notifications.get(notification_id)
        if not notification or notification.user_id != user_id:
            return False
        
        # Remove from user's notification list
        if notification_id in self.user_notifications[user_id]:
            self.user_notifications[user_id].remove(notification_id)
        
        # Remove from main storage
        del self.notifications[notification_id]
        
        print(f"Notification {notification_id} deleted by user {user_id}")
        return True
    
    def get_unread_count(self, user_id: str) -> int:
        """Get unread notification count for user"""
        count = 0
        
        for notif_id in self.user_notifications.get(user_id, []):
            notification = self.notifications.get(notif_id)
            if notification and not notification.is_read:
                count += 1
        
        return count
    
    def set_user_preferences(self, user_id: str, preferences: dict):
        """Set user notification preferences"""
        self.user_preferences[user_id].update(preferences)
        print(f"Updated notification preferences for user {user_id}")
    
    def get_user_preferences(self, user_id: str) -> dict:
        """Get user notification preferences"""
        default_preferences = {
            'enable_message': True,
            'enable_mention': True,
            'enable_room_invite': True,
            'enable_friend_request': True,
            'enable_system': True,
            'enable_warning': True,
            'enable_info': True,
            'do_not_disturb': False,
            'quiet_hours_start': None,
            'quiet_hours_end': None
        }
        
        user_prefs = self.user_preferences.get(user_id, {})
        default_preferences.update(user_prefs)
        return default_preferences
    
    def mute_user(self, user_id: str, muted_user_id: str):
        """Mute notifications from a specific user"""
        self.muted_users[user_id].add(muted_user_id)
        print(f"User {user_id} muted user {muted_user_id}")
    
    def unmute_user(self, user_id: str, muted_user_id: str):
        """Unmute notifications from a specific user"""
        self.muted_users[user_id].discard(muted_user_id)
        print(f"User {user_id} unmuted user {muted_user_id}")
    
    def mute_room(self, user_id: str, room_id: str):
        """Mute notifications from a specific room"""
        self.muted_rooms[user_id].add(room_id)
        print(f"User {user_id} muted room {room_id}")
    
    def unmute_room(self, user_id: str, room_id: str):
        """Unmute notifications from a specific room"""
        self.muted_rooms[user_id].discard(room_id)
        print(f"User {user_id} unmuted room {room_id}")
    
    def get_muted_users(self, user_id: str) -> List[str]:
        """Get list of muted users"""
        return list(self.muted_users.get(user_id, set()))
    
    def get_muted_rooms(self, user_id: str) -> List[str]:
        """Get list of muted rooms"""
        return list(self.muted_rooms.get(user_id, set()))
    
    def send_system_notification(self, user_id: str, message: str, 
                                priority: NotificationPriority = NotificationPriority.NORMAL):
        """Send a system notification"""
        return self.add_notification(
            user_id, 
            message, 
            NotificationType.SYSTEM, 
            priority
        )
    
    def send_mention_notification(self, user_id: str, mentioned_by: str, 
                                 room_name: str, message_content: str):
        """Send a mention notification"""
        content = f"You were mentioned by {mentioned_by} in {room_name}: {message_content[:100]}..."
        return self.add_notification(
            user_id,
            content,
            NotificationType.MENTION,
            NotificationPriority.HIGH
        )
    
    def send_room_invite_notification(self, user_id: str, room_name: str, 
                                    invited_by: str):
        """Send a room invite notification"""
        content = f"You've been invited to join '{room_name}' by {invited_by}"
        return self.add_notification(
            user_id,
            content,
            NotificationType.ROOM_INVITE,
            NotificationPriority.HIGH,
            expires_in_minutes=1440  # 24 hours
        )
    
    def cleanup_old_notifications(self):
        """Clean up old notifications based on retention policy"""
        cutoff_date = datetime.now() - timedelta(days=self.notification_retention_days)
        notifications_to_delete = []
        
        for notif_id, notification in self.notifications.items():
            if notification.timestamp < cutoff_date:
                notifications_to_delete.append(notif_id)
        
        # Delete old notifications
        for notif_id in notifications_to_delete:
            notification = self.notifications.get(notif_id)
            if notification:
                # Remove from user's list
                if notif_id in self.user_notifications[notification.user_id]:
                    self.user_notifications[notification.user_id].remove(notif_id)
                
                # Remove from main storage
                del self.notifications[notif_id]
        
        if notifications_to_delete:
            print(f"Cleaned up {len(notifications_to_delete)} old notifications")
    
    def get_statistics(self) -> dict:
        """Get notification service statistics"""
        total_unread = sum(
            1 for notif in self.notifications.values() if not notif.is_read
        )
        
        return {
            'total_notifications': len(self.notifications),
            'total_notifications_sent': self.total_notifications_sent,
            'total_notifications_delivered': self.total_notifications_delivered,
            'total_notifications_failed': self.total_notifications_failed,
            'pending_notifications': len(self.pending_notifications),
            'failed_notifications': len(self.failed_notifications),
            'total_unread': total_unread,
            'users_with_notifications': len(self.user_notifications),
            'users_with_preferences': len(self.user_preferences)
        }
    
    def __str__(self):
        return f"NotificationService(notifications={len(self.notifications)}, pending={len(self.pending_notifications)})"
    
    def __repr__(self):
        return self.__str__() 