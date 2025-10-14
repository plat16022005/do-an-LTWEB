package aloute.com.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import aloute.com.entity.Attachments;
import aloute.com.entity.Posts;
import aloute.com.repository.AttachmentRepository;
import aloute.com.repository.common.PostsRepository;

public class createPostWithAttachments {
	@Autowired
	private PostsRepository postRepository;

	@Autowired
	private AttachmentRepository attachmentRepository;

	public void createPostWithAttachments(Posts post, List<String> fileUrls) {
	    postRepository.save(post);

	    for (String url : fileUrls) {
	        Attachments attach = new Attachments();
	        attach.setPost(post);
	        attach.setFileUrl(url);
	        attach.setFileType("image/jpeg");
	        attachmentRepository.save(attach);
	    }
	}

}
