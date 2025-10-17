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
import aloute.com.service.PostLikeService;
import aloute.com.service.PostRepostService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserSearchController {
	@Autowired
	private SearchService searchService;
	@Autowired
	private PostLikeService postLikeService;
	@Autowired
	private PostRepostService postRepostService;		
	@GetMapping("/search")
	public String showSearchForm(@RequestParam(required = false) String keyword,
	                             Model model,
	                             HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return "redirect:/access-deniel";
	    }

	    model.addAttribute("keyword", keyword);

	    if (keyword != null && !keyword.isBlank()) {
	        List<User> resultUsers = searchService.searchUser(keyword);
	        List<Posts> resultPosts = searchService.searchPost(keyword);
	        model.addAttribute("resultUsers", resultUsers);
	        model.addAttribute("resultPosts", resultPosts);
	        model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, resultPosts));
	        model.addAttribute("repostedPostIds", postRepostService.getRepostedPostIdsByUser(user, resultPosts));	
	        model.addAttribute("hasResults", true);
	    } else {
	        model.addAttribute("hasResults", false);
	    }

	    return "user/search";
	}

	@PostMapping("/search/result")
	@ResponseBody
	public String handleSearch(@RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes, Model model, HttpSession session) {
		User user = (User) session.getAttribute("user");
	    List<User> resultUsers = searchService.searchUser(keyword);
	    List<Posts> resultPosts = searchService.searchPost(keyword);

	    Map<String, Object> response = new HashMap<>();
	    redirectAttributes.addFlashAttribute("resultUsers", resultUsers);
	    redirectAttributes.addFlashAttribute("resultPosts", resultPosts);
		model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, resultPosts));
		model.addAttribute("repostedPostIds", postRepostService.getRepostedPostIdsByUser(user, resultPosts));	    
	    return "redirect:/search";
	}

}
