package com.example.case_study_2.dto;

import java.time.LocalDateTime;

public class NotificationDto {
    private String id;
    private String title;
    private String description;
    private String link;
    private String timeAgo;
    private String type; // PRIMARY, SUCCESS, WARNING, DANGER
    private String icon;
    private LocalDateTime createdAt;

    public NotificationDto() {
        this.createdAt = LocalDateTime.now();
    }

    public NotificationDto(String id, String title, String description, String link, String timeAgo, String type, String icon) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.link = link;
        this.timeAgo = timeAgo;
        this.type = type;
        this.icon = icon;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(String timeAgo) {
        this.timeAgo = timeAgo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
