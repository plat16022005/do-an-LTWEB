package aloutesocial.com.service.impl;

import java.util.Random;

import aloutesocial.com.dao.AccountDao;
import aloutesocial.com.dao.impl.AccountDaoImpl;
import aloutesocial.com.model.Account;
import aloutesocial.com.model.User;
import aloutesocial.com.service.AccountService;

public class AccountServiceImpl implements AccountService {
	AccountDao accountDao = new AccountDaoImpl();
	@Override
	public Account login(String username, String password) {
		Account account = this.get(username);
		return account;
	}
	
	public Account get(String username)
	{
		return accountDao.get(username);
	}

	@Override
	public void insert(Account account) {
		accountDao.insert(account);
		
	}

	@Override
	public boolean checkExistUsername(String username) {
		return accountDao.checkExistUsername(username);
	}

	@Override
	public boolean checkRepassword(String password, String repassword) {
		return accountDao.checkRepassword(password, repassword);
	}

	@Override
	public boolean register(String username, String password, String repassword) {
	    if (accountDao.checkExistUsername(username)) {
	        return false;
	    }
	    if (accountDao.checkRepassword(password, repassword))
	    {
	    	return false;
	    }
	    // Không set id, để DB tự tăng
	    Account account = new Account(username,password, "user", "active");
	    accountDao.insert(account);
	    return true;
	}
	public String generateRandomPassword(int length) {
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#$%";
	    StringBuilder sb = new StringBuilder();
	    Random rnd = new Random();
	    for (int i = 0; i < length; i++) {
	        sb.append(chars.charAt(rnd.nextInt(chars.length())));
	    }
	    return sb.toString();
	}
}
