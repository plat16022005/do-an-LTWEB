package aloutesocial.com.controller;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import aloutesocial.com.service.*;
import aloutesocial.com.service.impl.AccountServiceImpl;
import aloutesocial.com.service.impl.UserServiceImpl;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/register")
public class RegisterController extends HttpServlet {
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		HttpSession session = req.getSession(false);
		if (session != null && session.getAttribute("username") != null) {
			resp.sendRedirect(req.getContextPath() + "/admin");
			return;
		}
		Cookie[] cookies = req.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("username")) {
					session = req.getSession(true);
					session.setAttribute("username", cookie.getValue());
					resp.sendRedirect(req.getContextPath() + "/admin");
					return;
				}
			}
		}
		resp.sendRedirect(req.getContextPath() + "/views/register.jsp");
	}

	@SuppressWarnings("static-access")
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		PrintWriter out = resp.getWriter();
		out.println("Ok");
		resp.setCharacterEncoding("UTF-8");
		req.setCharacterEncoding("UTF-8");
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String repassword = req.getParameter("repassword");
		String email = req.getParameter("email");
		String fullname = req.getParameter("hovaten");
		String phone = req.getParameter("sodienthoai");
		String gioitinh = req.getParameter("gioitinh");
		String date = req.getParameter("ngaysinh");
		UserService serviceUser = new UserServiceImpl();
		AccountService serviceAccount = new AccountServiceImpl();
		String alertMsg = "";
		if (serviceUser.checkExistEmail(email)) {
			alertMsg = "Email đã tồn tại!";
			System.out.println(alertMsg);
			req.setAttribute("alert", alertMsg);
			resp.sendRedirect(req.getContextPath() + "/views/register.jsp");
			return;
		}
		if (serviceAccount.checkExistUsername(username)) {
			alertMsg = "Tài khoản đã tồn tại!";
			System.out.println(alertMsg);
			req.setAttribute("alert", alertMsg);
			req.getRequestDispatcher("/views/register.jsp").forward(req, resp);
			return;
		}
		if (serviceAccount.checkRepassword(password, repassword)) {
			alertMsg = "Vui lòng nhập lại mật khẩu trùng với mật khẩu đã đặt";
			System.out.println(alertMsg);
			req.setAttribute("alert", alertMsg);
			resp.sendRedirect(req.getContextPath() + "/views/register.jsp");
			return;
		}
		boolean isSuccess1 = serviceAccount.register(username, password, repassword);
		boolean isSuccess2 = serviceUser.register(username, email, fullname, phone, gioitinh, date);
		
		if (isSuccess1 && isSuccess2) {
			// SendMail sm = new SendMail();
			// sm.sendMail(email, "Shopping.iotstar.vn", "Welcome to Shopping. Please Login
			// to use service. Thanks !");
			req.setAttribute("alert", alertMsg);
			resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
		} else {
			alertMsg = "System error!";
			System.out.println(alertMsg);
			req.setAttribute("alert", alertMsg);
			resp.sendRedirect(req.getContextPath() + "/views/register.jsp");
		}
	}

}
