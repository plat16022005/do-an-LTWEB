package aloute.com.repository;

import aloute.com.entity.Message;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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
	@Query("SELECT CASE WHEN m.sender.userId = :userId THEN m.receiver.userId ELSE m.sender.userId END as partnerId " +
	           "FROM Message m " +
	           "WHERE m.sender.userId = :userId OR m.receiver.userId = :userId " +
	           "GROUP BY partnerId " +
	           "ORDER BY MAX(m.createdAt) DESC")
	    List<Integer> findDistinctConversationPartnerIdsSortedByRecent(@Param("userId") Integer userId);
	@Modifying // Bắt buộc phải có cho @Query UPDATE/DELETE
	@Query("UPDATE Message m SET m.isRead = true " +
	       "WHERE m.sender.userId = :senderId " +
	       "AND m.receiver.userId = :receiverId " +
	       "AND m.isRead = false")
	void updateReadStatus(@Param("senderId") Integer senderId, 
	                      @Param("receiverId") Integer receiverId);
	long countByReceiver_UserIdAndIsReadFalse(Integer receiverId);
	public interface UnreadCountPerSender {
	    Integer getSenderId();
	    Long getUnreadCount();
	}
	@Query("SELECT m.sender.userId AS senderId, COUNT(m) AS unreadCount " +
		       "FROM Message m " +
		       "WHERE m.receiver.userId = :receiverId " +
		       "AND m.isRead = false " +
		       "GROUP BY m.sender.userId")
		List<UnreadCountPerSender> getUnreadCountsPerSender(@Param("receiverId") Integer receiverId);
	long countBySender_UserIdAndReceiver_UserIdAndIsReadFalse(Integer senderId, Integer receiverId);
}
