package com.systemdesign.lld.chat.service;

import com.systemdesign.lld.chat.model.Message;
import com.systemdesign.lld.chat.model.User;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * MessageService class for managing messages in the distributed chat system
 */
public class MessageService {
    private final Map<String, Message> messages; // messageId -> Message
    private final Map<String, List<String>> userMessages; // userId -> List of messageIds
    private final Map<String, List<String>> roomMessages; // roomId -> List of messageIds
    private final Map<String, List<String>> privateMessages; // userId -> List of private messageIds
    
    // Message queues for delivery
    private final BlockingQueue<Message> pendingMessages;
    private final List<Message> failedMessages;
    
    // Message threading
    private final Map<String, List<String>> threads; // threadId -> List of messageIds
    
    // Message history limits
    private final int maxMessagesPerRoom = 10000;
    private final int maxMessagesPerUser = 5000;
    private final int messageRetentionDays = 30;
    
    // Statistics
    private long totalMessagesSent;
    private long totalMessagesDelivered;
    private long totalMessagesFailed;
    
    public MessageService() {
        this.messages = new ConcurrentHashMap<>();
        this.userMessages = new ConcurrentHashMap<>();
        this.roomMessages = new ConcurrentHashMap<>();
        this.privateMessages = new ConcurrentHashMap<>();
        this.pendingMessages = new LinkedBlockingQueue<>();
        this.failedMessages = new ArrayList<>();
        this.threads = new ConcurrentHashMap<>();
        
        this.totalMessagesSent = 0;
        this.totalMessagesDelivered = 0;
        this.totalMessagesFailed = 0;
        
        System.out.println("Message service initialized");
    }
    
    /**
     * Send a message
     */
    public synchronized boolean sendMessage(Message message) {
        try {
            // Store message
            messages.put(message.getMessageId(), message);
            
            // Add to pending queue for delivery
            pendingMessages.offer(message);
            
            // Update statistics
            totalMessagesSent++;
            
            // Index message
            indexMessage(message);
            
            System.out.println("Message queued for delivery: " + message.getMessageId());
            return true;
            
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Index message for quick retrieval
     */
    private void indexMessage(Message message) {
        // Index by sender
        userMessages.computeIfAbsent(message.getSender().getUserId(), k -> new ArrayList<>())
                   .add(message.getMessageId());
        
        // Index by type
        if (message.isRoomMessage() && message.getRoomId() != null) {
            roomMessages.computeIfAbsent(message.getRoomId(), k -> new ArrayList<>())
                       .add(message.getMessageId());
        } else if (message.isPrivateMessage() && message.getRecipientId() != null) {
            // Index for both sender and recipient
            privateMessages.computeIfAbsent(message.getSender().getUserId(), k -> new ArrayList<>())
                          .add(message.getMessageId());
            privateMessages.computeIfAbsent(message.getRecipientId(), k -> new ArrayList<>())
                          .add(message.getMessageId());
        }
        
        // Thread indexing
        if (message.getThreadId() != null) {
            threads.computeIfAbsent(message.getThreadId(), k -> new ArrayList<>())
                   .add(message.getMessageId());
        }
    }
    
    /**
     * Process pending messages for delivery
     */
    public void processPendingMessages() {
        int processedCount = 0;
        
        // Process up to 100 messages per batch
        for (int i = 0; i < 100 && !pendingMessages.isEmpty(); i++) {
            Message message = pendingMessages.poll();
            if (message == null) break;
            
            if (deliverMessage(message)) {
                message.markAsDelivered();
                totalMessagesDelivered++;
                processedCount++;
            } else {
                message.incrementDeliveryAttempts();
                
                if (message.canRetryDelivery()) {
                    // Re-queue for retry
                    pendingMessages.offer(message);
                } else {
                    // Mark as failed
                    message.markAsFailed();
                    synchronized (failedMessages) {
                        failedMessages.add(message);
                    }
                    totalMessagesFailed++;
                }
            }
        }
        
        if (processedCount > 0) {
            System.out.println("Processed " + processedCount + " messages");
        }
    }
    
    /**
     * Attempt to deliver a message
     */
    private boolean deliverMessage(Message message) {
        try {
            // Simulate message delivery
            if (message.isRoomMessage()) {
                return deliverRoomMessage(message);
            } else if (message.isPrivateMessage()) {
                return deliverPrivateMessage(message);
            } else if (message.isSystemMessage()) {
                return deliverSystemMessage(message);
            }
            
            return false;
            
        } catch (Exception e) {
            System.err.println("Error delivering message " + message.getMessageId() + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Deliver room message
     */
    private boolean deliverRoomMessage(Message message) {
        // In a real system, this would send to all room members
        System.out.println("Delivering room message " + message.getMessageId() + " to room " + message.getRoomId());
        return true;
    }
    
    /**
     * Deliver private message
     */
    private boolean deliverPrivateMessage(Message message) {
        // In a real system, this would send to the recipient
        System.out.println("Delivering private message " + message.getMessageId() + " to user " + message.getRecipientId());
        return true;
    }
    
    /**
     * Deliver system message
     */
    private boolean deliverSystemMessage(Message message) {
        // In a real system, this would broadcast to all users
        System.out.println("Delivering system message " + message.getMessageId());
        return true;
    }
    
    /**
     * Get message by ID
     */
    public Message getMessage(String messageId) {
        return messages.get(messageId);
    }
    
    /**
     * Get messages for a user
     */
    public List<Message> getUserMessages(String userId, int limit) {
        List<String> messageIds = userMessages.get(userId);
        if (messageIds == null) {
            return new ArrayList<>();
        }
        
        // Get recent messages
        List<String> recentIds = messageIds.subList(
            Math.max(0, messageIds.size() - limit), 
            messageIds.size()
        );
        
        return recentIds.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
    
    /**
     * Get messages for a room
     */
    public List<Message> getRoomMessages(String roomId, int limit) {
        List<String> messageIds = roomMessages.get(roomId);
        if (messageIds == null) {
            return new ArrayList<>();
        }
        
        // Get recent messages
        List<String> recentIds = messageIds.subList(
            Math.max(0, messageIds.size() - limit), 
            messageIds.size()
        );
        
        return recentIds.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
    
    /**
     * Get private messages for a user
     */
    public List<Message> getPrivateMessages(String userId, int limit) {
        List<String> messageIds = privateMessages.get(userId);
        if (messageIds == null) {
            return new ArrayList<>();
        }
        
        // Get recent messages
        List<String> recentIds = messageIds.subList(
            Math.max(0, messageIds.size() - limit), 
            messageIds.size()
        );
        
        return recentIds.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .filter(msg -> msg.getMessageType() == Message.MessageType.PRIVATE_MESSAGE)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
    
    /**
     * Get conversation between two users
     */
    public List<Message> getConversationMessages(String user1Id, String user2Id, int limit) {
        List<Message> user1Private = getPrivateMessages(user1Id, Integer.MAX_VALUE);
        
        return user1Private.stream()
                .filter(msg -> 
                    (msg.getSender().getUserId().equals(user1Id) && msg.getRecipientId().equals(user2Id)) ||
                    (msg.getSender().getUserId().equals(user2Id) && msg.getRecipientId().equals(user1Id))
                )
                .sorted(Comparator.comparing(Message::getTimestamp))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Get messages in a thread
     */
    public List<Message> getThreadMessages(String threadId) {
        List<String> messageIds = threads.get(threadId);
        if (messageIds == null) {
            return new ArrayList<>();
        }
        
        return messageIds.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
    
    /**
     * Search messages by content
     */
    public List<Message> searchMessages(String query, String userId, String roomId, int limit) {
        String lowerQuery = query.toLowerCase();
        
        return messages.values().stream()
                .filter(msg -> {
                    // Filter by user or room if specified
                    if (userId != null && !msg.getSender().getUserId().equals(userId)) {
                        return false;
                    }
                    if (roomId != null && !roomId.equals(msg.getRoomId())) {
                        return false;
                    }
                    
                    // Search in content
                    return msg.getContent().toLowerCase().contains(lowerQuery);
                })
                .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Edit a message
     */
    public boolean editMessage(String messageId, String newContent, String editorUserId) {
        Message message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        
        // Check if user can edit this message
        if (!message.getSender().getUserId().equals(editorUserId)) {
            return false;
        }
        
        // Check edit time limit (e.g., 15 minutes)
        LocalDateTime editDeadline = message.getTimestamp().plusMinutes(15);
        if (LocalDateTime.now().isAfter(editDeadline)) {
            return false;
        }
        
        message.editContent(newContent);
        System.out.println("Message " + messageId + " edited by user " + editorUserId);
        return true;
    }
    
    /**
     * Delete a message
     */
    public boolean deleteMessage(String messageId, String deleterUserId) {
        Message message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        
        // Check if user can delete this message
        if (!message.getSender().getUserId().equals(deleterUserId)) {
            return false;
        }
        
        message.deleteMessage();
        System.out.println("Message " + messageId + " deleted by user " + deleterUserId);
        return true;
    }
    
    /**
     * Add reaction to a message
     */
    public boolean addReaction(String messageId, String userId, String emoji) {
        Message message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        
        message.addReaction(userId, emoji);
        System.out.println("User " + userId + " reacted to message " + messageId + " with " + emoji);
        return true;
    }
    
    /**
     * Remove reaction from a message
     */
    public boolean removeReaction(String messageId, String userId) {
        Message message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        
        message.removeReaction(userId);
        System.out.println("User " + userId + " removed reaction from message " + messageId);
        return true;
    }
    
    /**
     * Mark message as read by user
     */
    public boolean markAsRead(String messageId, String userId) {
        Message message = messages.get(messageId);
        if (message == null) {
            return false;
        }
        
        // In a real system, you'd track read status per user
        if (userId.equals(message.getRecipientId()) || message.getRoomId() != null) {
            message.markAsRead();
            return true;
        }
        
        return false;
    }
    
    /**
     * Get unread message count for user
     */
    public int getUnreadCount(String userId) {
        List<String> messageIds = privateMessages.get(userId);
        if (messageIds == null) {
            return 0;
        }
        
        return (int) messageIds.stream()
                .map(messages::get)
                .filter(Objects::nonNull)
                .filter(msg -> msg.getRecipientId().equals(userId) && 
                             msg.getStatus() != Message.MessageStatus.READ)
                .count();
    }
    
    /**
     * Clean up old messages based on retention policy
     */
    public void cleanupOldMessages() {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(messageRetentionDays);
        
        List<String> messagesToDelete = messages.values().stream()
                .filter(msg -> msg.getTimestamp().isBefore(cutoffDate))
                .map(Message::getMessageId)
                .collect(Collectors.toList());
        
        // Delete old messages
        for (String messageId : messagesToDelete) {
            deleteMessageCompletely(messageId);
        }
        
        if (!messagesToDelete.isEmpty()) {
            System.out.println("Cleaned up " + messagesToDelete.size() + " old messages");
        }
    }
    
    /**
     * Completely delete a message from all indexes
     */
    private void deleteMessageCompletely(String messageId) {
        Message message = messages.get(messageId);
        if (message == null) {
            return;
        }
        
        // Remove from all indexes
        userMessages.getOrDefault(message.getSender().getUserId(), new ArrayList<>()).remove(messageId);
        
        if (message.isRoomMessage() && message.getRoomId() != null) {
            roomMessages.getOrDefault(message.getRoomId(), new ArrayList<>()).remove(messageId);
        } else if (message.isPrivateMessage() && message.getRecipientId() != null) {
            privateMessages.getOrDefault(message.getSender().getUserId(), new ArrayList<>()).remove(messageId);
            privateMessages.getOrDefault(message.getRecipientId(), new ArrayList<>()).remove(messageId);
        }
        
        if (message.getThreadId() != null) {
            threads.getOrDefault(message.getThreadId(), new ArrayList<>()).remove(messageId);
        }
        
        // Remove from main storage
        messages.remove(messageId);
    }
    
    /**
     * Get all messages (for admin/debugging)
     */
    public List<Message> getAllMessages() {
        return new ArrayList<>(messages.values());
    }
    
    /**
     * Get message service statistics
     */
    public Map<String, Object> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalMessages", messages.size());
        stats.put("totalMessagesSent", totalMessagesSent);
        stats.put("totalMessagesDelivered", totalMessagesDelivered);
        stats.put("totalMessagesFailed", totalMessagesFailed);
        stats.put("pendingMessages", pendingMessages.size());
        stats.put("failedMessages", failedMessages.size());
        stats.put("threadsCount", threads.size());
        stats.put("roomsWithMessages", roomMessages.size());
        stats.put("usersWithMessages", userMessages.size());
        
        return stats;
    }
    
    @Override
    public String toString() {
        return "MessageService{messages=" + messages.size() + ", pending=" + pendingMessages.size() + "}";
    }
} 