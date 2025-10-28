package aloute.com.repository.common;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import aloute.com.entity.PostLike;
import aloute.com.entity.Posts;
import aloute.com.entity.User;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Integer> {
    Optional<PostLike> findByPostAndUser(Posts post, User user);
    long countByPost(Posts post);
    void deleteByPostAndUser(Posts post, User user);
    @Query("SELECT pl FROM PostLike pl JOIN FETCH pl.user WHERE pl.post.postId = :postId")
    List<PostLike> findByPostIdWithUser(@Param("postId") Integer postId);
}
