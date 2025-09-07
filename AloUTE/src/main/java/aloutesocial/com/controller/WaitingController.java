package aloutesocial.com.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import aloutesocial.com.model.Account;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/waiting")
public class WaitingController extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false); // Dùng false để tránh tạo session mới không cần thiết

		// Kiểm tra xem session có tồn tại và có chứa thuộc tính "username" không
		if (session != null && session.getAttribute("username") != null) {
			// Lấy đối tượng User từ session với đúng key là "username"
			Account u = (Account) session.getAttribute("username");
			System.out.println(u.getRole());
			// Dựa vào roleid để chuyển hướng
			if ("admin".equals(u.getRole())) {
				resp.sendRedirect(req.getContextPath() + "/views/admin/UI.jsp");
			} else if ("manager".equals(u.getRole())) {
				resp.sendRedirect(req.getContextPath() + "/views/manager/UI.jsp");
			} else {
				resp.sendRedirect(req.getContextPath() + "/views/user/UI.jsp");
			}
		} else {
			// Nếu không có session hoặc không có thông tin user, chuyển về trang đăng nhập
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}
}
