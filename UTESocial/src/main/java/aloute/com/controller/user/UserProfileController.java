package aloute.com.controller.user;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
<<<<<<< Updated upstream
=======
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
>>>>>>> Stashed changes

import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.UserRepository;
import aloute.com.repository.common.PostsRepository;
import aloute.com.service.AttachmentService;
import aloute.com.service.FriendService;
import aloute.com.service.PostService;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserProfileController {
	@Autowired
	private UserRepository userRepository;
<<<<<<< Updated upstream
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
=======
	@Autowired
	private PostsRepository postRepository;
	@Autowired
	private PostService postService;
	@Autowired
	private SharesRepository sharesRepository;	
	@Autowired
	private FriendService friendService;
	@Autowired
	private AttachmentService attachmentService;	
	@Autowired
	private aloute.com.service.PostLikeService postLikeService;
	@Autowired
	private aloute.com.service.PostRepostService postRepostService;

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
		if (user == null)
		{
			return "redirect:/access-deniel";
		}
		List<Posts> posts = postRepository.findPostsOfUserWithVisibility(user.getUserId(),information.getUserId());
		List<Posts> reposts = sharesRepository.findRepostedPostsOfUser(information.getUserId());
		List<User> friends = friendService.getFriendList(information.getUserId());
        

        
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
		model.addAttribute("reposts", reposts);
		model.addAttribute("likedPostIds", postLikeService.getLikedPostIdsByUser(user, posts));
		model.addAttribute("repostedPostIds", postRepostService.getRepostedPostIdsByUser(user, reposts));
		model.addAttribute("friends", friends);
>>>>>>> Stashed changes
		return "user/profile";
	}
	@GetMapping("/posts/{id}/edit")
	public String editPost(@PathVariable("id") Integer id, Model model) {
		Posts post = postService.getPostById(id);
		model.addAttribute("post", post);
	    return "user/post_config";  // trả về template chỉnh sửa bài viết
	}
	@PostMapping("/posts/{id}/edit")
	public String updatePost(@PathVariable Integer id,
	                         @RequestParam("content") String content,
	                         @RequestParam("visibility") String visibility,
	                         @RequestParam(value = "deleteFileIds", required = false) List<Integer> deleteIds,
	                         @RequestParam(value = "newFiles", required = false) List<MultipartFile> newFiles,
	                         HttpSession session) {

	    Posts post = postRepository.findById(id)
	            .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết ID: " + id));

	    post.setContent(content);
	    post.setVisibility(visibility);
	    postRepository.save(post);

	    if (deleteIds != null) {
	        attachmentService.deleteByIds(deleteIds);
	    }

	    if (newFiles != null && !newFiles.isEmpty()) {
	        attachmentService.saveNewAttachments(id, newFiles, session);
	    }

	    return "redirect:/profile";
	}


}
