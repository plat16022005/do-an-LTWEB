package aloute.com.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.common.PostsRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserHomeController {
    @Autowired
    private PostsRepository postRepository;
	@Autowired
	private aloute.com.service.PostLikeService postLikeService;
	@Autowired
	private aloute.com.service.PostRepostService postRepostService;
	@GetMapping("/home")
	public String showHomeForm(Model model, HttpSession session)
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}

        List<Posts> posts = postRepository.findAllVisiblePosts(user.getUserId());

        List<Posts> friendPosts = postRepository.findFriendPosts(user.getUserId());
		model.addAttribute("posts", posts);
		model.addAttribute("friendPosts", friendPosts);
		model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, posts));
		model.addAttribute("repostedPostIds", postRepostService.getRepostedPostIdsByUser(user, posts));
		return "user/home";
	}
}
