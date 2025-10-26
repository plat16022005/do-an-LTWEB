package aloute.com.repository;

import aloute.com.entity.GroupsUTE;
import aloute.com.entity.UserGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GroupsUTERepository extends JpaRepository<GroupsUTE, Integer> {
    // Lấy các nhóm mà người dùng là thành viên
    List<GroupsUTE> findByMembersUserUserId(Integer userId);

}
