package com.systemdesign.lld.socialmedia;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Main application class for the social media platform
 */
public class SocialMediaPlatform {
    private final Map<String, User> users;
    private final Map<String, Post> posts;
    private final Map<String, Conversation> conversations;
    private final Map<String, List<Post>> feeds;
    private final FeedService feedService;
    private final NotificationService notificationService;
    private final ScheduledExecutorService scheduledExecutor;
    private final Scanner scanner;
    
    private User currentUser;
    
    public SocialMediaPlatform() {
        this.users = new ConcurrentHashMap<>();
        this.posts = new ConcurrentHashMap<>();
        this.conversations = new ConcurrentHashMap<>();
        this.feeds = new ConcurrentHashMap<>();
        this.feedService = new FeedService();
        this.notificationService = new NotificationService();
        this.scheduledExecutor = Executors.newScheduledThreadPool(2);
        this.scanner = new Scanner(System.in);
        
        // Start background services
        startBackgroundServices();
        
        System.out.println("🚀 Social Media Platform started!");
    }
    
    /**
     * Start background services
     */
    private void startBackgroundServices() {
        // Feed generation service
        scheduledExecutor.scheduleAtFixedRate(() -> {
            feedService.generateFeeds();
        }, 0, 5, TimeUnit.MINUTES);
        
        // Notification processing service
        scheduledExecutor.scheduleAtFixedRate(() -> {
            notificationService.processNotifications();
        }, 0, 30, TimeUnit.SECONDS);
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
        System.out.println("         SOCIAL MEDIA PLATFORM");
        System.out.println("=".repeat(60));
        System.out.println("Connect, Share, and Discover with the world!");
        System.out.println("Total users: " + users.size());
        System.out.println("Total posts: " + posts.size());
        System.out.println("=".repeat(60));
    }
    
    /**
     * Display authentication menu
     */
    private void displayAuthMenu() {
        System.out.println("\n--- Authentication ---");
        System.out.println("1. Register");
        System.out.println("2. Login");
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
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                System.out.println("Thank you for using Social Media Platform!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid option. Please try again.");
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
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Create user
        String userId = UUID.randomUUID().toString().substring(0, 8);
        User user = new User(userId, username, email, password);
        users.put(userId, user);
        
        System.out.println("✅ User registered successfully!");
        System.out.println("User ID: " + userId);
        System.out.println("You can now login with username: " + username);
    }
    
    /**
     * User login
     */
    private void login() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        System.out.print("Enter password: ");
        String password = scanner.nextLine();
        
        // Find user
        User user = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (user == null || !user.validatePassword(password)) {
            System.out.println("Invalid username or password.");
            return;
        }
        
        currentUser = user;
        currentUser.setOnline(true);
        
        System.out.println("✅ Successfully logged in as " + username);
        System.out.println("Welcome back, " + currentUser.getUsername() + "!");
    }
    
    /**
     * Display main menu
     */
    private void displayMainMenu() {
        System.out.println("\n--- Main Menu ---");
        System.out.println("Current user: " + currentUser.getUsername());
        System.out.println("Followers: " + currentUser.getFollowersCount() + 
                          " | Following: " + currentUser.getFollowingCount() + 
                          " | Posts: " + currentUser.getPostsCount());
        System.out.println();
        System.out.println("1. Feed");
        System.out.println("2. Create Post");
        System.out.println("3. Profile Management");
        System.out.println("4. Social Network");
        System.out.println("5. Messages");
        System.out.println("6. Notifications");
        System.out.println("7. Search");
        System.out.println("8. Settings");
        System.out.println("9. Logout");
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
                showFeed();
                break;
            case 2:
                createPost();
                break;
            case 3:
                handleProfileManagement();
                break;
            case 4:
                handleSocialNetwork();
                break;
            case 5:
                handleMessages();
                break;
            case 6:
                showNotifications();
                break;
            case 7:
                handleSearch();
                break;
            case 8:
                handleSettings();
                break;
            case 9:
                logout();
                break;
            default:
                System.out.println("Invalid option. Please try again.");
        }
    }
    
    /**
     * Show feed
     */
    private void showFeed() {
        System.out.println("\n--- Your Feed ---");
        
        List<Post> feed = feedService.getUserFeed(currentUser.getUserId());
        
        if (feed.isEmpty()) {
            System.out.println("No posts in your feed. Follow more users to see their posts!");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        for (int i = 0; i < Math.min(10, feed.size()); i++) {
            Post post = feed.get(i);
            User author = users.get(post.getAuthorId());
            
            System.out.println("\n" + "─".repeat(50));
            System.out.println("📝 " + (author != null ? author.getUsername() : "Unknown") + 
                             " • " + post.getCreatedAt().format(formatter));
            System.out.println(post.getContent());
            
            if (!post.getMediaUrls().isEmpty()) {
                System.out.println("📎 Media: " + post.getMediaUrls().size() + " attachments");
            }
            
            if (!post.getHashtags().isEmpty()) {
                System.out.println("🏷️ " + post.getHashtags().stream()
                        .map(tag -> "#" + tag)
                        .collect(Collectors.joining(" ")));
            }
            
            System.out.println("👍 " + post.getLikesCount() + " likes | " +
                             "💬 " + post.getCommentsCount() + " comments | " +
                             "🔁 " + post.getSharesCount() + " shares");
        }
        
        System.out.println("\n" + "─".repeat(50));
        System.out.println("1. Like/Unlike a post");
        System.out.println("2. Comment on a post");
        System.out.println("3. Share a post");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        if (choice >= 1 && choice <= 3) {
            System.out.print("Enter post number (1-" + Math.min(10, feed.size()) + "): ");
            int postIndex = scanner.nextInt() - 1;
            scanner.nextLine(); // consume newline
            
            if (postIndex >= 0 && postIndex < Math.min(10, feed.size())) {
                Post selectedPost = feed.get(postIndex);
                
                switch (choice) {
                    case 1:
                        toggleLike(selectedPost);
                        break;
                    case 2:
                        addComment(selectedPost);
                        break;
                    case 3:
                        sharePost(selectedPost);
                        break;
                }
            } else {
                System.out.println("Invalid post number.");
            }
        }
    }
    
    /**
     * Create post
     */
    private void createPost() {
        System.out.println("\n--- Create Post ---");
        System.out.print("Enter post content: ");
        String content = scanner.nextLine();
        
        System.out.println("Select post type:");
        System.out.println("1. Text");
        System.out.println("2. Image");
        System.out.println("3. Video");
        System.out.println("4. Link");
        System.out.print("Choose type: ");
        
        int typeChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        PostType type;
        switch (typeChoice) {
            case 1:
                type = PostType.TEXT;
                break;
            case 2:
                type = PostType.IMAGE;
                break;
            case 3:
                type = PostType.VIDEO;
                break;
            case 4:
                type = PostType.LINK;
                break;
            default:
                type = PostType.TEXT;
        }
        
        // Create post
        String postId = UUID.randomUUID().toString().substring(0, 8);
        Post post = new Post(postId, currentUser.getUserId(), content, type);
        
        // Add media URLs if applicable
        if (type == PostType.IMAGE || type == PostType.VIDEO) {
            System.out.print("Enter media URL (optional): ");
            String mediaUrl = scanner.nextLine();
            if (!mediaUrl.trim().isEmpty()) {
                post.addMediaUrl(mediaUrl);
            }
        }
        
        // Set visibility
        System.out.println("Select visibility:");
        System.out.println("1. Public");
        System.out.println("2. Friends Only");
        System.out.println("3. Private");
        System.out.print("Choose visibility: ");
        
        int visibilityChoice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (visibilityChoice) {
            case 1:
                post.setVisibility(PrivacyLevel.PUBLIC);
                break;
            case 2:
                post.setVisibility(PrivacyLevel.FRIENDS_ONLY);
                break;
            case 3:
                post.setVisibility(PrivacyLevel.PRIVATE);
                break;
            default:
                post.setVisibility(PrivacyLevel.PUBLIC);
        }
        
        // Save post
        posts.put(postId, post);
        currentUser.addPost(post);
        
        // Send notifications to followers
        notificationService.sendPostNotifications(currentUser, post);
        
        System.out.println("✅ Post created successfully!");
        System.out.println("Post ID: " + postId);
    }
    
    /**
     * Handle profile management
     */
    private void handleProfileManagement() {
        System.out.println("\n--- Profile Management ---");
        System.out.println("1. View my profile");
        System.out.println("2. Edit profile");
        System.out.println("3. View my posts");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                viewProfile(currentUser);
                break;
            case 2:
                editProfile();
                break;
            case 3:
                viewUserPosts(currentUser);
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * View user profile
     */
    private void viewProfile(User user) {
        System.out.println("\n--- User Profile ---");
        System.out.println("Username: " + user.getUsername());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Bio: " + (user.getBio() != null ? user.getBio() : "No bio"));
        System.out.println("Joined: " + user.getJoinDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        System.out.println("Status: " + (user.isOnline() ? "Online" : "Offline"));
        System.out.println("Last Active: " + user.getLastActive().format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm")));
        System.out.println("Followers: " + user.getFollowersCount());
        System.out.println("Following: " + user.getFollowingCount());
        System.out.println("Posts: " + user.getPostsCount());
    }
    
    /**
     * Edit profile
     */
    private void editProfile() {
        System.out.println("\n--- Edit Profile ---");
        System.out.print("Enter new bio (current: " + (currentUser.getBio() != null ? currentUser.getBio() : "None") + "): ");
        String bio = scanner.nextLine();
        
        if (!bio.trim().isEmpty()) {
            currentUser.setBio(bio);
            System.out.println("✅ Bio updated successfully!");
        }
        
        System.out.print("Enter new profile picture URL (optional): ");
        String profilePicture = scanner.nextLine();
        
        if (!profilePicture.trim().isEmpty()) {
            currentUser.setProfilePicture(profilePicture);
            System.out.println("✅ Profile picture updated successfully!");
        }
    }
    
    /**
     * View user posts
     */
    private void viewUserPosts(User user) {
        System.out.println("\n--- Posts by " + user.getUsername() + " ---");
        
        List<Post> userPosts = user.getRecentPosts(10);
        
        if (userPosts.isEmpty()) {
            System.out.println("No posts yet.");
            return;
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm");
        
        for (Post post : userPosts) {
            System.out.println("\n" + "─".repeat(40));
            System.out.println("📝 " + post.getCreatedAt().format(formatter));
            System.out.println(post.getContent());
            System.out.println("👍 " + post.getLikesCount() + " likes | " +
                             "💬 " + post.getCommentsCount() + " comments | " +
                             "🔁 " + post.getSharesCount() + " shares");
        }
    }
    
    /**
     * Handle social network
     */
    private void handleSocialNetwork() {
        System.out.println("\n--- Social Network ---");
        System.out.println("1. Follow user");
        System.out.println("2. Unfollow user");
        System.out.println("3. View followers");
        System.out.println("4. View following");
        System.out.println("5. Block user");
        System.out.println("6. View user profile");
        System.out.println("7. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                followUser();
                break;
            case 2:
                unfollowUser();
                break;
            case 3:
                viewFollowers();
                break;
            case 4:
                viewFollowing();
                break;
            case 5:
                blockUser();
                break;
            case 6:
                viewOtherUserProfile();
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Follow user
     */
    private void followUser() {
        System.out.print("Enter username to follow: ");
        String username = scanner.nextLine();
        
        User targetUser = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        if (targetUser.getUserId().equals(currentUser.getUserId())) {
            System.out.println("You cannot follow yourself.");
            return;
        }
        
        currentUser.follow(targetUser.getUserId());
        targetUser.addFollower(currentUser.getUserId());
        
        // Send notification
        notificationService.sendFollowNotification(currentUser, targetUser);
        
        System.out.println("✅ Now following " + username);
    }
    
    /**
     * Unfollow user
     */
    private void unfollowUser() {
        System.out.print("Enter username to unfollow: ");
        String username = scanner.nextLine();
        
        User targetUser = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        currentUser.unfollow(targetUser.getUserId());
        targetUser.removeFollower(currentUser.getUserId());
        
        System.out.println("✅ Unfollowed " + username);
    }
    
    /**
     * View followers
     */
    private void viewFollowers() {
        System.out.println("\n--- Your Followers ---");
        
        Set<String> followers = currentUser.getFollowers();
        
        if (followers.isEmpty()) {
            System.out.println("No followers yet.");
            return;
        }
        
        for (String followerId : followers) {
            User follower = users.get(followerId);
            if (follower != null) {
                System.out.println("👤 " + follower.getUsername() + " (" + follower.getEmail() + ")");
            }
        }
    }
    
    /**
     * View following
     */
    private void viewFollowing() {
        System.out.println("\n--- You're Following ---");
        
        Set<String> following = currentUser.getFollowing();
        
        if (following.isEmpty()) {
            System.out.println("You're not following anyone yet.");
            return;
        }
        
        for (String followingId : following) {
            User followedUser = users.get(followingId);
            if (followedUser != null) {
                System.out.println("👤 " + followedUser.getUsername() + " (" + followedUser.getEmail() + ")");
            }
        }
    }
    
    /**
     * Block user
     */
    private void blockUser() {
        System.out.print("Enter username to block: ");
        String username = scanner.nextLine();
        
        User targetUser = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        if (targetUser.getUserId().equals(currentUser.getUserId())) {
            System.out.println("You cannot block yourself.");
            return;
        }
        
        currentUser.blockUser(targetUser.getUserId());
        System.out.println("✅ Blocked " + username);
    }
    
    /**
     * View other user profile
     */
    private void viewOtherUserProfile() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        
        User targetUser = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (targetUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        if (targetUser.canViewProfile(currentUser)) {
            viewProfile(targetUser);
            viewUserPosts(targetUser);
        } else {
            System.out.println("❌ This profile is private.");
        }
    }
    
    /**
     * Handle messages
     */
    private void handleMessages() {
        System.out.println("\n--- Messages ---");
        System.out.println("1. Send message");
        System.out.println("2. View conversations");
        System.out.println("3. View conversation");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                sendMessage();
                break;
            case 2:
                viewConversations();
                break;
            case 3:
                viewConversation();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Send message
     */
    private void sendMessage() {
        System.out.print("Enter recipient username: ");
        String username = scanner.nextLine();
        
        User recipient = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (recipient == null) {
            System.out.println("User not found.");
            return;
        }
        
        if (!currentUser.canSendMessage(recipient)) {
            System.out.println("❌ You cannot send messages to this user.");
            return;
        }
        
        System.out.print("Enter message: ");
        String content = scanner.nextLine();
        
        String messageId = UUID.randomUUID().toString().substring(0, 8);
        Message message = new Message(messageId, currentUser.getUserId(), recipient.getUserId(), content, MessageType.TEXT);
        
        // Add to conversations
        String conversationId = message.getConversationId();
        Conversation conversation = conversations.get(conversationId);
        
        if (conversation == null) {
            Set<String> participants = new HashSet<>();
            participants.add(currentUser.getUserId());
            participants.add(recipient.getUserId());
            conversation = new Conversation(conversationId, participants);
            conversations.put(conversationId, conversation);
        }
        
        conversation.addMessage(message);
        currentUser.addMessage(conversationId, message);
        recipient.addMessage(conversationId, message);
        
        System.out.println("✅ Message sent successfully!");
    }
    
    /**
     * View conversations
     */
    private void viewConversations() {
        System.out.println("\n--- Your Conversations ---");
        
        Set<String> userConversations = conversations.keySet().stream()
                .filter(convId -> {
                    Conversation conv = conversations.get(convId);
                    return conv.getParticipants().contains(currentUser.getUserId());
                })
                .collect(Collectors.toSet());
        
        if (userConversations.isEmpty()) {
            System.out.println("No conversations yet.");
            return;
        }
        
        for (String convId : userConversations) {
            Conversation conv = conversations.get(convId);
            Set<String> otherParticipants = conv.getParticipants().stream()
                    .filter(p -> !p.equals(currentUser.getUserId()))
                    .collect(Collectors.toSet());
            
            String otherUsernames = otherParticipants.stream()
                    .map(users::get)
                    .filter(Objects::nonNull)
                    .map(User::getUsername)
                    .collect(Collectors.joining(", "));
            
            System.out.println("💬 " + otherUsernames + " (" + conv.getMessages().size() + " messages)");
        }
    }
    
    /**
     * View conversation
     */
    private void viewConversation() {
        System.out.print("Enter other user's username: ");
        String username = scanner.nextLine();
        
        User otherUser = users.values().stream()
                .filter(u -> u.getUsername().equals(username))
                .findFirst()
                .orElse(null);
        
        if (otherUser == null) {
            System.out.println("User not found.");
            return;
        }
        
        String conversationId = new Message("", currentUser.getUserId(), otherUser.getUserId(), "", MessageType.TEXT).getConversationId();
        Conversation conversation = conversations.get(conversationId);
        
        if (conversation == null) {
            System.out.println("No conversation found.");
            return;
        }
        
        System.out.println("\n--- Conversation with " + username + " ---");
        
        List<Message> messages = conversation.getMessages();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        for (Message msg : messages) {
            String sender = msg.getSenderId().equals(currentUser.getUserId()) ? "You" : username;
            System.out.println("[" + msg.getSentAt().format(formatter) + "] " + sender + ": " + msg.getContent());
        }
    }
    
    /**
     * Toggle like on post
     */
    private void toggleLike(Post post) {
        boolean wasLiked = post.isLikedBy(currentUser.getUserId());
        
        if (wasLiked) {
            post.unlikePost(currentUser.getUserId());
            System.out.println("👍 Unliked post");
        } else {
            post.likePost(currentUser.getUserId());
            System.out.println("👍 Liked post");
            
            // Send notification to post author
            User author = users.get(post.getAuthorId());
            if (author != null) {
                notificationService.sendLikeNotification(currentUser, author, post);
            }
        }
    }
    
    /**
     * Add comment to post
     */
    private void addComment(Post post) {
        System.out.print("Enter comment: ");
        String comment = scanner.nextLine();
        
        String commentId = post.addComment(currentUser.getUserId(), comment);
        System.out.println("✅ Comment added successfully!");
        
        // Send notification to post author
        User author = users.get(post.getAuthorId());
        if (author != null) {
            notificationService.sendCommentNotification(currentUser, author, post);
        }
    }
    
    /**
     * Share post
     */
    private void sharePost(Post post) {
        boolean shared = post.sharePost(currentUser.getUserId());
        
        if (shared) {
            System.out.println("✅ Post shared successfully!");
            
            // Send notification to post author
            User author = users.get(post.getAuthorId());
            if (author != null) {
                notificationService.sendShareNotification(currentUser, author, post);
            }
        } else {
            System.out.println("❌ Failed to share post.");
        }
    }
    
    /**
     * Show notifications
     */
    private void showNotifications() {
        System.out.println("\n--- Notifications ---");
        
        List<String> notifications = notificationService.getUserNotifications(currentUser.getUserId());
        
        if (notifications.isEmpty()) {
            System.out.println("No new notifications.");
            return;
        }
        
        for (String notification : notifications) {
            System.out.println("🔔 " + notification);
        }
    }
    
    /**
     * Handle search
     */
    private void handleSearch() {
        System.out.println("\n--- Search ---");
        System.out.println("1. Search users");
        System.out.println("2. Search posts");
        System.out.println("3. Search hashtags");
        System.out.println("4. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                searchUsers();
                break;
            case 2:
                searchPosts();
                break;
            case 3:
                searchHashtags();
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Search users
     */
    private void searchUsers() {
        System.out.print("Enter username to search: ");
        String query = scanner.nextLine().toLowerCase();
        
        List<User> matchingUsers = users.values().stream()
                .filter(u -> u.getUsername().toLowerCase().contains(query))
                .limit(10)
                .collect(Collectors.toList());
        
        if (matchingUsers.isEmpty()) {
            System.out.println("No users found.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        for (User user : matchingUsers) {
            System.out.println("👤 " + user.getUsername() + " (" + user.getEmail() + ")");
            System.out.println("   Followers: " + user.getFollowersCount() + 
                             " | Following: " + user.getFollowingCount());
        }
    }
    
    /**
     * Search posts
     */
    private void searchPosts() {
        System.out.print("Enter content to search: ");
        String query = scanner.nextLine().toLowerCase();
        
        List<Post> matchingPosts = posts.values().stream()
                .filter(p -> p.getContent().toLowerCase().contains(query))
                .filter(p -> p.canView(currentUser))
                .limit(10)
                .collect(Collectors.toList());
        
        if (matchingPosts.isEmpty()) {
            System.out.println("No posts found.");
            return;
        }
        
        System.out.println("\n--- Search Results ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        for (Post post : matchingPosts) {
            User author = users.get(post.getAuthorId());
            System.out.println("📝 " + (author != null ? author.getUsername() : "Unknown") + 
                             " • " + post.getCreatedAt().format(formatter));
            System.out.println("   " + post.getContent().substring(0, Math.min(100, post.getContent().length())));
            System.out.println("   👍 " + post.getLikesCount() + " likes");
            System.out.println();
        }
    }
    
    /**
     * Search hashtags
     */
    private void searchHashtags() {
        System.out.print("Enter hashtag to search (without #): ");
        String hashtag = scanner.nextLine().toLowerCase();
        
        List<Post> matchingPosts = posts.values().stream()
                .filter(p -> p.getHashtags().contains(hashtag))
                .filter(p -> p.canView(currentUser))
                .limit(10)
                .collect(Collectors.toList());
        
        if (matchingPosts.isEmpty()) {
            System.out.println("No posts found with hashtag #" + hashtag);
            return;
        }
        
        System.out.println("\n--- Posts with #" + hashtag + " ---");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy");
        
        for (Post post : matchingPosts) {
            User author = users.get(post.getAuthorId());
            System.out.println("📝 " + (author != null ? author.getUsername() : "Unknown") + 
                             " • " + post.getCreatedAt().format(formatter));
            System.out.println("   " + post.getContent().substring(0, Math.min(100, post.getContent().length())));
            System.out.println("   👍 " + post.getLikesCount() + " likes");
            System.out.println();
        }
    }
    
    /**
     * Handle settings
     */
    private void handleSettings() {
        System.out.println("\n--- Settings ---");
        System.out.println("1. Privacy settings");
        System.out.println("2. Account settings");
        System.out.println("3. Back to main menu");
        System.out.print("Choose an option: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                updatePrivacySettings();
                break;
            case 2:
                updateAccountSettings();
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
        }
    }
    
    /**
     * Update privacy settings
     */
    private void updatePrivacySettings() {
        System.out.println("\n--- Privacy Settings ---");
        PrivacySettings settings = currentUser.getPrivacySettings();
        
        System.out.println("Current profile visibility: " + settings.getProfileVisibility());
        System.out.println("1. Public");
        System.out.println("2. Friends Only");
        System.out.println("3. Private");
        System.out.print("Choose profile visibility: ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        
        switch (choice) {
            case 1:
                settings.setProfileVisibility(PrivacyLevel.PUBLIC);
                break;
            case 2:
                settings.setProfileVisibility(PrivacyLevel.FRIENDS_ONLY);
                break;
            case 3:
                settings.setProfileVisibility(PrivacyLevel.PRIVATE);
                break;
            default:
                System.out.println("Invalid option.");
                return;
        }
        
        System.out.println("✅ Privacy settings updated successfully!");
    }
    
    /**
     * Update account settings
     */
    private void updateAccountSettings() {
        System.out.println("\n--- Account Settings ---");
        System.out.print("Enter new email (current: " + currentUser.getEmail() + "): ");
        String email = scanner.nextLine();
        
        if (!email.trim().isEmpty()) {
            currentUser.setEmail(email);
            System.out.println("✅ Email updated successfully!");
        }
    }
    
    /**
     * Logout
     */
    private void logout() {
        if (currentUser != null) {
            currentUser.setOnline(false);
            currentUser = null;
            System.out.println("✅ Logged out successfully!");
        }
    }
    
    /**
     * Main method
     */
    public static void main(String[] args) {
        SocialMediaPlatform platform = new SocialMediaPlatform();
        
        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n🔄 Shutting down Social Media Platform...");
            platform.scheduledExecutor.shutdown();
            System.out.println("✅ Shutdown complete.");
        }));
        
        platform.run();
    }
    
    /**
     * Inner class for feed service
     */
    private class FeedService {
        public List<Post> getUserFeed(String userId) {
            User user = users.get(userId);
            if (user == null) {
                return new ArrayList<>();
            }
            
            // Get posts from followed users
            Set<String> following = user.getFollowing();
            
            return posts.values().stream()
                    .filter(post -> following.contains(post.getAuthorId()) || post.getAuthorId().equals(userId))
                    .filter(post -> post.canView(user))
                    .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                    .collect(Collectors.toList());
        }
        
        public void generateFeeds() {
            for (User user : users.values()) {
                List<Post> feed = getUserFeed(user.getUserId());
                feeds.put(user.getUserId(), feed);
            }
        }
    }
    
    /**
     * Inner class for notification service
     */
    private class NotificationService {
        private final Map<String, List<String>> notifications = new ConcurrentHashMap<>();
        
        public void sendFollowNotification(User follower, User followed) {
            String message = follower.getUsername() + " started following you";
            addNotification(followed.getUserId(), message);
        }
        
        public void sendLikeNotification(User liker, User author, Post post) {
            String message = liker.getUsername() + " liked your post";
            addNotification(author.getUserId(), message);
        }
        
        public void sendCommentNotification(User commenter, User author, Post post) {
            String message = commenter.getUsername() + " commented on your post";
            addNotification(author.getUserId(), message);
        }
        
        public void sendShareNotification(User sharer, User author, Post post) {
            String message = sharer.getUsername() + " shared your post";
            addNotification(author.getUserId(), message);
        }
        
        public void sendPostNotifications(User author, Post post) {
            for (String followerId : author.getFollowers()) {
                String message = author.getUsername() + " posted something new";
                addNotification(followerId, message);
            }
        }
        
        private void addNotification(String userId, String message) {
            notifications.computeIfAbsent(userId, k -> new ArrayList<>()).add(message);
        }
        
        public List<String> getUserNotifications(String userId) {
            return notifications.getOrDefault(userId, new ArrayList<>());
        }
        
        public void processNotifications() {
            // Process and potentially clean up old notifications
            long currentTime = System.currentTimeMillis();
            // Implementation would handle notification processing
        }
    }
} 