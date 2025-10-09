package aloute.com.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.User;
import aloute.com.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserController {
    @Autowired
    private UserService userService;
    @GetMapping("/")
    public String showIndexForm() {
        return "index"; // Tự động map tới /WEB-INF/jsp/index.jsp
    }
    @GetMapping("/login")
    public String showLoginForm()
    {
    	return "login";
    }
    
    @PostMapping("/do-login")
    public String handleLogin(@RequestParam String email,
				            @RequestParam String password,
							RedirectAttributes redirectAttributes,
							HttpSession session)
    {
        User user = userService.login(email, password);
        
        if (user != null) {
        	session.setAttribute("user", user);
        	if (user.getRole().equals("Admin"))
        		return "redirect:/";
        	else
        		return "redirect:/home";
        } else {
        	redirectAttributes.addFlashAttribute("error", "Sai tên đăng nhập hoặc mật khẩu");
            return "redirect:/login";
        }
    }
    
    @GetMapping("/register")
    public String showRegisterForm()
    {
    	return "register";
    }
    
    @PostMapping("/do-register")
    @ResponseBody
    public ResponseEntity<String> handleRegister(@RequestParam String fullName,
                                                 @RequestParam String location,
                                                 @RequestParam String email,
                                                 @RequestParam String verificationCode,
                                                 @RequestParam String password,
                                                 @RequestParam String confirmPassword,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate birthday,
                                                 @RequestParam String gender,
                                                 RedirectAttributes redirectAttributes,
                                                 HttpSession session) {

        String OTP = (String) session.getAttribute("otp");

        // 1. Kiểm tra OTP
        if (OTP == null || !OTP.equals(verificationCode)) {
            return ResponseEntity.status(400).body("Mã xác nhận không đúng!");
        }

        // 2. Kiểm tra mật khẩu
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.status(401).body("Mật khẩu nhập lại không khớp!");
        }

        // 3. Đăng ký tài khoản
        boolean created = userService.register(fullName, location, email, password, birthday, gender);
        if (created) {
        	session.invalidate();
        	redirectAttributes.addFlashAttribute("success", "Đã tạo tài khoản");
            return ResponseEntity.ok("Đăng ký thành công!");
        } else {
            return ResponseEntity.status(409).body("Email này đã được sử dụng!");
        }
    }
}
