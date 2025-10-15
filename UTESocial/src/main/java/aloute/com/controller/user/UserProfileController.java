package aloute.com.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.FriendService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserProfileController {
	@Autowired
	private UserRepository userRepository;
    @Autowired
    private PostsRepository postRepository;
    @Autowired
    private FriendService friendService;
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
    	model.addAttribute("isBeRequest", isBeRequest);
    	model.addAttribute("isPending", isPending);
    	model.addAttribute("isFriend", isFriend);
    	model.addAttribute("info", information);
    	model.addAttribute("isOwner", isOwner);
        model.addAttribute("posts", posts);
        model.addAttribute("friends", friends);
		return "user/profile";
	}
}
