package aloute.com.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import aloute.com.entity.User;
import aloute.com.entity.Posts;
import aloute.com.service.SearchService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserSearchController {
	@Autowired
	private SearchService searchService;
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
	@PostMapping("/search/result")
	@ResponseBody
	public Map<String, Object> handleSearch(@RequestParam("keyword") String keyword) {
	    List<User> resultUsers = searchService.searchUser(keyword);
	    List<Posts> resultPosts = searchService.searchPost(keyword);

	    Map<String, Object> response = new HashMap<>();
	    response.put("users", resultUsers);
	    response.put("posts", resultPosts);
	    return response;
	}

}
