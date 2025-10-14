package aloute.com.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import aloute.com.entity.User;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserSearchController {
	@GetMapping("/search")
	public String showUploadForm(Model model, HttpSession session)
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
		return "user/search";
	}
}
