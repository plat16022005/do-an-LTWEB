package aloute.com.controller;

import aloute.com.entity.Posts;
import aloute.com.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/explore")
public class ExploreRestController {

    @Autowired
    private PostService postService;

    // GET /api/explore/random
    @GetMapping("/random")
    public List<Posts> getRandomPosts() {
        return postService.getRandomPosts();
    }
}