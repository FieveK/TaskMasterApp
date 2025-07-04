package com.example.taskmaster.Model;

import java.util.List;

public class Task {
    private String id;
    private String title;
    private String description;
    private String deadline;
    private String location;
    private String photoUrl;
    private List<String> participants; // ✅ Pindah ke atas

    public Task() {}

    public Task(String id, String title, String description, String deadline, String location, String photoUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.location = location;
        this.photoUrl = photoUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDeadline() { return deadline; }
    public void setDeadline(String deadline) { this.deadline = deadline; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public List<String> getParticipants() { return participants; }
    public void setParticipants(List<String> participants) { this.participants = participants; }
}
