package aloute.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aloute.com.entity.User;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserId(Integer userId);
	User findByNameUser(String nameUser);
    User findByEmail(String email);
    Page<User> findByRoleIn(List<String> roles, Pageable pageable);;  // Tìm tất cả người dùng có vai trò trong danh sách được cung cấp, trả về page user
    long countByRoleIn(List<String> roles); // Đếm số lượng người dùng có vai trò trong danh sách

    @Query("SELECT u FROM User u WHERE " +
        "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(u.nameUser) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByFullNameOrNameUser(@Param("keyword") String keyword);
}
