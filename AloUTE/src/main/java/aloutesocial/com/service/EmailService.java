package aloutesocial.com.service;

public interface EmailService {
	public void sendEmail(String toEmail, String subject, String body);
	public String generateOTP(int length);
	public String getEmail(String username);
}
