package aloutesocial.com.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import aloutesocial.com.config.DBConnection;
import aloutesocial.com.dao.UserDao;
import aloutesocial.com.model.User;

public class UserDaoImpl implements UserDao {
	public Connection conn = null;
	public PreparedStatement ps = null;
	public ResultSet rs = null;
	@Override
	public void insert(User user) {
	    String sql = "INSERT INTO user(user_name,email,ho_va_ten,so_dien_thoai,gioi_tinh,ngay_sinh) VALUES(?,?,?,?,?,?)";
	    try {
	        conn = new DBConnection().getConnection();
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, user.getUsername());
	        ps.setString(2, user.getEmail());
	        ps.setString(3, user.getHovaten());
	        ps.setString(4, user.getSodienthoai());
	        ps.setString(5, user.getGioitinh());
	        ps.setString(6, user.getNgaysinh());
	        
	        int rows = ps.executeUpdate();  // sửa chỗ này
	        if (rows > 0) {
	            System.out.println("Thêm user thành công!");
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
	public boolean checkExistEmail(String email) {
		boolean duplicate = false;
		String query = "select * from user where email = ?";
		try {
			conn = new DBConnection().getConnection();
			ps = conn.prepareStatement(query);
			ps.setString(1, email);
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
	public String getEmail(String username) {
	    String sql = "SELECT email FROM user WHERE user_name = ?";
	    try {
	        conn = new DBConnection().getConnection();
	        ps = conn.prepareStatement(sql);
	        ps.setString(1, username);
	        rs = ps.executeQuery();

	        String email = null;
	        if (rs.next()) {  // phải gọi rs.next() để trỏ đến dòng dữ liệu
	            email = rs.getString("email");
	        }

	        rs.close();
	        ps.close();
	        conn.close();

	        return email;
	    } catch (Exception ex) {
	        ex.printStackTrace();
	        return "cc";
	    }
	}

}
