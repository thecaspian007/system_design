package com.systemdesign.lld.chat.model;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Message model representing a chat message in the distributed system
 */
public class Message {
    public enum MessageType {
        PRIVATE_MESSAGE,
        ROOM_MESSAGE,
        SYSTEM_MESSAGE,
        NOTIFICATION
    }
    
    public enum MessageStatus {
        PENDING,
        SENT,
        DELIVERED,
        READ,
        FAILED
    }
    
    private final String messageId;
    private final User sender;
    private String content;
    private final MessageType messageType;
    private String recipientId;
    private String roomId;
    private final LocalDateTime timestamp;
    private MessageStatus status;
    private LocalDateTime editedAt;
    private boolean isDeleted;
    
    // Message metadata
    private String serverId;
    private int deliveryAttempts;
    private final int maxDeliveryAttempts = 3;
    
    // Reply information
    private String replyToMessageId;
    private String threadId;
    
    // Media attachments
    private List<String> attachments;
    
    // Reactions
    private Map<String, String> reactions; // userId -> emoji
    
    // Recipient reference (for private messages)
    private User recipient;
    
    public Message(User sender, String content, MessageType messageType) {
        this.messageId = UUID.randomUUID().toString();
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
        this.timestamp = LocalDateTime.now();
        this.status = MessageStatus.PENDING;
        this.isDeleted = false;
        this.deliveryAttempts = 0;
        this.attachments = new ArrayList<>();
        this.reactions = new HashMap<>();
    }
    
    /**
     * Set message recipient for private messages
     */
    public void setRecipient(User recipient) {
        this.recipient = recipient;
        this.recipientId = recipient.getUserId();
    }
    
    /**
     * Set room ID for room messages
     */
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }
    
    /**
     * Mark message as sent
     */
    public void markAsSent(String serverId) {
        this.status = MessageStatus.SENT;
        this.serverId = serverId;
    }
    
    /**
     * Mark message as delivered
     */
    public void markAsDelivered() {
        this.status = MessageStatus.DELIVERED;
    }
    
    /**
     * Mark message as read
     */
    public void markAsRead() {
        this.status = MessageStatus.READ;
    }
    
    /**
     * Mark message as failed
     */
    public void markAsFailed() {
        this.status = MessageStatus.FAILED;
    }
    
    /**
     * Increment delivery attempts
     */
    public void incrementDeliveryAttempts() {
        this.deliveryAttempts++;
    }
    
    /**
     * Check if message can be retried for delivery
     */
    public boolean canRetryDelivery() {
        return status == MessageStatus.PENDING && deliveryAttempts < maxDeliveryAttempts;
    }
    
    /**
     * Edit message content
     */
    public void editContent(String newContent) {
        this.content = newContent;
        this.editedAt = LocalDateTime.now();
    }
    
    /**
     * Delete message
     */
    public void deleteMessage() {
        this.isDeleted = true;
        this.content = "[Message deleted]";
    }
    
    /**
     * Add reaction to message
     */
    public void addReaction(String userId, String emoji) {
        reactions.put(userId, emoji);
    }
    
    /**
     * Remove reaction from message
     */
    public void removeReaction(String userId) {
        reactions.remove(userId);
    }
    
    /**
     * Get reaction count
     */
    public int getReactionCount() {
        return reactions.size();
    }
    
    /**
     * Add attachment to message
     */
    public void addAttachment(String attachment) {
        attachments.add(attachment);
    }
    
    /**
     * Check if message is a private message
     */
    public boolean isPrivateMessage() {
        return messageType == MessageType.PRIVATE_MESSAGE;
    }
    
    /**
     * Check if message is a room message
     */
    public boolean isRoomMessage() {
        return messageType == MessageType.ROOM_MESSAGE;
    }
    
    /**
     * Check if message is a system message
     */
    public boolean isSystemMessage() {
        return messageType == MessageType.SYSTEM_MESSAGE;
    }
    
    /**
     * Get display content (truncated if too long)
     */
    public String getDisplayContent() {
        if (isDeleted) {
            return "[Message deleted]";
        }
        
        int maxLength = 100;
        if (content.length() > maxLength) {
            return content.substring(0, maxLength) + "...";
        }
        return content;
    }
    
    // Getters
    public String getMessageId() { return messageId; }
    public User getSender() { return sender; }
    public String getContent() { return content; }
    public MessageType getMessageType() { return messageType; }
    public String getRecipientId() { return recipientId; }
    public String getRoomId() { return roomId; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public MessageStatus getStatus() { return status; }
    public LocalDateTime getEditedAt() { return editedAt; }
    public boolean isDeleted() { return isDeleted; }
    public String getServerId() { return serverId; }
    public int getDeliveryAttempts() { return deliveryAttempts; }
    public String getReplyToMessageId() { return replyToMessageId; }
    public String getThreadId() { return threadId; }
    public List<String> getAttachments() { return new ArrayList<>(attachments); }
    public Map<String, String> getReactions() { return new HashMap<>(reactions); }
    public User getRecipient() { return recipient; }
    
    // Setters
    public void setReplyToMessageId(String replyToMessageId) { this.replyToMessageId = replyToMessageId; }
    public void setThreadId(String threadId) { this.threadId = threadId; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(messageId, message.messageId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(messageId);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "messageId='" + messageId + '\'' +
                ", sender=" + sender.getDisplayName() +
                ", type=" + messageType +
                ", content='" + getDisplayContent() + '\'' +
                '}';
    }
} 