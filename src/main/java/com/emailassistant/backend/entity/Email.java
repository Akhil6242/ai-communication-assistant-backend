package com.emailassistant.backend.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "emails")
public class Email {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(nullable = false, length = 500)
    private String sender;
    
    @Column(nullable = false, length = 1000)
    private String subject;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;
    
    @Column(length = 50)
    private String sentiment;
    
    @Column(length = 50)
    private String priority;
    
    @Column(length = 100)
    private String category;
    
    @Column(columnDefinition = "TEXT")
    private String aiResponse;
    
    @Column(length = 50)
    private String status = "Pending";
    
    // Constructors
    public Email() {}
    
    public Email(String sender, String subject, String body, LocalDateTime receivedAt, 
                String sentiment, String priority, String category) {
        this.sender = sender;
        this.subject = subject;
        this.body = body;
        this.receivedAt = receivedAt;
        this.sentiment = sentiment;
        this.priority = priority;
        this.category = category;
    }
    
    // All your existing getters and setters remain the same...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getSender() { return sender; }
    public void setSender(String sender) { this.sender = sender; }
    
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    
    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }
    
    public LocalDateTime getReceivedAt() { return receivedAt; }
    public void setReceivedAt(LocalDateTime receivedAt) { this.receivedAt = receivedAt; }
    
    public String getSentiment() { return sentiment; }
    public void setSentiment(String sentiment) { this.sentiment = sentiment; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getAiResponse() { return aiResponse; }
    public void setAiResponse(String aiResponse) { this.aiResponse = aiResponse; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
