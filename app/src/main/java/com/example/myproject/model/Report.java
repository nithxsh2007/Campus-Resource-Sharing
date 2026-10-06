package com.example.myproject.model;

public class Report {
    private String reportId;
    private String resourceId;
    private String reportedBy;
    private String reason;
    private String description;
    private long createdAt;

    public Report() {
        // Default constructor for Firestore
    }

    public Report(String reportId, String resourceId, String reportedBy, String reason, String description) {
        this.reportId = reportId;
        this.resourceId = resourceId;
        this.reportedBy = reportedBy;
        this.reason = reason;
        this.description = description;
        this.createdAt = System.currentTimeMillis();
    }

    public String getReportId() {
        return reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getReportedBy() {
        return reportedBy;
    }

    public void setReportedBy(String reportedBy) {
        this.reportedBy = reportedBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
