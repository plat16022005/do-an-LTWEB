package aloutesocial.com.dao;

import aloutesocial.com.model.Account;

public interface AccountDao {
	Account get(String username);
	void insert(Account account);
	void updatePass(String username, String newpass);
	boolean checkExistUsername(String username);
	boolean checkRepassword(String password, String repassword);
}
