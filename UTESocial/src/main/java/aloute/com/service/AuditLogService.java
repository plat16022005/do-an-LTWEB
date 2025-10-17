package aloute.com.service;

import aloute.com.entity.AuditLogs;
import aloute.com.entity.User;
import aloute.com.repository.AuditLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuditLogService 
{

    @Autowired
    private AuditLogRepository auditLogRepository;

    public void logAction(User user, String action, String details) 
    {
        try {
            AuditLogs log = new AuditLogs();
            log.setUser(user); // user có thể là null nếu hành động từ hệ thống
            log.setAction(action);
            log.setDetails(details);
            // createdAt được tự động set bởi @PrePersist
            auditLogRepository.save(log);
        } 
        catch (Exception e) 
        {
            
            System.err.println("Error saving audit log: " + e.getMessage());
        }
    }

    // Ghi log mà không cần user 
    public void logAction(String action, String details) 
    {
        logAction(null, action, details);
    }
}