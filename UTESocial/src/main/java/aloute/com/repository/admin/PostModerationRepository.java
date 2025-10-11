package aloute.com.repository.admin;

import aloute.com.entity.admin.PostModeration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface PostModerationRepository extends JpaRepository<PostModeration, Integer> {
}
