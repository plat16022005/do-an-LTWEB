package aloute.com.service;

import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminService 
{

    @Autowired
    private UserRepository userRepository;

    public List<User> getAllUsers() 
    {
        return userRepository.findAll();
    }
    
    public void lockUser(Integer userId, String reason) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setIsLocked(true);
            user.setLockedReason(reason);
            user.setLockedAt(LocalDateTime.now());
            userRepository.save(user);
        }
    }

    public void unlockUser(Integer userId) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) 
        {
            User user = userOptional.get();
            user.setIsLocked(false);
            user.setLockedReason(null);
            user.setLockedAt(null);
            userRepository.save(user);
        }
    }
    
    public void changeUserRole(Integer userId, String newRole) 
    {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) 
        {
            User user = userOptional.get();
            // Đảm bảo chỉ có thể thay đổi role hợp lệ
            if ("user".equals(newRole) || "manager".equals(newRole) || "admin".equals(newRole)) 
            {
                user.setRole(newRole);
                userRepository.save(user);
            }
        }
    }
}