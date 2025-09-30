package com.emailassistant.backend.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.emailassistant.backend.entity.Email;
import com.emailassistant.backend.repository.EmailRepository;

@Component
public class DataLoader implements CommandLineRunner {
    
    @Autowired
    private EmailRepository emailRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Only load data if database is empty
        if (emailRepository.count() > 0) {
            System.out.println("📊 Database already contains " + emailRepository.count() + " emails");
            return;
        }
        
        System.out.println("🔄 Loading sample email data...");
        
        // Critical Priority Emails
        emailRepository.save(new Email(
            "ceo@techcorp.com",
            "CRITICAL: Complete system outage affecting all customers",
            "Our entire production system is down. Customers cannot access their accounts and we're losing revenue by the minute. This needs immediate escalation to your highest priority team. Expected resolution time?",
            LocalDateTime.now().minusMinutes(15),
            "negative",
            "Critical",
            "Technical Support"
        ));
        
        emailRepository.save(new Email(
            "finance@bigclient.com",
            "URGENT: Payment processing failure - $50K transaction stuck",
            "We have a critical payment processing issue. A $50,000 transaction has been stuck for 3 hours. Our accounting team is waiting and this is blocking our month-end closure. Please expedite.",
            LocalDateTime.now().minusHours(1),
            "negative",
            "Critical",
            "Billing"
        ));
        
        // Urgent Priority Emails
        emailRepository.save(new Email(
            "support@retailchain.com",
            "Urgent: Cannot access admin dashboard before Black Friday",
            "With Black Friday approaching, our team cannot access the admin dashboard to set up promotions. We've tried multiple browsers and cleared cache. This is blocking our entire marketing campaign.",
            LocalDateTime.now().minusHours(2),
            "negative",
            "Urgent",
            "Account Access"
        ));
        
        emailRepository.save(new Email(
            "manager@startup.io",
            "Immediate help needed: API integration failing",
            "Our integration with your API started failing this morning. Error code 500 on all requests. Our app is broken for users. Need immediate assistance as this affects our launch demo today.",
            LocalDateTime.now().minusHours(3),
            "negative",
            "Urgent",
            "Technical Support"
        ));
        
        emailRepository.save(new Email(
            "billing@healthcare.org",
            "Urgent: Incorrect charges on our enterprise account",
            "We noticed incorrect charges of $2,400 on our last invoice. This appears to be a duplication error. Please investigate immediately as this affects our budget approval process.",
            LocalDateTime.now().minusHours(4),
            "negative",
            "Urgent",
            "Billing"
        ));
        
        // Normal Priority Emails
        emailRepository.save(new Email(
            "developer@webapp.com",
            "Question about API rate limits for our integration",
            "Hi there! We're building an integration with your service and want to understand the rate limits. What's the maximum requests per minute we can make? Also, do you offer higher limits for enterprise accounts?",
            LocalDateTime.now().minusHours(6),
            "neutral",
            "Normal",
            "Technical Support"
        ));
        
        emailRepository.save(new Email(
            "procurement@company.ltd",
            "Request for detailed pricing information",
            "Hello, we're evaluating your solution for our team of 150 users. Could you provide detailed pricing for your enterprise plan? We're also interested in any volume discounts available.",
            LocalDateTime.now().minusHours(8),
            "positive",
            "Normal",
            "Pricing"
        ));
        
        emailRepository.save(new Email(
            "admin@nonprofit.org",
            "Help with account verification for our organization",
            "We're a registered non-profit and would like to verify our account to access the educational discounts you offer. What documentation do we need to provide for verification?",
            LocalDateTime.now().minusHours(12),
            "positive",
            "Normal",
            "Account Access"
        ));
        
        emailRepository.save(new Email(
            "customer@smallbiz.com",
            "General inquiry about refund policy",
            "Hi, I'm considering your service but want to understand your refund policy first. If we're not satisfied within the first month, how does the refund process work?",
            LocalDateTime.now().minusHours(18),
            "neutral",
            "Normal",
            "Billing"
        ));
        
        emailRepository.save(new Email(
            "team@agency.co",
            "Positive feedback and feature request",
            "Love your platform! It's been a game-changer for our workflow. One small request: could you add dark mode support? Many of our team members work late and would appreciate it. Keep up the great work!",
            LocalDateTime.now().minusHours(24),
            "positive",
            "Normal",
            "General Support"
        ));
        
        System.out.println("✅ Sample email data loaded successfully!");
        System.out.println("📊 Total emails in database: " + emailRepository.count());
    }
}
