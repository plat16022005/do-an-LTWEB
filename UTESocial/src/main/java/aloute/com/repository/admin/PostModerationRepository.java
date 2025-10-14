package aloute.com.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloute.com.entity.PostModeration;


@Repository
public interface PostModerationRepository extends JpaRepository<PostModeration, Integer> {
}
