package aloutesocial.com.service;

import aloutesocial.com.model.User;

public interface UserService {
	void insert(User user);

	boolean checkExistEmail(String email);
	boolean register(String email, String username, String fullname, String phone, String gioitinh, String ngaysinh);
	String getEmailUser(String username);
}
