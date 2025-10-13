package aloute.com.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;

@Repository
public interface PostsRepository extends JpaRepository<Posts, Integer> {
}