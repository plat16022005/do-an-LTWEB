package aloute.com.repository;

import aloute.com.entity.User;
import aloute.com.entity.UserGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {
    List<UserGroup> findByUserUserId(Integer userId);
    List<UserGroup> findByGroupGroupId(Integer groupId);
    
    @Query("SELECT ug FROM UserGroup ug JOIN FETCH ug.user WHERE ug.group.groupId = :groupId")
    List<UserGroup> findByGroupGroupIdWithUser(@Param("groupId") Integer groupId);
    
    @Query(value = """
    	    SELECT u.UserID
    	    FROM Users u
    	    WHERE u.UserID IN (
    	        SELECT CASE 
    	                 WHEN f.UserID1 = :ownerId THEN f.UserID2
    	                 ELSE f.UserID1
    	               END
    	        FROM Friends f
    	        WHERE (f.UserID1 = :ownerId OR f.UserID2 = :ownerId)
    	          AND (f.Status = 'accepted' OR f.Status IS NULL) -- tuỳ DB bạn có dùng trạng thái
    	    )
    	    AND u.UserID NOT IN (
    	        SELECT ug.UserID FROM UserGroups ug WHERE ug.GroupID = :groupId
    	    )
    	""", nativeQuery = true)
    	List<Integer> findFriendIdsNotInGroup(
    	    @Param("ownerId") Integer ownerId,
    	    @Param("groupId") Integer groupId
    	);
    Optional<UserGroup> findByGroupGroupIdAndUserUserId(Integer groupId, Integer userId);
 // (Giữ nguyên các hàm cũ của bạn, ví dụ: findByGroup_GroupId)
    List<UserGroup> findByGroup_GroupId(Integer groupId); 
    
    // ⭐ THÊM HÀM NÀY:
    // Tự động tạo câu query kiểm tra xem có tồn tại bản ghi
    // khớp cả userId và groupId không.
    boolean existsByUser_UserIdAndGroup_GroupId(Integer userId, Integer groupId);
    
}
