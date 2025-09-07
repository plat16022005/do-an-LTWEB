package aloutesocial.com.dao;

import aloutesocial.com.model.User;

public interface UserDao {
	void insert(User user);

	boolean checkExistEmail(String email);
	
	String getEmail(String username);
}
