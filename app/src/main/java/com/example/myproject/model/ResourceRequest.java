package com.example.myproject.model;

public class ResourceRequest {
    private String requestId;
    private String title;
    private String category;
    private String description;
    private String neededByDate;
    private String location;
    private String requesterId;
    private String requesterName;
    private String requesterPhone;
    private String status; // "open", "fulfilled", "closed"
    private long createdAt;

    public ResourceRequest() {
        // Default constructor for Firestore
    }

    public ResourceRequest(String requestId, String title, String category, String description,
                           String neededByDate, String location, String requesterId,
                           String requesterName, String requesterPhone, String status) {
        this.requestId = requestId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.neededByDate = neededByDate;
        this.location = location;
        this.requesterId = requesterId;
        this.requesterName = requesterName;
        this.requesterPhone = requesterPhone;
        this.status = status;
        this.createdAt = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getNeededByDate() {
        return neededByDate;
    }

    public void setNeededByDate(String neededByDate) {
        this.neededByDate = neededByDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(String requesterId) {
        this.requesterId = requesterId;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterPhone() {
        return requesterPhone;
    }

    public void setRequesterPhone(String requesterPhone) {
        this.requesterPhone = requesterPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
