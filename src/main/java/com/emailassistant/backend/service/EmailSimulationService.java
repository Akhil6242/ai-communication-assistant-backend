package com.emailassistant.backend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.emailassistant.backend.entity.Email;
import com.emailassistant.backend.repository.EmailRepository;

@Service
public class EmailSimulationService {
    
    @Autowired
    private EmailRepository emailRepository;
    
    @Autowired
    private EmailAIService aiService;
    
    // Realistic email templates for different scenarios
    private final List<EmailTemplate> emailTemplates = Arrays.asList(
        new EmailTemplate(
            "ops@ecommerce.com", 
            "CRITICAL: Payment gateway returning 500 errors", 
            "Our payment system is completely down. Customers can't complete purchases during our biggest sales day. We're losing approximately $2,000 per minute. Error logs show 500 internal server errors on all payment requests. Need immediate escalation to your engineering team. This is affecting thousands of customers.",
            "Critical", "negative", "Technical Support"
        ),
        new EmailTemplate(
            "support@retailgiant.com", 
            "URGENT: Black Friday promotion system crashed", 
            "The promotional pricing system crashed 30 minutes ago right as our Black Friday sale launched. All discount codes are showing as invalid. Customer service is overwhelmed with complaints. This is a PR disaster waiting to happen. Please help immediately!",
            "Urgent", "negative", "Technical Support"
        ),
        new EmailTemplate(
            "billing@healthcare.net", 
            "Help: Duplicate charges on enterprise account", 
            "We noticed our enterprise account was charged twice this month - $4,500 instead of the usual $2,250. This appears to be a billing system error. Can you please investigate and process a refund for the duplicate charge? We need this resolved before month-end reconciliation.",
            "Urgent", "negative", "Billing"
        ),
        new EmailTemplate(
            "admin@startup.io", 
            "Support request: API authentication suddenly failing", 
            "Our API integration worked perfectly until this morning. Now all requests return 401 Unauthorized errors even with valid tokens. We haven't changed anything on our end. This is blocking our product demo to investors scheduled for this afternoon. Please advise urgently.",
            "Urgent", "negative", "Technical Support"
        ),
        new EmailTemplate(
            "team@agency.co", 
            "Query: Upgrading to enterprise plan for 50 users", 
            "Hi there! We're currently on the Pro plan with 20 users but need to expand to 50 users next month. Could you provide pricing details for the enterprise plan? Also, what's included in terms of priority support and additional features? We're looking to make the switch by the 15th.",
            "Normal", "positive", "Pricing"
        ),
        new EmailTemplate(
            "developer@webapp.com", 
            "Request: API rate limit increase for integration", 
            "We're building a customer portal integration and hitting your current rate limits during peak usage. Could we discuss increasing our limits from 1000 to 5000 requests per hour? We're processing real-time data for about 200 concurrent users. What options are available?",
            "Normal", "neutral", "Technical Support"
        ),
        new EmailTemplate(
            "contact@nonprofit.org", 
            "Help with account verification for educational discount", 
            "We're a registered 501(c)(3) nonprofit organization and would like to apply for your educational pricing. We work with underprivileged students and could really benefit from the reduced rates. What documentation do you need for verification? Thank you for supporting educational causes!",
            "Normal", "positive", "Account Access"
        ),
        new EmailTemplate(
            "customer@smallbiz.com", 
            "Question about data export and account cancellation", 
            "We're consolidating our tools and may need to cancel our account next month. Before we do, how can we export all our data? Is there a specific format it comes in? Also, what's the cancellation process and notice period required? Thanks for your help.",
            "Normal", "neutral", "General Support"
        )
    );
    
    public List<Email> simulateIncomingEmails() {
        List<Email> newEmails = new ArrayList<>();
        Random random = new Random();
        
        // Generate 2-4 random emails
        int emailCount = 2 + random.nextInt(3);
        Collections.shuffle(emailTemplates);
        
        for (int i = 0; i < emailCount && i < emailTemplates.size(); i++) {
            EmailTemplate template = emailTemplates.get(i);
            
            // Check if this email already exists (avoid exact duplicates)
            if (!emailExists(template.sender, template.subject)) {
                Email email = new Email();
                email.setSender(template.sender);
                email.setSubject(template.subject);
                email.setBody(template.body);
                email.setReceivedAt(LocalDateTime.now().minusMinutes(random.nextInt(60)));
                email.setStatus("Pending");
                
                // Use AI for analysis (this makes it real AI processing)
                try {
                    Map<String, Object> analysis = aiService.analyzeEmail(template.subject, template.body);
                    email.setSentiment((String) analysis.get("sentiment"));
                    email.setPriority((String) analysis.get("priority"));
                    email.setCategory((String) analysis.get("category"));
                } catch (Exception e) {
                    // Fallback to template values if AI service is unavailable
                    email.setSentiment(template.sentiment);
                    email.setPriority(template.priority);
                    email.setCategory(template.category);
                }
                
                // Generate AI response
                try {
                    String aiResponse = aiService.generateResponse(
                        email.getBody(),
                        email.getSentiment(),
                        email.getCategory()
                    );
                    email.setAiResponse(aiResponse);
                } catch (Exception e) {
                    email.setAiResponse("Thank you for contacting us. Our team will review your request and respond promptly.");
                }
                
                emailRepository.save(email);
                newEmails.add(email);
                
                System.out.println("📧 Simulated new email: " + template.subject + " [" + email.getPriority() + "/" + email.getSentiment() + "]");
            }
        }
        
        return newEmails;
    }
    
    private boolean emailExists(String sender, String subject) {
        return emailRepository.findAll().stream()
            .anyMatch(email -> 
                email.getSender().equals(sender) && 
                email.getSubject().equals(subject)
            );
    }
    
    // Helper class for email templates
    private static class EmailTemplate {
        String sender, subject, body, priority, sentiment, category;
        
        EmailTemplate(String sender, String subject, String body, String priority, String sentiment, String category) {
            this.sender = sender;
            this.subject = subject;
            this.body = body;
            this.priority = priority;
            this.sentiment = sentiment;
            this.category = category;
        }
    }
}
