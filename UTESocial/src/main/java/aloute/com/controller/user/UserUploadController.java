package aloute.com.controller.user;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import aloute.com.entity.Attachments;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.AttachmentRepository;
import aloute.com.repository.common.PostsRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class UserUploadController {
	@Autowired
	private PostsRepository postRepository;
	@Autowired
	private AttachmentRepository attachmentsRepository;	
	@GetMapping("/upload")
	public String showUploadForm(Model model, HttpSession session)
	{
    	User user = (User) session.getAttribute("user");
    	if (user == null)
    	{
    		return "redirect:/access-deniel";
    	}
		return "user/upload";
	}
	
	@PostMapping("/upload/post")
	public String handleUpload(@RequestParam("content") String content,
	                            @RequestParam(value = "files", required = false) List<MultipartFile> files,
	                            HttpSession session) throws IOException {
	    if ((content == null || content.isBlank()) && (files == null || files.isEmpty())) {
	        return "redirect:/upload";
	    }

	    User user = (User) session.getAttribute("user");

	    // ✅ Tạo bài viết
	    Posts post = new Posts();
	    post.setUser(user);
	    post.setContent(content);
	    post.setCreatedAt(LocalDateTime.now());
	    post.setLikesCount(0);
	    post.setShareCount(0);
	    post.setCommentsCount(0);
	    post.setVisibility(visibility);
	    post.setDeleted(false);
	    post.setStatus("approved");
	    postRepository.save(post);

	    // ✅ Xử lý file đính kèm
	    if (files != null && !files.isEmpty()) {
	        System.out.println("Vào");
	        for (MultipartFile file : files) {
	            if (!file.isEmpty()) {
	                String filename = file.getOriginalFilename().toLowerCase();
	                System.out.println("Tên file: " + filename);
	                String ext = filename.substring(filename.lastIndexOf("."));
	                String uniqueName = UUID.randomUUID().toString() + ext;

	                // 📁 Lấy đường dẫn gốc project
	                String projectRoot = System.getProperty("user.dir");

	                String folder;
	                String fileType;

	                if (ext.matches("\\.(png|jpg|jpeg|webp)$")) {
	                    folder = projectRoot + File.separator + "uploads" + File.separator + "posts" + File.separator + user.getNameUser();
	                    fileType = "image";
	                } else if (ext.matches("\\.(mp4|mov|avi|webm|wav)$")) {
	                    folder = projectRoot + File.separator + "uploads" + File.separator + "videos" + File.separator + user.getNameUser();
	                    fileType = "video";
	                } else {
	                    folder = projectRoot + File.separator + "uploads" + File.separator + "other" + File.separator + user.getNameUser();
	                    fileType = "other";
	                }

	                File dir = new File(folder);
	                if (!dir.exists()) dir.mkdirs();

	                File dest = new File(dir, uniqueName);
	                file.transferTo(dest);

	                // ✅ Đường dẫn để load ảnh/video trên web
	                String url = "/uploads/" + (fileType.equals("image") ? "posts" :
	                                fileType.equals("video") ? "videos" : "other") +
	                             "/" + user.getNameUser() + "/" + uniqueName;

	                Attachments attachments = new Attachments();
	                attachments.setPost(post);
	                attachments.setFileUrl(url);
	                attachments.setFileType(fileType);
	                attachments.setUploadedAt(LocalDateTime.now());

	                attachmentsRepository.save(attachments);
	            }
	        }
	    }

	    return "redirect:/home";
	}

}
