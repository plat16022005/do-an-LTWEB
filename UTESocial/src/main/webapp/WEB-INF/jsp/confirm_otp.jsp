<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đặt Lại Mật Khẩu - AloUTE</title>
    <!-- Bootstrap CSS - ĐÃ SỬA LỖI -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css" rel="stylesheet" xintegrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB" crossorigin="anonymous">
    <style>
        /* Đảm bảo body chiếm toàn bộ chiều cao */
        html, body {
            height: 100%;
        }
        body {
            /* Đường dẫn này giả định thư mục 'assets' nằm ở gốc của ứng dụng web */
            background-image: url('assets/bg-social.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem 0;
        }
        /* Card */
        .form-card {
            background-color: rgba(30, 41, 59, 0.85);
            backdrop-filter: blur(10px);
            border: 1px solid #334155;
            max-width: 500px;
            width: 100%;
        }
        /* Tùy chỉnh input */
        .form-control, .form-floating > .form-control {
            background-color: #334155;
            border-color: #475569;
            color: #e2e8f0;
        }
        .form-control:focus, .form-floating > .form-control:focus {
            background-color: #334155;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 0.25rem rgba(14, 165, 233, 0.25);
            color: #e2e8f0;
        }
        .form-floating > label {
            color: #94a3b8;
        }
        /* Tùy chỉnh nút */
        .btn-primary {
            background-color: #0ea5e9;
            border-color: #0ea5e9;
        }
        .btn-primary:hover {
            background-color: #0284c7;
            border-color: #0284c7;
        }
    </style>
</head>
<body class="text-light">

    <main class="m-auto">
        <div class="card form-card rounded-4 shadow">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <a class="navbar-brand fw-bold fs-2 d-inline-flex align-items-center text-decoration-none" href="/">
                        <svg class="bi me-2" width="40" height="40" fill="#0ea5e9" viewBox="0 0 16 16"><path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732L2.438 12.366a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1z"/></svg>
                        AloUTE
                    </a>
                </div>

                <!-- Form Nhập OTP và Mật khẩu mới -->
                <div id="otp-step">
                     <div class="text-center">
                         <h1 class="h3 mb-3 fw-normal">Xác nhận OTP</h1>
                         <p class="text-muted">Mã OTP đã được gửi đến email của bạn. Vui lòng nhập mã để tiếp tục.</p>
                    </div>
                    <form id="reset-password-form" action="/do-reset-pass" method="post">
                        <div class="form-floating mb-3">
                            <input type="text" class="form-control rounded-3" id="otp" name="otp" placeholder="Mã OTP" required>
                            <label for="otp">Mã OTP</label>
                        </div>
						<c:if test="${not empty errorOTP}">
							<div
								style="color: red; font-size: 0.9rem; margin-top: -10px; margin-bottom: 10px;">${errorOTP}</div>
						</c:if>
						<div class="form-floating mb-3">
                            <input type="password" class="form-control rounded-3" id="password" name="password" placeholder="Mật khẩu mới" required>
                            <label for="password">Mật khẩu mới</label>
                        </div>
                        <div class="form-floating mb-3">
                            <input type="password" class="form-control rounded-3" id="confirm-password" name="confirmPassword" placeholder="Xác nhận mật khẩu mới" required>
                            <label for="confirm-password">Xác nhận mật khẩu mới</label>
                        </div>
						<c:if test="${not empty errorPass}">
							<div
								style="color: red; font-size: 0.9rem; margin-top: -10px; margin-bottom: 10px;">${errorPass}</div>
						</c:if>
						<button class="w-100 btn btn-lg btn-primary rounded-pill mt-3" type="submit">Đặt lại mật khẩu</button>
                    </form>
                </div>
                 <div class="text-center mt-4">
                    <a href="/login" class="text-decoration-none">Quay lại Đăng nhập</a>
                </div>
            </div>
        </div>
    </main>

    <!-- Bootstrap JS - ĐÃ SỬA LỖI -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
</body>
</html>

