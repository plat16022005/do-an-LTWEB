package aloute.com.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.EmailService;
import aloute.com.service.PostService;
import aloute.com.service.UserService;
import jakarta.servlet.http.HttpSession;

@Controller
@ControllerAdvice
public class UserController 
{
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostsRepository postRepository;
    @Autowired
    private PostService postService;
    @GetMapping("/")
    public String showIndex(Model model, HttpSession session) {
    	User user = (User) session.getAttribute("user");
    	if (user != null)
    	{
    		return "redirect:/home";
    	}
        List<Posts> posts = postRepository.findAllWithUserAndAttachments();
        model.addAttribute("posts", posts);
        return "index";
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
//        	redirectAttributes.addFlashAttribute("user", user);
        	if (user.getRole().equals("Admin"))
        		return "redirect:/admin/dashboard";
        	else
        	{
        		if (user.getIsLocked() == false)
        		{
	        		if (user.getNameUser() == null || user.getNameUser().equals(""))
	        			return "redirect:/get-name";
	        		
	        		return "redirect:/home";
        		}
        		else
        		{
        			redirectAttributes.addFlashAttribute("error", "Tài khoản bạn đã bị khóa!");
        			return "redirect:/login";
        		}
        	}
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

    public String handleRegister(@RequestParam String fullName,
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
        
        redirectAttributes.addFlashAttribute("oldFullName", fullName);
        redirectAttributes.addFlashAttribute("oldLocation", location);
        redirectAttributes.addFlashAttribute("oldEmail", email);
        redirectAttributes.addFlashAttribute("oldBirthday", birthday);
        redirectAttributes.addFlashAttribute("oldGender", gender);

        // 1. Kiểm tra OTP
        if (OTP == null || !OTP.equals(verificationCode)) {
//            return ResponseEntity.status(400).body("Mã xác nhận không đúng!");
            redirectAttributes.addFlashAttribute("errorOTP", "Mã xác nhận không đúng!");
            return "redirect:/register";
        }

        // 2. Kiểm tra mật khẩu
        if (!password.equals(confirmPassword)) {
//            return ResponseEntity.status(401).body("Mật khẩu nhập lại không khớp!");
            redirectAttributes.addFlashAttribute("errorPass", "Hãy nhập lại mật khẩu đúng với mật khẩu đã nhập!");
            return "redirect:/register";
        }

        // 3. Đăng ký tài khoản
        boolean created = userService.register(fullName, location, email, password, birthday, gender);
        if (created) {
        	session.invalidate();
        	redirectAttributes.addFlashAttribute("success", "Tạo tài khoản thành công! Vui lòng đăng nhập!");
        	return "redirect:/login";
        } else {
        	redirectAttributes.addFlashAttribute("errorEmail", "Email đã có người sử dụng");
        	return "redirect:/register";
        }
    }
    
    @GetMapping("/forgot-pass")
    public String showForgotPassForm()
    {
    	return "forgot_password";
    }
    @PostMapping("/do-forgot-pass")
    public String handleForgotPass(@RequestParam String email,
    								RedirectAttributes redirectAttributes,
    								HttpSession session)
    {
    	if (userRepository.findByEmail(email) == null)
    	{
    		redirectAttributes.addFlashAttribute("errorEmail", "Email này chưa có người dùng!");
    		return "redirect:/forgot-pass";
    	}
        String otp = emailService.generateOTP(6);
        emailService.sendEmail(email, "Mã xác nhận AloUTE", "Mã OTP của bạn là: " + otp);
        session.setAttribute("otp", otp);
        session.setAttribute("email", email);
        
        return "redirect:/reset-pass";
    }
    @GetMapping("/reset-pass")
    public String showConfirmOTPForm()
    {
    	return "confirm_otp";
    }
    @PostMapping("do-reset-pass")
    public String handleResetPass(@RequestParam String otp,
    							@RequestParam String password,
    							@RequestParam String confirmPassword,
								RedirectAttributes redirectAttributes,
								HttpSession session)
    {
    	String email = (String) session.getAttribute("email");
    	String OTP = (String) session.getAttribute("otp");
    	if (!otp.equals(OTP))
    	{
    		redirectAttributes.addFlashAttribute("errorOTP", "Mã OTP không đúng! Vui lòng nhập lại!");
    		return "redirect:/reset-pass";
    	}
    	if (!password.equals(confirmPassword))
    	{
    		redirectAttributes.addFlashAttribute("errorPass", "Mật khẩu không khớp! Vui lòng nhập lại!");
    		return "redirect:/reset-pass";
    	}
    	redirectAttributes.addFlashAttribute("success", "Đổi mật khẩu thành công!");
    	userService.resetPass(email, password);
    	session.invalidate();
    	return "redirect:/login";
    }
    @GetMapping("/access-deniel")
    public String showDenielForm()
    {
    	return "access-deniel";
    }
    @GetMapping("/get-name")
    public String showGetNameForm(HttpSession session)
    {
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
    	return "get_name";
    }
    @PostMapping("/do-get-name")
    public String handleGetName(@RequestParam String nameUser,
    							RedirectAttributes redirectAttributes,
    							HttpSession session)
    {
    	User user = (User) session.getAttribute("user");
    	User xacnhan = userRepository.findByNameUser("@"+nameUser);
    	if (xacnhan != null)
    	{
    		redirectAttributes.addFlashAttribute("errorGetName", "Tên người dùng đã có người sử dụng");
    		return "redirect:/get-name";
    	}
    	user.setNameUser("@" + nameUser);
    	userRepository.save(user);
    	return "redirect:/home";
    }
    @GetMapping("/logout")
    public String handleLogout(HttpSession session,
    						RedirectAttributes redirectAttributes)
    {
    	session.invalidate();
    	return "redirect:/login";
    }
    @ModelAttribute("user")
    public User addUserToModel(HttpSession session) {
        return (User) session.getAttribute("user");
    }
    @GetMapping("/profile")
    public String showProfileForm(HttpSession session)
    {
    	User user = (User) session.getAttribute("user");
    	return "redirect:/" + user.getNameUser();
    }
    @GetMapping("/testform")
    public String showTestForm(HttpSession session, Model model) {
        // Lấy user từ session
        User user = (User) session.getAttribute("user");

        // Truyền vào model
        model.addAttribute("user", user);

        return "user/group_detail";
    }

}
