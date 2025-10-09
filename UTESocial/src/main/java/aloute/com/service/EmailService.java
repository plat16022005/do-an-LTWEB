package aloute.com.service;

public interface EmailService {
    void sendEmail(String toEmail, String subject, String body);
    String generateOTP(int length);
}
