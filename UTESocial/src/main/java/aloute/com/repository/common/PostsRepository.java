package aloute.com.repository.common;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
    @Query("""
            SELECT DISTINCT p FROM Posts p
            LEFT JOIN FETCH p.user
            LEFT JOIN FETCH p.attachments
            ORDER BY p.createdAt DESC
        """)
        List<Posts> findAllWithUserAndAttachments();
}