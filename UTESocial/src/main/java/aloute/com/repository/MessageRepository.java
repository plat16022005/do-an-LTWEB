package aloute.com.repository;

import aloute.com.entity.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MessageRepository extends JpaRepository<Message, Integer> {
	@Query("""
		    SELECT m FROM Message m
		    JOIN FETCH m.sender
		    JOIN FETCH m.receiver
		    WHERE (m.sender.userId = :userId1 AND m.receiver.userId = :userId2)
		       OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1)
		    ORDER BY m.createdAt DESC
		""")
		List<Message> findLatestMessageBetween(@Param("userId1") Integer userId1,
		                                       @Param("userId2") Integer userId2,
		                                       org.springframework.data.domain.Pageable pageable);
	@Query("""
		    SELECT DISTINCT m FROM Message m
		    LEFT JOIN FETCH m.sender
		    LEFT JOIN FETCH m.receiver
		    LEFT JOIN FETCH m.attachments
		    WHERE (m.sender.userId = :userId1 AND m.receiver.userId = :userId2)
		       OR (m.sender.userId = :userId2 AND m.receiver.userId = :userId1)
		    ORDER BY m.createdAt ASC
		""")
		List<Message> findAllMessagesWithAttachments(@Param("userId1") Integer userId1,
		                                             @Param("userId2") Integer userId2);

}
