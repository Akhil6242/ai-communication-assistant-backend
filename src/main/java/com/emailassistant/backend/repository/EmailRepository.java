package com.emailassistant.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.emailassistant.backend.entity.Email;

@Repository
public interface EmailRepository extends JpaRepository<Email, Long> {
    
    // Find emails by priority (Urgent emails first)
    List<Email> findByPriorityOrderByReceivedAtDesc(String priority);
    
    // Find all emails ordered by priority and received time
    @Query("SELECT e FROM Email e ORDER BY " +
           "CASE e.priority WHEN 'Critical' THEN 1 WHEN 'Urgent' THEN 2 ELSE 3 END, " +
           "e.receivedAt DESC")
    List<Email> findAllOrderedByPriority();
    
    // Find emails by status
    List<Email> findByStatus(String status);
}
