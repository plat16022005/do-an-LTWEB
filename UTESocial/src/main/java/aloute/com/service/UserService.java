package aloute.com.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.util.PasswordUtil;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;
	@Autowired
	 private AuditLogService auditLogService;
	
    public User login(String email, String password) {
		// Allow login with either email or username (templates say 'Email hoặc tên người dùng')
		User user = userRepository.findByEmail(email);
		if (user == null) {
			// try by nameUser; accept values like '@name' or 'name'
			String maybeName = email;
			if (!maybeName.startsWith("@")) {
				maybeName = "@" + maybeName;
			}
			user = userRepository.findByNameUser(maybeName);
		}

		if (user != null && PasswordUtil.hashPassword(password).equals(user.getPasswordHash())) {
			auditLogService.logAction(user, "LOGIN_SUCCESS", "User logged in successfully."); // Ghi log khi đăng nhập thành công
			return user;
		}
		else {
		         auditLogService.logAction("LOGIN_FAILURE", "Failed login attempt for email/username: " + email);
		     }
		return null;
    }
    
    public boolean register(String hoten, String queQuan, String email, String passWord, LocalDate birthday, String gender)
    {
    	User tonTai = userRepository.findByEmail(email);
    	if (tonTai != null)
    	{
    		return false;
    	}
    	User user = new User();
    	user.setFullName(hoten);
    	user.setLocation(queQuan);
    	user.setEmail(email);
    	user.setPasswordHash(PasswordUtil.hashPassword(passWord));
    	user.setBirthday(birthday);
    	user.setGender(gender);
    	user.setRole("user"); 
    	user.setCreatedAt(LocalDateTime.now());
    	userRepository.save(user);
    	return true;
    }
    public void resetPass(String email, String password)
    {
    	User user = userRepository.findByEmail(email);
    	user.setPasswordHash(PasswordUtil.hashPassword(password));
    	userRepository.save(user);
    }
}
