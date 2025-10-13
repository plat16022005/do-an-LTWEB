package aloute.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import aloute.com.entity.Attachments;

public interface AttachmentRepository extends JpaRepository<Attachments, Integer> {
}
