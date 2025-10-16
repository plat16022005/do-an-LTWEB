package aloute.com.repository.common;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloute.com.entity.PostLike;
import aloute.com.entity.Posts;
import aloute.com.entity.User;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostAndUser(Posts post, User user);
    long countByPost(Posts post);
    void deleteByPostAndUser(Posts post, User user);
}
