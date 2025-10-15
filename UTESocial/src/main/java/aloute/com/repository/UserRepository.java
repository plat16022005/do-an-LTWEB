package aloute.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import aloute.com.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserId(Integer userId);
	User findByNameUser(String nameUser);
    User findByEmail(String email);
    List<User> findByRoleIn(List<String> roles);  // Tìm tất cả người dùng có vai trò trong danh sách được cung cấp
    long countByRoleIn(List<String> roles); // Đếm số lượng người dùng có vai trò trong danh sách

    @Query("SELECT u FROM User u WHERE " +
        "LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<User> searchByFullNameOrEmail(@Param("keyword") String keyword);
}
