package aloute.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import aloute.com.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
	User findByEmail(String email);
}
