package aloute.com.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.entity.PostLike;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.common.PostLikeRepository;
import aloute.com.repository.common.PostsRepository;

@Service
public class PostLikeService {
    public int getLikesCount(Integer postId) {
        Optional<Posts> post = postsRepository.findById(postId);
        return post.map(Posts::getLikesCount).orElse(0);
    }

    @Autowired
    private PostLikeRepository postLikeRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Transactional
    public boolean toggleLike(Integer postId, User user) {
        Optional<Posts> maybePost = postsRepository.findById(postId);
        if (maybePost.isEmpty()) return false;
        Posts post = maybePost.get();

        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);
        if (existing.isPresent()) {
            // unlike
            postLikeRepository.deleteByPostAndUser(post, user);
            post.setLikesCount(Math.max(0, post.getLikesCount() - 1));
            postsRepository.save(post);
            return false; // now unliked
        } else {
            // like
            PostLike pl = new PostLike();
            pl.setPost(post);
            pl.setUser(user);
            postLikeRepository.save(pl);
            post.setLikesCount(post.getLikesCount() + 1);
            postsRepository.save(post);
            return true; // now liked
        }
    }

    public Set<Integer> getLikedPostIdsByUser(User user, List<Posts> posts) {
        if (user == null) return new HashSet<>();
        try {
            return postLikeRepository.findAll().stream()
                    .filter(pl -> pl.getUser() != null && pl.getUser().getUserId().equals(user.getUserId()))
                    .map(pl -> pl.getPost().getPostId())
                    .collect(Collectors.toSet());
        } catch (Exception ex) {
            // defensive: if DB schema mismatch or other issue occurs, don't break page rendering
            System.err.println("Warning: cannot read PostLikes table: " + ex.getMessage());
            return new HashSet<>();
        }
    }
}
