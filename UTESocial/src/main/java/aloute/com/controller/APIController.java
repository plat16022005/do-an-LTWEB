package aloute.com.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import aloute.com.service.EmailService;
import aloute.com.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/api")
@ResponseBody
public class APIController {
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    
    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestBody Map<String, String> body, HttpSession session) throws MessagingException 
    {
        String email = body.get("email");
        
        if (email == null || email.trim().isEmpty()) 
        {
            return ResponseEntity.badRequest().body("Email không được để trống");
        }
        
        if (userRepository.findByEmail(email) != null) 
        {
            return ResponseEntity.status(409).body("Email đã có người sử dụng");  // 409 Conflict
        }

        String otp = emailService.generateOTP(6);
        emailService.sendEmail(email, "Mã xác nhận AloUTE", "Mã OTP của bạn là: " + otp);
        session.setAttribute("otp", otp);

        return ResponseEntity.ok("Mã OTP đã được gửi tới " + email);
    }
}