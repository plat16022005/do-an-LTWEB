package aloute.com.service;

import aloute.com.entity.Posts;
import aloute.com.repository.common.PostsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PostService {

    @Autowired
    private PostsRepository postRepository;

    public List<Posts> getRandomPosts() {
        return postRepository.findAllWithUserAndAttachments();
    }
    
    public Posts getPostById(Integer postId) {
        return postRepository.findByIdWithAttachments(postId)
        	    .orElseThrow(() -> new RuntimeException("Post not found"));
    }

}
