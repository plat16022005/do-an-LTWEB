package aloute.com.controller.user;

import java.util.List;

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
<<<<<<< Updated upstream
=======
import aloute.com.service.PostLikeService;
import aloute.com.service.PostRepostService;
import aloute.com.service.BlockService;
>>>>>>> Stashed changes
import jakarta.servlet.http.HttpSession;

@Controller
public class UserSearchController {
	@Autowired
	private SearchService searchService;
<<<<<<< Updated upstream
=======
	@Autowired
	private PostLikeService postLikeService;
	@Autowired
	private PostRepostService postRepostService;
	@Autowired
	private BlockService blockService;		
>>>>>>> Stashed changes
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
	        
	        // Lọc bỏ những user có mối quan hệ chặn
	        List<Integer> blockedIds = blockService.getBlockedUserIds(user.getUserId());
	        List<Integer> blockerIds = blockService.getBlockerUserIds(user.getUserId());
	        
	        resultUsers.removeIf(u -> 
	            blockedIds.contains(u.getUserId()) || // Tôi đã chặn
	            blockerIds.contains(u.getUserId())    // Người ta đã chặn tôi
	        );
	        
	        // Lọc bỏ bài viết của những user có mối quan hệ chặn
	        resultPosts.removeIf(p -> 
	            blockedIds.contains(p.getUser().getUserId()) || 
	            blockerIds.contains(p.getUser().getUserId())
	        );
	        
	        model.addAttribute("resultUsers", resultUsers);
	        model.addAttribute("resultPosts", resultPosts);
	        model.addAttribute("hasResults", true);
	    } else {
	        model.addAttribute("hasResults", false);
	    }

	    return "user/search";
	}

	@PostMapping("/search/result")
	@ResponseBody
	public String handleSearch(@RequestParam("keyword") String keyword, RedirectAttributes redirectAttributes) {
	    List<User> resultUsers = searchService.searchUser(keyword);
	    List<Posts> resultPosts = searchService.searchPost(keyword);

	    redirectAttributes.addFlashAttribute("resultUsers", resultUsers);
	    redirectAttributes.addFlashAttribute("resultPosts", resultPosts);
	    return "redirect:/search";
	}

}
