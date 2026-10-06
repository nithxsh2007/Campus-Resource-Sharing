package com.example.myproject.model;

public class BorrowRequest {
    private String requestId;
    private String resourceId;
    private String resourceName;
    private String ownerId;
    private String borrowerId;
    private String borrowerName;
    private String requestDate;
    private String returnDate;
    private String status; // "pending", "accepted", "rejected", "returned"
    private String note;
    private long timestamp;

    public BorrowRequest() {
        // Default constructor for Firestore
    }

    public BorrowRequest(String requestId, String resourceId, String resourceName, String ownerId,
                         String borrowerId, String borrowerName, String requestDate,
                         String returnDate, String status, String note) {
        this.requestId = requestId;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.ownerId = ownerId;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.requestDate = requestDate;
        this.returnDate = returnDate;
        this.status = status;
        this.note = note;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public void setBorrowerId(String borrowerId) {
        this.borrowerId = borrowerId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public void setBorrowerName(String borrowerName) {
        this.borrowerName = borrowerName;
    }

    public String getRequestDate() {
        return requestDate;
    }

    public void setRequestDate(String requestDate) {
        this.requestDate = requestDate;
    }

    public String getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(String returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
