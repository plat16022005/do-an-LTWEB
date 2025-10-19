package aloute.com.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.FriendService;
<<<<<<< Updated upstream
=======
import aloute.com.service.PostService;
import aloute.com.service.BlockService;
>>>>>>> Stashed changes
import jakarta.servlet.http.HttpSession;

@Controller
public class UserProfileController {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostsRepository postRepository;
	@Autowired
	private FriendService friendService;
	@Autowired
	private aloute.com.service.PostLikeService postLikeService;
<<<<<<< Updated upstream
=======
	@Autowired
	private aloute.com.service.PostRepostService postRepostService;
	@Autowired
	private BlockService blockService;
>>>>>>> Stashed changes

	@GetMapping("/api/posts/{postId}/like")
	public @ResponseBody String toggleLike(@PathVariable Integer postId, HttpSession session) {
		User user = (User) session.getAttribute("user");
		if (user == null) {
			return "{\"error\": \"Unauthorized\"}";
		}
		boolean isLiked = postLikeService.toggleLike(postId, user);
		int likesCount = postLikeService.getLikesCount(postId);
		return String.format("{\"liked\": %b, \"count\": %d}", isLiked, likesCount);
	}
	@GetMapping("/{nameUser}")
	public String showProfileForm(@PathVariable String nameUser, Model model, HttpSession session)
	{
		User user = (User) session.getAttribute("user");
		User information = userRepository.findByNameUser(nameUser);
		if (information == null) {
			return "redirect:/access-deniel";
		}
		List<Posts> posts = postRepository.findPostsOfUser(information.getUserId());
		List<User> friends = friendService.getFriendList(information.getUserId());
        
		if (user == null)
		{
			return "redirect:/access-deniel";
		}
        
		boolean isOwner = user != null && user.getNameUser().equals(information.getNameUser());
		boolean isFriend = friendService.checkFriend(user.getUserId(), information.getUserId());
		boolean isBeRequest = friendService.checkBeRequest(information.getUserId(), user.getUserId());
		boolean isPending = friendService.checkPending(user.getUserId(), information.getUserId());
		
		// Kiểm tra trạng thái chặn
		boolean isBlocked = blockService.isBlocked(user.getUserId(), information.getUserId()); // Tôi chặn người này
		boolean isBlockedByOther = blockService.isBlocked(information.getUserId(), user.getUserId()); // Người này chặn tôi
		boolean hasBlockRelation = isBlocked || isBlockedByOther;
		
		model.addAttribute("isBeRequest", isBeRequest);
		model.addAttribute("isPending", isPending);
		model.addAttribute("isFriend", isFriend);
		model.addAttribute("isBlocked", isBlocked);
		model.addAttribute("isBlockedByOther", isBlockedByOther);
		model.addAttribute("hasBlockRelation", hasBlockRelation);
		model.addAttribute("info", information);
		model.addAttribute("isOwner", isOwner);
		model.addAttribute("posts", posts);
		model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, posts));
		model.addAttribute("friends", friends);
		return "user/profile";
	}
}
