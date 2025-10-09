package aloute.com.service;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.util.PasswordUtil;

@Service
public class UserService {
	@Autowired
    private UserRepository userRepository;
	
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && PasswordUtil.hashPassword(password).equals(user.getPasswordHash())) {
            return user;
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
    	
        user.setRole("User"); // Mặc định là user khi mới đăng ký
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
