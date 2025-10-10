package aloute.com.controller.admin;

import aloute.com.entity.User;
import aloute.com.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController 
{

    @Autowired
    private AdminService adminService;
    
    
    @GetMapping
    public String adminIndex() 
    {
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/dashboard")
    public String showAdminDashboard() 
    {
        return "admin/dashboard"; 
    }

    
    
    @GetMapping("/users")
    public String manageUsers(Model model) 
    {
        List<User> users = adminService.getAllUsers();
        model.addAttribute("users", users);
        return "admin/users";
    }

    
    
    @PostMapping("/users/lock")
    public String lockUser(@RequestParam Integer userId, @RequestParam String reason) 
    {
        adminService.lockUser(userId, reason);
        return "redirect:/admin/users";
    }

    
    
    @PostMapping("/users/unlock")
    public String unlockUser(@RequestParam Integer userId) 
    {
        adminService.unlockUser(userId);
        return "redirect:/admin/users";
    }
    
    
    
    @PostMapping("/users/change-role")
    public String changeUserRole(@RequestParam Integer userId, @RequestParam String newRole) 
    {
        adminService.changeUserRole(userId, newRole);
        return "redirect:/admin/users";
    }
}