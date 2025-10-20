package aloute.com.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.service.FriendService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserFriendController {
	@Autowired
	private FriendService friendService;
	@Autowired
	private UserRepository userRepository;
	@GetMapping("/friend")
	public String showFriendForm(Model model, HttpSession session)
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
    	List<User> friends = friendService.getFriendList(user.getUserId());
    	List<User> beRequestFriends = friendService.getListBeingRequested(user.getUserId());
    	List<User> requestFriends = friendService.getListRequested(user.getUserId());
    	
    	model.addAttribute("friends", friends);
    	model.addAttribute("beRequestFriends", beRequestFriends);
    	model.addAttribute("requestFriends", requestFriends);
		return "user/friend";
	}
	@PostMapping("/friend/unfriend")
	public String handleUnfriend(
	        @RequestParam("friendId") Integer friendId,
	        @RequestParam("username") String username,
	        HttpSession session)
	{
		User user1 = (User) session.getAttribute("user");
		friendService.unfriend(user1.getUserId(), friendId);
		return "redirect:/" + username;
	}
	@PostMapping("/friend/pending")
	public String handlePending(
	        @RequestParam("friendId") Integer friendId,
	        HttpSession session)
	{
		User user1 = (User) session.getAttribute("user");
		User user2 = userRepository.findById(friendId).orElseThrow();
		friendService.send(user1, user2);
		return "redirect:/" + user2.getNameUser();
	}
	@PostMapping("/friend/accept")
	public String handleAccept(
	        @RequestParam("friendId") Integer friendId,
	        HttpSession session)
	{
		User user1 = (User) session.getAttribute("user");
		User user2 = userRepository.findById(friendId).orElseThrow();
		friendService.accept(user1, user2);
		return "redirect:/" + user2.getNameUser();
	}
	@PostMapping("/friend/deny")
	public String handleDeny(
	        @RequestParam("friendId") Integer friendId,
	        HttpSession session)
	{
		User user1 = (User) session.getAttribute("user");
		User user2 = userRepository.findById(friendId).orElseThrow();
		friendService.deny(user1, user2);
		return "redirect:/" + user2.getNameUser();
	}
}
