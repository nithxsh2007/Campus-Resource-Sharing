package com.example.myproject.model;

public class SavedResource {
    private String savedId;
    private String userId;
    private String resourceId;
    private long savedAt;

    public SavedResource() {
        // Default constructor for Firestore
    }

    public SavedResource(String savedId, String userId, String resourceId) {
        this.savedId = savedId;
        this.userId = userId;
        this.resourceId = resourceId;
        this.savedAt = System.currentTimeMillis();
    }

    public String getSavedId() {
        return savedId;
    }

    public void setSavedId(String savedId) {
        this.savedId = savedId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public long getSavedAt() {
        return savedAt;
    }

    public void setSavedAt(long savedAt) {
        this.savedAt = savedAt;
    }
}
