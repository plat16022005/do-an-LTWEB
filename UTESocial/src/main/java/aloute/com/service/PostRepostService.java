package aloute.com.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import aloute.com.entity.Shares;
import aloute.com.entity.Posts;
import aloute.com.entity.User;
import aloute.com.repository.SharesRepository;
import aloute.com.repository.common.PostsRepository;

@Service
public class PostRepostService {
	public int getRepostCount(Integer postId) {
	    return postsRepository.findById(postId)
	            .map(Posts::getShareCount)
	            .orElse(0);
	}

    @Autowired
    private SharesRepository sharesRepository;

    @Autowired
    private PostsRepository postsRepository;

    /**
     * 🌀 Toggle repost (đăng lại / hủy đăng lại)
     */
    @Transactional
    public boolean toggleRepost(Integer postId, User user) {
        Optional<Posts> maybePost = postsRepository.findById(postId);
        if (maybePost.isEmpty()) return false;
        Posts post = maybePost.get();

        Optional<Shares> existing = sharesRepository.findByPostAndUser(post, user);
        if (existing.isPresent()) {
            // ❌ Hủy đăng lại
            sharesRepository.deleteByPostAndUser(post, user);
            post.setShareCount(Math.max(0, post.getShareCount() - 1));
            postsRepository.save(post);
            return false; // hiện tại chưa repost
        } else {
            // ✅ Đăng lại
            Shares s = new Shares();
            s.setPost(post);
            s.setUser(user);
            sharesRepository.save(s);
            post.setShareCount(post.getShareCount() + 1);
            postsRepository.save(post);
            return true; // hiện tại đã repost
        }
    }

    /**
     * 📊 Lấy danh sách các ID bài viết mà user đã repost
     */
    public Set<Integer> getRepostedPostIdsByUser(User user, List<Posts> posts) {
        if (user == null) return new HashSet<>();
        try {
            return sharesRepository.findAll().stream()
                    .filter(sh -> sh.getUser() != null && sh.getUser().getUserId().equals(user.getUserId()))
                    .map(sh -> sh.getPost().getPostId())
                    .collect(Collectors.toSet());
        } catch (Exception ex) {
            // Defensive: tránh crash trang khi có lỗi DB
            System.err.println("⚠ Warning: cannot read Shares table: " + ex.getMessage());
            return new HashSet<>();
        }
    }
}
