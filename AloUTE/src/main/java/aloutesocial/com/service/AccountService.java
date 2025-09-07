package aloutesocial.com.service;

import aloutesocial.com.model.Account;

public interface AccountService {
	Account login(String username, String password);
	void insert(Account account);
	boolean checkExistUsername(String username);
	boolean checkRepassword(String password, String repassword);
	boolean register(String username, String password, String repassword);
	String generateRandomPassword(int length);
}
