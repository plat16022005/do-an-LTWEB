package aloute.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aloute.com.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {
	User findByUserId(Integer userId);
	User findByNameUser(String nameUser);
    User findByEmail(String email);
    List<User> findByRoleIn(List<String> roles);  // Tìm tất cả người dùng có vai trò trong danh sách được cung cấp
    long countByRoleIn(List<String> roles); // Đếm số lượng người dùng có vai trò trong danh sách
}
