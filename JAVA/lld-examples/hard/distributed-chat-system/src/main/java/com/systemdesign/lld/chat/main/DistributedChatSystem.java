package com.systemdesign.lld.chat.main;

import com.systemdesign.lld.chat.model.ChatRoom;
import com.systemdesign.lld.chat.model.Message;
import com.systemdesign.lld.chat.model.User;
import com.systemdesign.lld.chat.server.ChatServer;
import com.systemdesign.lld.chat.service.MessageService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Main application class for the distributed chat system
 */
public class DistributedChatSystem {
    private final Map<String, User> users;
    private final Map<String, ChatRoom> rooms;
    private final Map<String, ChatServer> servers;
    private final MessageService messageService;
    private final ScheduledExecutorService scheduledExecutor;
    private final Scanner scanner;
    private User currentUser;
    private ChatServer currentServer;
    
    public DistributedChatSystem() {
        this.users = new HashMap<>();
        this.rooms = new HashMap<>();
        this.servers = new HashMap<>();
        this.messageService = new MessageService();
        this.scheduledExecutor = Executors.newScheduledThreadPool(3);
        this.scanner = new Scanner(System.in);
        
        // Initialize servers
        initializeServers();
        
        // Start background services
        startBackgroundServices();
        
        System.out.println("🚀 Distributed Chat System started!");
        System.out.println("Available servers: " + servers.keySet());
    }
    
    /**
     * Initialize chat servers
     */
    private void initializeServers() {
        servers.put("us-east-1", new ChatServer("us-east-1", "US East"));
        servers.put("us-west-1", new ChatServer("us-west-1", "US West"));
        servers.put("eu-west-1", new ChatServer("eu-west-1", "Europe West"));
        
        System.out.println("Initialized 3 chat servers across different regions");
    }
    
    /**
     * Start background services
     */
    private void startBackgroundServices() {
        // Message delivery service
        scheduledExecutor.scheduleAtFixedRate(() -> {
            messageService.processPendingMessages();
        }, 1, 1, TimeUnit.SECONDS);
        
        // Server heartbeat service
        scheduledExecutor.scheduleAtFixedRate(() -> {
            for (ChatServer server : servers.values()) {
                server.updateUserPresence();
            }
        }, 5, 5, TimeUnit.SECONDS);
        
        // Cleanup service
        scheduledExecutor.scheduleAtFixedRate(() -> {
            messageService.cleanupOldMessages();
        }, 1, 1, TimeUnit.HOURS);
    }
    
    /**
     * Main application loop
     */
    public void run() {
        displayWelcome();
        
        while (true) {
            try {
                if (currentUser == null) {
                    displayAuthMenu();
                    handleAuthMenu();
                } else {
                    displayMainMenu();
                    handleMainMenu();
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Display welcome message
     */
    private void displayWelcome() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("         DISTRIBUTED CHAT SYSTEM");
        System.out.println("=".repeat(60));
        System.out.println("Multi-server, real-time messaging platform");
        System.out.println("Active servers: " + servers.size());
        System.out.println("=".repeat(60));
    }
    
    /**
     * Display authentication menu
     */
    private void displayAuthMenu() {
        System.out.println("\n--- Authentication ---");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Exit");
        System.out.print("Choose an option: ");
    }
    
    /**
     * Handle authentication menu
     */
    private void handleAuthMenu() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                login();
                break;
            case 2:
                register();
                break;
            case 3:
                System.out.println("Thank you for using Distributed Chat System!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * User login
     */
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Find user
        User user = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (user == null) {
            System.out.println("User not found. Please register first.");
            return;
        }
        
        // Load balance to find best server
        ChatServer bestServer = findBestServer();
        if (bestServer == null) {
            System.out.println("No servers available. Please try again later.");
            return;
        }
        
        // Connect to server
        bestServer.addUser(user);
        String sessionId = bestServer.connectUser(user.getUserId());
        
        if (sessionId != null) {
            currentUser = user;
            currentServer = bestServer;
            System.out.println("✅ Successfully logged in as " + username);
            System.out.println("Connected to server: " + bestServer.getServerId());
        } else {
            System.out.println("❌ Failed to connect to server. Please try again.");
        }
    }
    
    /**
     * User registration
     */
    private void register() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        // Check if username already exists
        boolean userExists = users.values().stream()
                .anyMatch(u -> u.getUsername().equals(username));
        
        if (userExists) {
            System.out.println("Username already exists. Please choose a different one.");
            return;
        }
        
        System.out.print("Enter email: ");
        String email = scanner.nextLine();
        
        System.out.print("Enter display name: ");
        String displayName = scanner.nextLine();
        
        // Create user
        User user = new User(username, email, displayName);
        users.put(user.getUserId(), user);
        
        System.out.println("✅ User registered successfully!");
        System.out.println("User ID: " + user.getUserId());
        System.out.println("You can now login with username: " + username);
    }
    
    /**
     * Display main menu
     */
    private void displayMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Current user: " + currentUser.getDisplayName() + " (@" + currentUser.getUsername() + ")");
        System.out.println("Connected to: " + currentServer.getServerId());
        System.out.println();
        System.out.println("1. Chat Rooms");
        System.out.println("2. Private Messages");
        System.out.println("3. User Management");
        System.out.println("4. Server Statistics");
        System.out.println("5. System Information");
        System.out.println("6. Logout");
        System.out.print("Choose an option: ");
    }
    
    /**
     * Handle main menu
     */
    private void handleMainMenu() {
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                handleChatRooms();
                break;
            case 2:
                handlePrivateMessages();
                break;
            case 3:
                handleUserManagement();
                break;
            case 4:
                showServerStatistics();
                break;
            case 5:
                showSystemInformation();
                break;
            case 6:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Handle chat rooms
     */
    private void handleChatRooms() {
        System.out.println("\n--- Chat Rooms ---");
        System.out.println("1. List all rooms");
        System.out.println("2. Create room");
        System.out.println("3. Join room");
        System.out.println("4. Send message to room");
        System.out.println("5. View room messages");
        System.out.println("6. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                listRooms();
                break;
            case 2:
                createRoom();
                break;
            case 3:
                joinRoom();
                break;
            case 4:
                sendRoomMessage();
                break;
            case 5:
                viewRoomMessages();
                break;
            case 6:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * List all rooms
     */
    private void listRooms() {
        System.out.println("\n--- Available Rooms ---");
        if (rooms.isEmpty()) {
            System.out.println("No rooms available.");
            return;
        }
        
        for (ChatRoom room : rooms.values()) {
            System.out.printf("🏠 %s (%s)\n", room.getName(), room.getRoomId());
            System.out.printf("   Description: %s\n", room.getDescription());
            System.out.printf("   Members: %d | Messages: %d | Active: %s\n",
                    room.getMemberCount(), room.getTotalMessages(), room.isActive() ? "Yes" : "No");
            System.out.println();
        }
    }
    
    /**
     * Create room
     */
    private void createRoom() {
        System.out.print("Enter room name: ");
        String name = scanner.nextLine();
        
        System.out.print("Enter room description: ");
        String description = scanner.nextLine();
        
        ChatRoom room = new ChatRoom(name, description, currentUser);
        rooms.put(room.getRoomId(), room);
        
        System.out.println("✅ Room created successfully!");
        System.out.println("Room ID: " + room.getRoomId());
        System.out.println("You are now the admin of this room.");
    }
    
    /**
     * Join room
     */
    private void joinRoom() {
        System.out.print("Enter room ID: ");
        String roomId = scanner.nextLine();
        
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        
        if (room.addMember(currentUser.getUserId())) {
            System.out.println("✅ Successfully joined room: " + room.getName());
        } else {
            System.out.println("❌ Failed to join room. You might be banned or room is full.");
        }
    }
    
    /**
     * Send message to room
     */
    private void sendRoomMessage() {
        System.out.print("Enter room ID: ");
        String roomId = scanner.nextLine();
        
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        
        if (!room.canSendMessage(currentUser.getUserId())) {
            System.out.println("❌ You don't have permission to send messages in this room.");
            return;
        }
        
        System.out.print("Enter message: ");
        String content = scanner.nextLine();
        
        Message message = new Message(currentUser, content, Message.MessageType.ROOM_MESSAGE);
        message.setRoomId(roomId);
        
        if (messageService.sendMessage(message)) {
            room.incrementMessageCount();
            System.out.println("✅ Message sent successfully!");
        } else {
            System.out.println("❌ Failed to send message.");
        }
    }
    
    /**
     * View room messages
     */
    private void viewRoomMessages() {
        System.out.print("Enter room ID: ");
        String roomId = scanner.nextLine();
        
        ChatRoom room = rooms.get(roomId);
        if (room == null) {
            System.out.println("Room not found.");
            return;
        }
        
        if (!room.isMember(currentUser.getUserId())) {
            System.out.println("❌ You must be a member to view messages.");
            return;
        }
        
        List<Message> messages = messageService.getRoomMessages(roomId, 20);
        
        System.out.println("\n--- Recent Messages in " + room.getName() + " ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (Message msg : messages) {
            System.out.printf("[%s] %s: %s\n", 
                    msg.getTimestamp().format(formatter),
                    msg.getSender().getDisplayName(),
                    msg.getContent());
        }
    }
    
    /**
     * Handle private messages
     */
    private void handlePrivateMessages() {
        System.out.println("\n--- Private Messages ---");
        System.out.println("1. Send private message");
        System.out.println("2. View conversations");
        System.out.println("3. View unread messages");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                sendPrivateMessage();
                break;
            case 2:
                viewConversations();
                break;
            case 3:
                viewUnreadMessages();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Send private message
     */
    private void sendPrivateMessage() {
        System.out.print("Enter recipient username: ");
        String recipientUsername = scanner.nextLine();
        
        User recipient = users.values().stream()
                .filter(u -> u.getUsername().equals(recipientUsername))
                .findFirst()
                .orElse(null);
        
        if (recipient == null) {
            System.out.println("User not found.");
            return;
        }
        
        System.out.print("Enter message: ");
        String content = scanner.nextLine();
        
        Message message = new Message(currentUser, content, Message.MessageType.PRIVATE_MESSAGE);
        message.setRecipient(recipient);
        
        if (messageService.sendMessage(message)) {
            System.out.println("✅ Private message sent successfully!");
        } else {
            System.out.println("❌ Failed to send message.");
        }
    }
    
    /**
     * View conversations
     */
    private void viewConversations() {
        System.out.print("Enter other user's username: ");
        String otherUsername = scanner.nextLine();
        
        User otherUser = users.values().stream()
                .filter(u -> u.getUsername().equals(otherUsername))
                .findFirst()
                .orElse(null);
        
        if (otherUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        List<Message> messages = messageService.getConversationMessages(
                currentUser.getUserId(), otherUser.getUserId(), 20);
        
        System.out.println("\n--- Conversation with " + otherUser.getDisplayName() + " ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        for (Message msg : messages) {
            String prefix = msg.getSender().getUserId().equals(currentUser.getUserId()) ? "You" : otherUser.getDisplayName();
            System.out.printf("[%s] %s: %s\n", 
                    msg.getTimestamp().format(formatter),
                    prefix,
                    msg.getContent());
        }
    }
    
    /**
     * View unread messages
     */
    private void viewUnreadMessages() {
        int unreadCount = messageService.getUnreadCount(currentUser.getUserId());
        System.out.println("You have " + unreadCount + " unread messages.");
        
        List<Message> privateMessages = messageService.getPrivateMessages(currentUser.getUserId(), 50);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        
        System.out.println("\n--- Recent Private Messages ---");
        for (Message msg : privateMessages) {
            String status = msg.getStatus() == Message.MessageStatus.READ ? "✓" : "●";
            System.out.printf("[%s] %s %s: %s\n", 
                    msg.getTimestamp().format(formatter),
                    status,
                    msg.getSender().getDisplayName(),
                    msg.getContent());
        }
    }
    
    /**
     * Handle user management
     */
    private void handleUserManagement() {
        System.out.println("\n--- User Management ---");
        System.out.println("1. List online users");
        System.out.println("2. View user profile");
        System.out.println("3. Update status message");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                listOnlineUsers();
                break;
            case 2:
                viewUserProfile();
                break;
            case 3:
                updateStatusMessage();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * List online users
     */
    private void listOnlineUsers() {
        System.out.println("\n--- Online Users ---");
        
        for (ChatServer server : servers.values()) {
            List<String> onlineUsers = server.getOnlineUsers();
            if (!onlineUsers.isEmpty()) {
                System.out.println("Server: " + server.getServerId());
                for (String userId : onlineUsers) {
                    User user = users.get(userId);
                    if (user != null) {
                        System.out.printf("  🟢 %s (@%s)\n", user.getDisplayName(), user.getUsername());
                    }
                }
            }
        }
    }
    
    /**
     * View user profile
     */
    private void viewUserProfile() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        User user = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (user == null) {
            System.out.println("User not found.");
            return;
        }
        
        System.out.println("\n--- User Profile ---");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Display Name: " + user.getDisplayName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Status: " + (user.isOnline() ? "Online" : "Offline"));
        System.out.println("Status Message: " + user.getStatusMessage());
        System.out.println("Last Seen: " + user.getLastSeen());
        System.out.println("Created: " + user.getCreatedAt());
        
        if (user.isOnline()) {
            System.out.println("Server: " + user.getServerId());
            System.out.println("Connection Duration: " + user.getConnectionDuration() + " seconds");
        }
    }
    
    /**
     * Update status message
     */
    private void updateStatusMessage() {
        System.out.print("Enter new status message: ");
        String status = scanner.nextLine();
        
        currentUser.setStatusMessage(status);
        System.out.println("✅ Status message updated successfully!");
    }
    
    /**
     * Show server statistics
     */
    private void showServerStatistics() {
        System.out.println("\n--- Server Statistics ---");
        
        for (ChatServer server : servers.values()) {
            Map<String, Object> stats = server.getServerStats();
            System.out.println("Server: " + stats.get("serverId"));
            System.out.println("  Region: " + stats.get("region"));
            System.out.println("  Total Users: " + stats.get("totalUsers"));
            System.out.println("  Online Users: " + stats.get("onlineUsers"));
            System.out.println("  Load Factor: " + String.format("%.2f", stats.get("loadFactor")));
            System.out.println("  Total Connections: " + stats.get("totalConnections"));
            System.out.println("  Uptime: " + stats.get("uptimeSeconds") + " seconds");
            System.out.println();
        }
    }
    
    /**
     * Show system information
     */
    private void showSystemInformation() {
        System.out.println("\n--- System Information ---");
        
        Map<String, Object> messageStats = messageService.getStatistics();
        System.out.println("📊 Message Statistics:");
        System.out.println("  Total Messages: " + messageStats.get("totalMessages"));
        System.out.println("  Messages Sent: " + messageStats.get("totalMessagesSent"));
        System.out.println("  Messages Delivered: " + messageStats.get("totalMessagesDelivered"));
        System.out.println("  Messages Failed: " + messageStats.get("totalMessagesFailed"));
        System.out.println("  Pending Messages: " + messageStats.get("pendingMessages"));
        
        System.out.println("\n🏠 Room Statistics:");
        System.out.println("  Total Rooms: " + rooms.size());
        System.out.println("  Active Rooms: " + rooms.values().stream().mapToInt(r -> r.isActive() ? 1 : 0).sum());
        
        System.out.println("\n👥 User Statistics:");
        System.out.println("  Total Users: " + users.size());
        System.out.println("  Online Users: " + servers.values().stream().mapToInt(ChatServer::getOnlineCount).sum());
        
        System.out.println("\n🖥️ Server Statistics:");
        System.out.println("  Total Servers: " + servers.size());
        System.out.println("  Active Servers: " + servers.values().stream().mapToInt(s -> s.isActive() ? 1 : 0).sum());
    }
    
    /**
     * Logout
     */
    private void logout() {
        if (currentServer != null) {
            currentServer.disconnectUser(currentUser.getUserId());
        }
        currentUser = null;
        currentServer = null;
        System.out.println("✅ Logged out successfully!");
    }
    
    /**
     * Find best server for load balancing
     */
    private ChatServer findBestServer() {
        return servers.values().stream()
                .filter(ChatServer::canAcceptConnections)
                .min(Comparator.comparing(ChatServer::getLoadFactor))
                .orElse(null);
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        DistributedChatSystem system = new DistributedChatSystem();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔄 Shutting down Distributed Chat System...");
            for (ChatServer server : system.servers.values()) {
                server.shutdown();
            }
            system.scheduledExecutor.shutdown();
            System.out.println("✅ Shutdown complete.");
        }));
        
        system.run();
    }
} 