"""
RoomManager class for the distributed chat system
"""

from typing import Dict, List, Optional
from chat_room import ChatRoom
from user import User


class RoomManager:
    """RoomManager class for managing chat rooms"""
    
    def __init__(self):
        self.rooms: Dict[str, ChatRoom] = {}  # room_name -> ChatRoom
        self.rooms_by_id: Dict[str, ChatRoom] = {}  # room_id -> ChatRoom
        self.user_rooms: Dict[str, List[str]] = {}  # user_id -> List[room_id]
    
    def create_room(self, name: str, description: str, creator: User) -> Optional[ChatRoom]:
        """Create a new chat room"""
        if name in self.rooms:
            return None  # Room already exists
        
        room = ChatRoom(name, description, creator)
        
        # Store room in indexes
        self.rooms[name] = room
        self.rooms_by_id[room.room_id] = room
        
        # Add creator to user_rooms
        if creator.user_id not in self.user_rooms:
            self.user_rooms[creator.user_id] = []
        self.user_rooms[creator.user_id].append(room.room_id)
        
        return room
    
    def get_room(self, room_name: str) -> Optional[ChatRoom]:
        """Get room by name"""
        return self.rooms.get(room_name)
    
    def get_room_by_id(self, room_id: str) -> Optional[ChatRoom]:
        """Get room by ID"""
        return self.rooms_by_id.get(room_id)
    
    def get_all_rooms(self) -> List[ChatRoom]:
        """Get all rooms"""
        return list(self.rooms.values())
    
    def get_active_rooms(self) -> List[ChatRoom]:
        """Get active rooms"""
        return [room for room in self.rooms.values() if room.is_active]
    
    def get_public_rooms(self) -> List[ChatRoom]:
        """Get public rooms"""
        return [room for room in self.rooms.values() if not room.is_private]
    
    def get_private_rooms(self) -> List[ChatRoom]:
        """Get private rooms"""
        return [room for room in self.rooms.values() if room.is_private]
    
    def join_room(self, room_id: str, user_id: str) -> bool:
        """Add user to room"""
        room = self.get_room_by_id(room_id)
        if room and room.add_member(user_id):
            # Update user_rooms index
            if user_id not in self.user_rooms:
                self.user_rooms[user_id] = []
            if room_id not in self.user_rooms[user_id]:
                self.user_rooms[user_id].append(room_id)
            return True
        return False
    
    def leave_room(self, room_id: str, user_id: str) -> bool:
        """Remove user from room"""
        room = self.get_room_by_id(room_id)
        if room and room.remove_member(user_id):
            # Update user_rooms index
            if user_id in self.user_rooms and room_id in self.user_rooms[user_id]:
                self.user_rooms[user_id].remove(room_id)
            return True
        return False
    
    def get_user_rooms(self, user_id: str) -> List[ChatRoom]:
        """Get rooms that user is a member of"""
        room_ids = self.user_rooms.get(user_id, [])
        return [self.rooms_by_id[room_id] for room_id in room_ids if room_id in self.rooms_by_id]
    
    def get_room_members(self, room_id: str) -> List[str]:
        """Get members of a room"""
        room = self.get_room_by_id(room_id)
        return list(room.members) if room else []
    
    def is_user_in_room(self, room_id: str, user_id: str) -> bool:
        """Check if user is in room"""
        room = self.get_room_by_id(room_id)
        return room.is_member(user_id) if room else False
    
    def is_user_admin(self, room_id: str, user_id: str) -> bool:
        """Check if user is admin of room"""
        room = self.get_room_by_id(room_id)
        return room.is_admin(user_id) if room else False
    
    def promote_to_admin(self, room_id: str, user_id: str, admin_user_id: str) -> bool:
        """Promote user to admin"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(admin_user_id):
            return room.add_admin(user_id)
        return False
    
    def demote_admin(self, room_id: str, user_id: str, admin_user_id: str) -> bool:
        """Demote admin to regular user"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(admin_user_id):
            return room.remove_admin(user_id)
        return False
    
    def ban_user(self, room_id: str, user_id: str, admin_user_id: str) -> bool:
        """Ban user from room"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(admin_user_id):
            success = room.ban_user(user_id)
            if success:
                # Remove from user_rooms index
                if user_id in self.user_rooms and room_id in self.user_rooms[user_id]:
                    self.user_rooms[user_id].remove(room_id)
            return success
        return False
    
    def unban_user(self, room_id: str, user_id: str, admin_user_id: str) -> bool:
        """Unban user from room"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(admin_user_id):
            return room.unban_user(user_id)
        return False
    
    def delete_room(self, room_id: str, user_id: str) -> bool:
        """Delete room (only creator can delete)"""
        room = self.get_room_by_id(room_id)
        if room and room.is_creator(user_id):
            # Remove from all indexes
            del self.rooms[room.name]
            del self.rooms_by_id[room_id]
            
            # Remove from user_rooms for all members
            for member_id in room.members:
                if member_id in self.user_rooms and room_id in self.user_rooms[member_id]:
                    self.user_rooms[member_id].remove(room_id)
            
            return True
        return False
    
    def update_room_description(self, room_id: str, description: str, user_id: str) -> bool:
        """Update room description"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(user_id):
            room.update_description(description)
            return True
        return False
    
    def set_room_read_only(self, room_id: str, read_only: bool, user_id: str) -> bool:
        """Set room as read-only"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(user_id):
            room.set_read_only(read_only)
            return True
        return False
    
    def set_room_private(self, room_id: str, private: bool, user_id: str) -> bool:
        """Set room as private"""
        room = self.get_room_by_id(room_id)
        if room and room.is_admin(user_id):
            room.set_private(private)
            return True
        return False
    
    def search_rooms(self, query: str) -> List[ChatRoom]:
        """Search rooms by name or description"""
        query = query.lower()
        results = []
        
        for room in self.rooms.values():
            if (query in room.name.lower() or 
                query in room.description.lower()):
                results.append(room)
        
        return results
    
    def get_room_statistics(self) -> dict:
        """Get room statistics"""
        total_rooms = len(self.rooms)
        active_rooms = len(self.get_active_rooms())
        public_rooms = len(self.get_public_rooms())
        private_rooms = len(self.get_private_rooms())
        
        total_members = sum(len(room.members) for room in self.rooms.values())
        avg_members_per_room = total_members / total_rooms if total_rooms > 0 else 0
        
        return {
            'total_rooms': total_rooms,
            'active_rooms': active_rooms,
            'public_rooms': public_rooms,
            'private_rooms': private_rooms,
            'total_members': total_members,
            'avg_members_per_room': avg_members_per_room
        }
    
    def get_popular_rooms(self, limit: int = 10) -> List[ChatRoom]:
        """Get popular rooms by member count"""
        rooms = list(self.rooms.values())
        rooms.sort(key=lambda r: len(r.members), reverse=True)
        return rooms[:limit]
    
    def get_recent_rooms(self, limit: int = 10) -> List[ChatRoom]:
        """Get recently created rooms"""
        rooms = list(self.rooms.values())
        rooms.sort(key=lambda r: r.created_at, reverse=True)
        return rooms[:limit]
    
    def get_most_active_rooms(self, limit: int = 10) -> List[ChatRoom]:
        """Get most active rooms by last activity"""
        rooms = list(self.rooms.values())
        rooms.sort(key=lambda r: r.last_activity, reverse=True)
        return rooms[:limit]
    
    def __str__(self):
        return f"RoomManager(rooms={len(self.rooms)}, active={len(self.get_active_rooms())})"
    
    def __repr__(self):
        return self.__str__() 