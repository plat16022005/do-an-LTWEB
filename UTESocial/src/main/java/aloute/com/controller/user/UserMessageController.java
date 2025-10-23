package aloute.com.controller.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import aloute.com.entity.Message;
import aloute.com.entity.User;
import aloute.com.service.FriendService;
import aloute.com.service.MessageService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserMessageController {
	@Autowired
	private FriendService friendService;
	@Autowired
	private MessageService messageService;

	@GetMapping("/message")
	public String showMessageForm(Model model, HttpSession session) {
	    User user = (User) session.getAttribute("user");
	    if (user == null) {
	        return "redirect:/access-deniel";
	    }

	    List<User> friends = friendService.getFriendList(user.getUserId());

	    // Map bạn bè + tin nhắn mới nhất
	    Map<Integer, String> lastMessages = new HashMap<>();
	    for (User friend : friends) {
	        String preview = messageService.getLatestMessagePreview(user.getUserId(), friend.getUserId());
	        lastMessages.put(friend.getUserId(), preview);
	    }

	    model.addAttribute("friends", friends);
	    model.addAttribute("lastMessages", lastMessages);

	    return "user/message";
	}
	@GetMapping("/message/load/{friendId}")
	@ResponseBody
	public List<Message> loadMessages(@PathVariable Integer friendId, HttpSession session) {
	    User currentUser = (User) session.getAttribute("user");
	    if (currentUser == null) {
	        return List.of();
	    }
	    return messageService.getAllMessagesBetween(currentUser.getUserId(), friendId);
	}

}
