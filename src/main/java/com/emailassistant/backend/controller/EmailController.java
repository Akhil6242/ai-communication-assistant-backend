package com.emailassistant.backend.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.emailassistant.backend.entity.Email;
import com.emailassistant.backend.repository.EmailRepository;
import com.emailassistant.backend.service.EmailAIService;
import com.emailassistant.backend.service.EmailSimulationService;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "http://localhost:3000")
public class EmailController {
    
    @Autowired
    private EmailRepository emailRepository;
    
    @Autowired
    private EmailAIService aiService;

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);


    @Autowired
    private EmailAIService emailAIService;

    @Autowired
    private EmailSimulationService emailSimulationService;
    
    // Get all emails (ordered by priority)
    @GetMapping
    public List<Email> getAllEmails() {
        return emailRepository.findAllOrderedByPriority();
    }
    
    // Get email by ID
    @GetMapping("/{id}")
    public ResponseEntity<Email> getEmailById(@PathVariable Long id) {
        Optional<Email> email = emailRepository.findById(id);
        if (email.isPresent()) {
            return ResponseEntity.ok(email.get());
        }
        return ResponseEntity.notFound().build();
    }
    
    // Create new email WITH AI ANALYSIS
    @PostMapping
    public Email createEmail(@RequestBody Email email) {
        // Set received time
        email.setReceivedAt(LocalDateTime.now());
        
        // Call AI service to analyze the email
        Map<String, Object> analysis = aiService.analyzeEmail(email.getSubject(), email.getBody());
        
        // Update email with AI analysis results
        email.setSentiment((String) analysis.get("sentiment"));
        email.setPriority((String) analysis.get("priority"));
        email.setCategory((String) analysis.get("category"));
        
        // Generate AI response
        String aiResponse = aiService.generateResponse(
            email.getBody(), 
            email.getSentiment(), 
            email.getCategory()
        );
        email.setAiResponse(aiResponse);
        
        System.out.println("🤖 AI Analysis completed for email: " + email.getSubject());
        System.out.println("   Sentiment: " + email.getSentiment());
        System.out.println("   Priority: " + email.getPriority());
        System.out.println("   Category: " + email.getCategory());
        
        return emailRepository.save(email);
    }
    
    // Manually trigger AI analysis for existing email
    @PostMapping("/{id}/analyze")
    public ResponseEntity<Email> analyzeEmail(@PathVariable Long id) {
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isPresent()) {
            Email email = optionalEmail.get();
            
            // Call AI service
            Map<String, Object> analysis = aiService.analyzeEmail(email.getSubject(), email.getBody());
            
            // Update email with analysis
            email.setSentiment((String) analysis.get("sentiment"));
            email.setPriority((String) analysis.get("priority"));
            email.setCategory((String) analysis.get("category"));
            
            // Generate new AI response
            String aiResponse = aiService.generateResponse(
                email.getBody(), 
                email.getSentiment(), 
                email.getCategory()
            );
            email.setAiResponse(aiResponse);
            
            return ResponseEntity.ok(emailRepository.save(email));
        }
        return ResponseEntity.notFound().build();
    }
    // Simulate fetching new emails (looks like real email fetching to users)
    @PostMapping("/fetch-emails")
    public ResponseEntity<Map<String, Object>> fetchEmails() {
        try {
            System.out.println("🔍 Simulating email fetch from external mail server...");
            
            List<Email> newEmails = generateRandomEmails(3);
            emailRepository.saveAll(newEmails);
            System.out.println("✅ Saved " + newEmails.size() + " new emails to database");

            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "Successfully fetched " + newEmails.size() + " new support emails");
            response.put("newEmailsCount", newEmails.size());
            response.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Error fetching emails: " + e.getMessage());
            
            return ResponseEntity.status(500).body(response);
        }
    }

    private List<Email> generateRandomEmails(int count) {
        List<Email> newEmails = new ArrayList<>();
        String[] subjects = {
            "Urgent: Payment processing issue",
            "Account verification needed",
            "Technical support request",
            "Billing inquiry - please help",
            "Password reset not working",
            "Feature request for mobile app",
            "Subscription cancellation request",
            "Login issues after update"
        };
        
        String[] senders = {
            "john.doe@customer.com",
            "sarah.smith@business.com", 
            "mike.johnson@company.com",
            "lisa.brown@startup.com",
            "alex.wilson@enterprise.com"
        };
        String[] bodies = {
            "I'm experiencing issues with my account and need immediate assistance.",
            "The payment processing seems to be stuck. Can you help resolve this quickly?",
            "I love your product but would like to suggest a new feature.",
            "Having trouble logging in after the recent update. Please help.",
            "Need to cancel my subscription due to budget constraints."
        };
        
        Random random = new Random();
        
        for (int i = 0; i < count; i++) {
            Email email = new Email();
            email.setSender(senders[random.nextInt(senders.length)]);
            email.setSubject(subjects[random.nextInt(subjects.length)]);
            email.setBody(bodies[random.nextInt(bodies.length)]);
            email.setReceivedAt(LocalDateTime.now().minusMinutes(random.nextInt(30)));
            email.setStatus("Pending");
            
            // Random sentiment and priority
            String[] sentiments = {"positive", "negative", "neutral"};
            String[] priorities = {"Normal", "Urgent", "Critical"};
            String[] categories = {"Account Access", "Billing", "Technical Support", "General Support"};
            
            email.setSentiment(sentiments[random.nextInt(sentiments.length)]);
            email.setPriority(priorities[random.nextInt(priorities.length)]);
            email.setCategory(categories[random.nextInt(categories.length)]);

            newEmails.add(email);
        }
    
        return newEmails;
    }

    // Get fetch statistics for dashboard
    @GetMapping("/fetch-stats")
    public ResponseEntity<Map<String, Object>> getFetchStats() {
        List<Email> allEmails = emailRepository.findAll();
        long totalEmails = allEmails.size();
        long todayEmails = allEmails.stream()
            .filter(email -> email.getReceivedAt().toLocalDate().equals(LocalDate.now()))
            .count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalEmails", totalEmails);
        stats.put("todayEmails", todayEmails);
        stats.put("pendingEmails", allEmails.stream().filter(e -> !"Resolved".equals(e.getStatus())).count());
        stats.put("lastFetchTime", LocalDateTime.now().toString());
        
        return ResponseEntity.ok(stats);
    }


    @PostMapping("/{id}/regenerate-response")
    public ResponseEntity<Email> regenerateAIResponse(@PathVariable Long id) {
        try {
            Optional<Email> emailOpt = emailRepository.findById(id);
            if (!emailOpt.isPresent()) {
                return ResponseEntity.notFound().build();
            }
            
            Email email = emailOpt.get();
            
            // Call AI service to generate new response
            String newResponse = emailAIService.generateResponse(
                email.getBody(), 
                email.getSentiment(), 
                email.getCategory()
            );
            
            // Update email with new AI response
            email.setAiResponse(newResponse);
            Email savedEmail = emailRepository.save(email);
            
            logger.info("🔄 Regenerated AI response for email ID: {}", id);
            return ResponseEntity.ok(savedEmail);
            
        } catch (Exception e) {
            logger.error("❌ Error regenerating AI response for email ID: " + id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    // Send reply (update AI response and status)
    @PostMapping("/{id}/reply")
    public ResponseEntity<Email> sendReply(@PathVariable Long id, @RequestBody ReplyRequest replyRequest) {
        Optional<Email> optionalEmail = emailRepository.findById(id);
        if (optionalEmail.isPresent()) {
            Email email = optionalEmail.get();
            email.setAiResponse(replyRequest.getReply());
            email.setStatus("Resolved");
            return ResponseEntity.ok(emailRepository.save(email));
        }
        return ResponseEntity.notFound().build();
    }
    
    // Inner class for reply request
    public static class ReplyRequest {
        private String reply;
        
        public String getReply() { return reply; }
        public void setReply(String reply) { this.reply = reply; }
    }
}
