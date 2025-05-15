package com.example.fakeinstagram;

public class Post {
    private String id;
    private String imageUrl;
    private String userId;
    private long timestamp;
    private String username;
    private String description;
    public Post() {

    }

    public Post(String id, String imageUrl, String userId, long timestamp) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public long getTimestamp() {
        return timestamp;
    }
    public Post(String id, String imageUrl, String userId, long timestamp, String username, String description) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
        this.username = username;
        this.description = description;
    }

    public String getUsername() {
        return username;
    }

    public String getDescription() {
        return description;
    }
}
