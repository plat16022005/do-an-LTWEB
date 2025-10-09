package aloute.com.service.impl;

import aloute.com.service.*;
import java.util.Properties;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.stereotype.Service;

import java.util.Properties;
import java.util.Random;

@Service
public class EmailServiceImpl implements EmailService {
    @Override
    public void sendEmail(String toEmail, String subject, String body) {
        final String fromEmail = "mylv3555@gmail.com";      // email gửi
        final String password = "vclu pobx ivyh llhe";          // app password

        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(fromEmail, "AloUTE Support"));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setText(body);
            Transport.send(msg);
            System.out.println("✅ Gửi email thành công!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String generateOTP(int length) {
        String chars = "0123456789";
        Random rnd = new Random();
        StringBuilder otp = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            otp.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return otp.toString();
    }
}
