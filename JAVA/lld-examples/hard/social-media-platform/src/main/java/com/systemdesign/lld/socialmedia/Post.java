package com.systemdesign.lld.socialmedia;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a post in the social media platform
 */
public class Post {
    private String postId;
    private String authorId;
    private String content;
    private List<String> mediaUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private PostType type;
    private PrivacyLevel visibility;
    
    // Engagement
    private Set<String> likes;
    private Set<String> dislikes;
    private Map<String, Comment> comments;
    private Set<String> shares;
    private Set<String> bookmarks;
    
    // Tags and mentions
    private Set<String> hashtags;
    private Set<String> mentions;
    
    // Location
    private String location;
    
    // Moderation
    private boolean isReported;
    private Set<String> reportedBy;
    private PostStatus status;
    
    public Post(String postId, String authorId, String content, PostType type) {
        this.postId = postId;
        this.authorId = authorId;
        this.content = content;
        this.type = type;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.visibility = PrivacyLevel.PUBLIC;
        this.status = PostStatus.ACTIVE;
        
        this.likes = ConcurrentHashMap.newKeySet();
        this.dislikes = ConcurrentHashMap.newKeySet();
        this.comments = new ConcurrentHashMap<>();
        this.shares = ConcurrentHashMap.newKeySet();
        this.bookmarks = ConcurrentHashMap.newKeySet();
        
        this.hashtags = new HashSet<>();
        this.mentions = new HashSet<>();
        this.reportedBy = ConcurrentHashMap.newKeySet();
        this.mediaUrls = new ArrayList<>();
        
        extractHashtagsAndMentions();
    }
    
    // Engagement methods
    public boolean likePost(String userId) {
        if (!userId.equals(authorId) && !dislikes.contains(userId)) {
            boolean added = likes.add(userId);
            dislikes.remove(userId); // Remove dislike if exists
            return added;
        }
        return false;
    }
    
    public boolean unlikePost(String userId) {
        return likes.remove(userId);
    }
    
    public boolean dislikePost(String userId) {
        if (!userId.equals(authorId) && !likes.contains(userId)) {
            boolean added = dislikes.add(userId);
            likes.remove(userId); // Remove like if exists
            return added;
        }
        return false;
    }
    
    public boolean undislikePost(String userId) {
        return dislikes.remove(userId);
    }
    
    public String addComment(String userId, String commentContent) {
        String commentId = UUID.randomUUID().toString().substring(0, 8);
        Comment comment = new Comment(commentId, userId, commentContent, this.postId);
        comments.put(commentId, comment);
        return commentId;
    }
    
    public boolean removeComment(String commentId, String userId) {
        Comment comment = comments.get(commentId);
        if (comment != null && (comment.getAuthorId().equals(userId) || this.authorId.equals(userId))) {
            comments.remove(commentId);
            return true;
        }
        return false;
    }
    
    public boolean sharePost(String userId) {
        if (!userId.equals(authorId)) {
            return shares.add(userId);
        }
        return false;
    }
    
    public boolean bookmarkPost(String userId) {
        return bookmarks.add(userId);
    }
    
    public boolean unbookmarkPost(String userId) {
        return bookmarks.remove(userId);
    }
    
    // Content methods
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
        extractHashtagsAndMentions();
    }
    
    public void addMediaUrl(String mediaUrl) {
        this.mediaUrls.add(mediaUrl);
        this.updatedAt = LocalDateTime.now();
    }
    
    public void removeMediaUrl(String mediaUrl) {
        this.mediaUrls.remove(mediaUrl);
        this.updatedAt = LocalDateTime.now();
    }
    
    private void extractHashtagsAndMentions() {
        hashtags.clear();
        mentions.clear();
        
        if (content != null) {
            String[] words = content.split("\\s+");
            for (String word : words) {
                if (word.startsWith("#") && word.length() > 1) {
                    hashtags.add(word.substring(1).toLowerCase());
                } else if (word.startsWith("@") && word.length() > 1) {
                    mentions.add(word.substring(1).toLowerCase());
                }
            }
        }
    }
    
    // Moderation methods
    public boolean reportPost(String userId, String reason) {
        if (!userId.equals(authorId)) {
            reportedBy.add(userId);
            this.isReported = true;
            return true;
        }
        return false;
    }
    
    public void moderatePost(PostStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Engagement statistics
    public int getLikesCount() {
        return likes.size();
    }
    
    public int getDislikesCount() {
        return dislikes.size();
    }
    
    public int getCommentsCount() {
        return comments.size();
    }
    
    public int getSharesCount() {
        return shares.size();
    }
    
    public int getBookmarksCount() {
        return bookmarks.size();
    }
    
    public double getEngagementRate() {
        return (double) (likes.size() + comments.size() + shares.size()) / Math.max(1, shares.size());
    }
    
    public List<Comment> getComments() {
        return new ArrayList<>(comments.values());
    }
    
    public List<Comment> getTopComments(int limit) {
        return comments.values().stream()
                .sorted((c1, c2) -> Integer.compare(c2.getLikesCount(), c1.getLikesCount()))
                .limit(limit)
                .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
    }
    
    // Validation methods
    public boolean isLikedBy(String userId) {
        return likes.contains(userId);
    }
    
    public boolean isDislikedBy(String userId) {
        return dislikes.contains(userId);
    }
    
    public boolean isSharedBy(String userId) {
        return shares.contains(userId);
    }
    
    public boolean isBookmarkedBy(String userId) {
        return bookmarks.contains(userId);
    }
    
    public boolean canView(User viewer) {
        if (status != PostStatus.ACTIVE) {
            return false;
        }
        
        if (viewer.getUserId().equals(authorId)) {
            return true;
        }
        
        switch (visibility) {
            case PUBLIC:
                return true;
            case FRIENDS_ONLY:
                return viewer.getFollowing().contains(authorId) && viewer.getFollowers().contains(authorId);
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }
    
    // Getters and setters
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public List<String> getMediaUrls() { return new ArrayList<>(mediaUrls); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public PostType getType() { return type; }
    public PrivacyLevel getVisibility() { return visibility; }
    public Set<String> getLikes() { return new HashSet<>(likes); }
    public Set<String> getDislikes() { return new HashSet<>(dislikes); }
    public Set<String> getShares() { return new HashSet<>(shares); }
    public Set<String> getBookmarks() { return new HashSet<>(bookmarks); }
    public Set<String> getHashtags() { return new HashSet<>(hashtags); }
    public Set<String> getMentions() { return new HashSet<>(mentions); }
    public String getLocation() { return location; }
    public boolean isReported() { return isReported; }
    public Set<String> getReportedBy() { return new HashSet<>(reportedBy); }
    public PostStatus getStatus() { return status; }
    
    public void setVisibility(PrivacyLevel visibility) { this.visibility = visibility; }
    public void setLocation(String location) { this.location = location; }
    
    @Override
    public String toString() {
        return String.format("Post{id='%s', author='%s', content='%s', likes=%d, comments=%d, type=%s}",
                postId, authorId, content.substring(0, Math.min(50, content.length())), 
                likes.size(), comments.size(), type);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Post post = (Post) obj;
        return postId.equals(post.postId);
    }
    
    @Override
    public int hashCode() {
        return postId.hashCode();
    }
}

enum PostType {
    TEXT,
    IMAGE,
    VIDEO,
    LINK,
    POLL,
    STORY
}

enum PostStatus {
    ACTIVE,
    HIDDEN,
    REMOVED,
    FLAGGED
}

/**
 * Represents a comment on a post
 */
class Comment {
    private String commentId;
    private String authorId;
    private String content;
    private String postId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Set<String> likes;
    private Map<String, Comment> replies;
    private String parentCommentId;
    
    public Comment(String commentId, String authorId, String content, String postId) {
        this.commentId = commentId;
        this.authorId = authorId;
        this.content = content;
        this.postId = postId;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.likes = ConcurrentHashMap.newKeySet();
        this.replies = new ConcurrentHashMap<>();
    }
    
    public Comment(String commentId, String authorId, String content, String postId, String parentCommentId) {
        this(commentId, authorId, content, postId);
        this.parentCommentId = parentCommentId;
    }
    
    public boolean likeComment(String userId) {
        if (!userId.equals(authorId)) {
            return likes.add(userId);
        }
        return false;
    }
    
    public boolean unlikeComment(String userId) {
        return likes.remove(userId);
    }
    
    public String addReply(String userId, String replyContent) {
        String replyId = UUID.randomUUID().toString().substring(0, 8);
        Comment reply = new Comment(replyId, userId, replyContent, this.postId, this.commentId);
        replies.put(replyId, reply);
        return replyId;
    }
    
    public void updateContent(String newContent) {
        this.content = newContent;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getCommentId() { return commentId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public String getPostId() { return postId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<String> getLikes() { return new HashSet<>(likes); }
    public Map<String, Comment> getReplies() { return new HashMap<>(replies); }
    public String getParentCommentId() { return parentCommentId; }
    
    public int getLikesCount() { return likes.size(); }
    public int getRepliesCount() { return replies.size(); }
    
    public boolean isLikedBy(String userId) {
        return likes.contains(userId);
    }
    
    @Override
    public String toString() {
        return String.format("Comment{id='%s', author='%s', content='%s', likes=%d, replies=%d}",
                commentId, authorId, content.substring(0, Math.min(30, content.length())), 
                likes.size(), replies.size());
    }
} 