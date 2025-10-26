package aloute.com.repository;

import aloute.com.entity.GroupMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupMessageRepository extends JpaRepository<GroupMessage, Integer> {
    List<GroupMessage> findByGroupGroupIdOrderByCreatedAtAsc(Integer groupId);
    @Query("SELECT gm FROM GroupMessage gm " +
            "LEFT JOIN FETCH gm.sender s " +
            "LEFT JOIN FETCH gm.attachments a " +
            "WHERE gm.group.groupId = :groupId " +
            "ORDER BY gm.createdAt ASC")
     List<GroupMessage> findMessagesByGroupIdWithDetails(@Param("groupId") Integer groupId);
}
