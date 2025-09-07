package aloutesocial.com.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import aloutesocial.com.service.EmailService;
import aloutesocial.com.service.impl.EmailServiceImpl;
import aloutesocial.com.service.AccountService;
import aloutesocial.com.service.impl.AccountServiceImpl;
import aloutesocial.com.dao.AccountDao;
import aloutesocial.com.dao.impl.AccountDaoImpl;
/**
 * Servlet implementation class PassController
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = "/forgetpass")
public class PassController extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public PassController() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getParameter("action");

        if ("sendOtp".equals(action)) {
            handleSendOtp(req, resp);
        } else if ("verifyOtp".equals(action)) {
        	System.out.println("verifyOtp");
            handleVerifyOtp(req, resp);
        } else if ("updatePass".equals(action)) {
        	handleUpdateNewPass(req, resp);
        }
        else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ!");
        }
	}
	private void handleSendOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String username = req.getParameter("username");
		req.getSession().setAttribute("username", username);
		EmailService serviceEmail = new EmailServiceImpl();
		String otp = serviceEmail.generateOTP(6); // 6 số
		String toEmail = serviceEmail.getEmail(username);
		System.out.println(toEmail);
		System.out.println(otp);
		req.getSession().setAttribute("otp", otp);
		req.getSession().setMaxInactiveInterval(300); // 5 phút

		serviceEmail.sendEmail(toEmail, "Mã xác nhận của bạn", "Mã OTP: " + otp);
		resp.sendRedirect(req.getContextPath() + "/views/confirmPass.jsp");
	}
    private void handleVerifyOtp(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String inputOtp = req.getParameter("otp");
        String sessionOtp = (String) req.getSession().getAttribute("otp");
        String username = (String) req.getSession().getAttribute("username");
        AccountService serviceAccount = new AccountServiceImpl();
        AccountDao accountDao = new AccountDaoImpl();
        if (sessionOtp != null && sessionOtp.equals(inputOtp)) {
        	resp.sendRedirect(req.getContextPath() + "/views/showpass.jsp");
        } else {
            req.setAttribute("error", "Mã OTP không đúng hoặc đã hết hạn!");
            resp.sendRedirect(req.getContextPath() + "/views/confirmPass.jsp");
        }
    }
    
    private void handleUpdateNewPass(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String newPass = req.getParameter("newpass");
        String username = (String) req.getSession().getAttribute("username");

        AccountDao accountDao = new AccountDaoImpl();
        accountDao.updatePass(username, newPass);
        req.getSession().setAttribute("msg", "Cập nhật mật khẩu thành công. Vui lòng đăng nhập lại!");
        resp.sendRedirect(req.getContextPath() + "/views/login.jsp");
    }
}
