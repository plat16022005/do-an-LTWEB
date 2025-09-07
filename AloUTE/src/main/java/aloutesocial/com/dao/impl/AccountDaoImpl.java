package aloutesocial.com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import aloutesocial.com.config.DBConnection;
import aloutesocial.com.dao.AccountDao;
import aloutesocial.com.model.Account;

public class AccountDaoImpl implements AccountDao {
	public Connection conn = null;
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	@Override
	public Account get(String username) {
		String sql = "Select * from account where user_name = ?";
	    try {
	        conn = new DBConnection().getConnection();
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, username);
	        rs = ps.executeQuery();

	        if (rs.next()) {
	            Account account = new Account(sql, sql, sql, sql);
	            account.setUser_name(rs.getString("user_name"));
	            account.setPass_word(rs.getString("pass_word"));
	            account.setRole(rs.getString("role"));
	            account.setStatus(rs.getString("status"));
	            return account;
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return null;
	}
	@Override
	public void insert(Account account) {
	    String sql = "INSERT INTO account(user_name, pass_word, role, status) VALUES(?,?,?,?)";
	    try {
	        conn = new DBConnection().getConnection();
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, account.getUser_name());
	        ps.setString(2, account.getPass_word());
	        ps.setString(3, "user");
	        ps.setString(4, "active");
	        
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            System.out.println("Thêm account thành công!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (ps != null) ps.close();
	            if (conn != null) conn.close();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	}

	@Override
	public boolean checkExistUsername(String username) {
		boolean duplicate = false;
		String query = "select * from account where username = ?";
		try {
			conn = new DBConnection().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, username);
			rs = ps.executeQuery();
			if (rs.next()) {
				duplicate = true;
			}
			ps.close();
			conn.close();
		} catch (Exception ex) {
		}
		return duplicate;
	}
	@Override
	public boolean checkRepassword(String password, String repassword) {
	    if (password != null && password.equals(repassword)) {
	        return false;
	    } else {
	        return true;
	    }
	}
	@Override
	public void updatePass(String username, String newpass) {
	    String sql = "UPDATE account SET pass_word = ? WHERE user_name = ?";
	    try (Connection conn = new DBConnection().getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        ps.setString(1, newpass);
	        ps.setString(2, username);
	        int rows = ps.executeUpdate();
	        if (rows > 0) {
	            System.out.println("Sửa mật khẩu thành công!");
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

}
