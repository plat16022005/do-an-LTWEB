package aloutesocial.com.service.impl;

import aloutesocial.com.dao.UserDao;
import aloutesocial.com.dao.impl.UserDaoImpl;
import aloutesocial.com.model.User;
import aloutesocial.com.service.UserService;

public class UserServiceImpl implements UserService {
	UserDao userDao = new UserDaoImpl();
	@Override
	public void insert(User user) {
		userDao.insert(user);
	}

	@Override
	public boolean checkExistEmail(String email) {
		return userDao.checkExistEmail(email);
	}

	public boolean register(String email, String username, String fullname, String phone, String gioitinh, String ngaysinh) {
	    if (userDao.checkExistEmail(email)) {
	        return false;
	    }

	    // Không set id, để DB tự tăng
	    User user = new User(email, username, fullname, phone, gioitinh, ngaysinh);
	    userDao.insert(user);
	    return true;
	}

	@Override
	public String getEmailUser(String username) {
		return userDao.getEmail(username);
	}
}
