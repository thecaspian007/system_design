package com.systemdesign.lld.socialmedia;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a message in the social media platform
 */
public class Message {
    private String messageId;
    private String senderId;
    private String receiverId;
    private String content;
    private MessageType type;
    private LocalDateTime sentAt;
    private LocalDateTime deliveredAt;
    private LocalDateTime readAt;
    private MessageStatus status;
    private String conversationId;
    private List<String> mediaUrls;
    private boolean isEncrypted;
    private String replyToMessageId;
    
    public Message(String messageId, String senderId, String receiverId, String content, MessageType type) {
        this.messageId = messageId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.type = type;
        this.sentAt = LocalDateTime.now();
        this.status = MessageStatus.SENT;
        this.conversationId = generateConversationId(senderId, receiverId);
        this.mediaUrls = new ArrayList<>();
        this.isEncrypted = false;
    }
    
    public Message(String messageId, String senderId, String receiverId, String content, 
                  MessageType type, String conversationId) {
        this(messageId, senderId, receiverId, content, type);
        this.conversationId = conversationId;
    }
    
    private String generateConversationId(String user1, String user2) {
        // Generate consistent conversation ID regardless of order
        String[] users = {user1, user2};
        Arrays.sort(users);
        return String.join("_", users);
    }
    
    public void markAsDelivered() {
        if (status == MessageStatus.SENT) {
            this.status = MessageStatus.DELIVERED;
            this.deliveredAt = LocalDateTime.now();
        }
    }
    
    public void markAsRead() {
        if (status == MessageStatus.DELIVERED || status == MessageStatus.SENT) {
            this.status = MessageStatus.READ;
            this.readAt = LocalDateTime.now();
            if (deliveredAt == null) {
                this.deliveredAt = LocalDateTime.now();
            }
        }
    }
    
    public void addMediaUrl(String mediaUrl) {
        this.mediaUrls.add(mediaUrl);
    }
    
    public void removeMediaUrl(String mediaUrl) {
        this.mediaUrls.remove(mediaUrl);
    }
    
    public void setReplyTo(String replyToMessageId) {
        this.replyToMessageId = replyToMessageId;
    }
    
    public void encrypt() {
        this.isEncrypted = true;
        // In a real implementation, this would encrypt the content
    }
    
    public void decrypt() {
        this.isEncrypted = false;
        // In a real implementation, this would decrypt the content
    }
    
    public boolean isRead() {
        return status == MessageStatus.READ;
    }
    
    public boolean isDelivered() {
        return status == MessageStatus.DELIVERED || status == MessageStatus.READ;
    }
    
    public boolean hasMedia() {
        return !mediaUrls.isEmpty();
    }
    
    public boolean isReply() {
        return replyToMessageId != null;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getContent() { return content; }
    public MessageType getType() { return type; }
    public LocalDateTime getSentAt() { return sentAt; }
    public LocalDateTime getDeliveredAt() { return deliveredAt; }
    public LocalDateTime getReadAt() { return readAt; }
    public MessageStatus getStatus() { return status; }
    public String getConversationId() { return conversationId; }
    public List<String> getMediaUrls() { return new ArrayList<>(mediaUrls); }
    public boolean isEncrypted() { return isEncrypted; }
    public String getReplyToMessageId() { return replyToMessageId; }
    
    @Override
    public String toString() {
        return String.format("Message{id='%s', from='%s', to='%s', content='%s', type=%s, status=%s}",
                messageId, senderId, receiverId, 
                content.substring(0, Math.min(30, content.length())), type, status);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Message message = (Message) obj;
        return messageId.equals(message.messageId);
    }
    
    @Override
    public int hashCode() {
        return messageId.hashCode();
    }
}

enum MessageType {
    TEXT,
    IMAGE,
    VIDEO,
    AUDIO,
    FILE,
    LOCATION,
    STICKER,
    GIF
}

enum MessageStatus {
    SENT,
    DELIVERED,
    READ,
    FAILED
}

/**
 * Represents a conversation between users
 */
class Conversation {
    private String conversationId;
    private Set<String> participants;
    private List<Message> messages;
    private LocalDateTime createdAt;
    private LocalDateTime lastMessageAt;
    private String lastMessageId;
    private Map<String, LocalDateTime> lastReadTimestamps;
    private boolean isGroupChat;
    private String groupName;
    private String groupDescription;
    private String groupIcon;
    private Set<String> admins;
    
    public Conversation(String conversationId, Set<String> participants) {
        this.conversationId = conversationId;
        this.participants = new HashSet<>(participants);
        this.messages = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.lastReadTimestamps = new HashMap<>();
        this.isGroupChat = participants.size() > 2;
        this.admins = new HashSet<>();
        
        // Initialize read timestamps
        for (String participant : participants) {
            lastReadTimestamps.put(participant, LocalDateTime.now());
        }
    }
    
    public void addMessage(Message message) {
        messages.add(message);
        this.lastMessageAt = message.getSentAt();
        this.lastMessageId = message.getMessageId();
    }
    
    public void addParticipant(String userId) {
        participants.add(userId);
        lastReadTimestamps.put(userId, LocalDateTime.now());
    }
    
    public void removeParticipant(String userId) {
        participants.remove(userId);
        lastReadTimestamps.remove(userId);
        admins.remove(userId);
    }
    
    public void addAdmin(String userId) {
        if (participants.contains(userId)) {
            admins.add(userId);
        }
    }
    
    public void removeAdmin(String userId) {
        admins.remove(userId);
    }
    
    public void updateLastRead(String userId) {
        if (participants.contains(userId)) {
            lastReadTimestamps.put(userId, LocalDateTime.now());
        }
    }
    
    public List<Message> getMessages() {
        return new ArrayList<>(messages);
    }
    
    public List<Message> getMessagesPaginated(int offset, int limit) {
        int start = Math.max(0, messages.size() - offset - limit);
        int end = Math.max(0, messages.size() - offset);
        return messages.subList(start, end);
    }
    
    public List<Message> getUnreadMessages(String userId) {
        LocalDateTime lastRead = lastReadTimestamps.get(userId);
        if (lastRead == null) {
            return new ArrayList<>();
        }
        
        return messages.stream()
                .filter(msg -> msg.getSentAt().isAfter(lastRead) && !msg.getSenderId().equals(userId))
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    public int getUnreadCount(String userId) {
        return getUnreadMessages(userId).size();
    }
    
    public boolean isAdmin(String userId) {
        return admins.contains(userId);
    }
    
    public boolean canModify(String userId) {
        return !isGroupChat || isAdmin(userId);
    }
    
    // Getters and setters
    public String getConversationId() { return conversationId; }
    public Set<String> getParticipants() { return new HashSet<>(participants); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public String getLastMessageId() { return lastMessageId; }
    public Map<String, LocalDateTime> getLastReadTimestamps() { return new HashMap<>(lastReadTimestamps); }
    public boolean isGroupChat() { return isGroupChat; }
    public String getGroupName() { return groupName; }
    public String getGroupDescription() { return groupDescription; }
    public String getGroupIcon() { return groupIcon; }
    public Set<String> getAdmins() { return new HashSet<>(admins); }
    
    public void setGroupName(String groupName) { this.groupName = groupName; }
    public void setGroupDescription(String groupDescription) { this.groupDescription = groupDescription; }
    public void setGroupIcon(String groupIcon) { this.groupIcon = groupIcon; }
    
    @Override
    public String toString() {
        return String.format("Conversation{id='%s', participants=%d, messages=%d, isGroup=%s}",
                conversationId, participants.size(), messages.size(), isGroupChat);
    }
} 