<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Đăng ký - AloUTE</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <style>
        html, body {
            height: 100%;
        }
        body {
            background-image: url('assets/bg-social.jpg');
            background-size: cover;
            background-position: center;
            background-repeat: no-repeat;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 2rem 0;
        }
        .register-card {
            background-color: rgba(30, 41, 59, 0.85);
            backdrop-filter: blur(10px);
            border: 1px solid #334155;
            max-width: 550px;
            width: 100%;
        }
        .form-control, .form-floating > .form-control, .form-select {
            background-color: #334155;
            border-color: #475569;
            color: #e2e8f0;
        }
        .form-control:focus, .form-floating > .form-control:focus, .form-select:focus {
            background-color: #334155;
            border-color: #0ea5e9;
            box-shadow: 0 0 0 0.25rem rgba(14, 165, 233, 0.25);
            color: #e2e8f0;
        }
        .form-floating > label, .form-label {
            color: #94a3b8;
        }
        .btn-primary {
            background-color: #0ea5e9;
            border-color: #0ea5e9;
        }
        .btn-primary:hover {
            background-color: #0284c7;
            border-color: #0284c7;
        }
        .btn-outline-secondary {
            border-color: #475569;
            color: #94a3b8;
        }
        .btn-outline-secondary:hover {
            background-color: #475569;
            color: #e2e8f0;
        }
    </style>
</head>
<body class="text-light">

    <main class="m-auto">
        <div class="card register-card rounded-4 shadow">
            <div class="card-body p-4 p-md-5">
                <div class="text-center mb-4">
                    <a class="navbar-brand fw-bold fs-2 d-inline-flex align-items-center text-decoration-none" href="index.jsp">
                        <svg class="bi me-2" width="40" height="40" fill="#0ea5e9" viewBox="0 0 16 16"><path d="M8 0a1 1 0 0 1 1 1v5.268l4.562-2.634a1 1 0 1 1 1 1.732L10 8l4.562 2.634a1 1 0 1 1-1 1.732L9 9.732V15a1 1 0 1 1-2 0V9.732L2.438 12.366a1 1 0 1 1-1-1.732L6 8 1.438 5.366a1 1 0 0 1 1-1.732L7 6.268V1a1 1 0 0 1 1-1z"/></svg>
                        AloUTE
                    </a>
                    <h1 class="h3 my-3 fw-normal">Tạo tài khoản mới</h1>
                </div>

                <form id="registerForm" action="/do-register" method="post">
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" id="fullName" name="fullName" placeholder="Họ và tên" required>
                        <label for="fullName">Họ và tên</label>
                    </div>
    
                    <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" id="location" name="location" placeholder="Quê quán">
                        <label for="location">Quê quán</label>
                    </div>
                    
                    <label for="email" class="form-label">Email</label>
                    <div class="input-group mb-3">
                        <input type="email" class="form-control rounded-start-3" id="email" name="email" placeholder="name@example.com" required style="border-top-right-radius: 0; border-bottom-right-radius: 0;">
                        <button class="btn btn-outline-secondary rounded-end-3" type="button" id="sendCodeBtn" onclick="sendCode()">Gửi mã</button>
					</div>

                    <div id="email-status" style="color: green; font-size: 14px; margin-top: -10px; margin-bottom: 10px;"></div>

                    <div class="form-floating mb-3">
                        <input type="text" class="form-control rounded-3" id="verificationCode" name="verificationCode" placeholder="Mã xác nhận" required>
                        <label for="verificationCode">Mã xác nhận</label>
                    </div>
                    <div id="code-status" style="color: red; font-size: 14px; margin-top: -10px; margin-bottom: 10px;"></div>
                    <div class="row g-2 mb-3">
                        <div class="col-md">
                            <div class="form-floating">
                                <input type="password" class="form-control rounded-3" id="password" name="password" placeholder="Mật khẩu" required>
                                <label for="password">Mật khẩu</label>
                            </div>
                        </div>
                        <div class="col-md">
                            <div class="form-floating">
                                <input type="password" class="form-control rounded-3" id="confirmPassword" name="confirmPassword" placeholder="Xác nhận mật khẩu" required>
                                <label for="confirmPassword">Xác nhận mật khẩu</label>
                            </div>
                        </div>
                    </div>
                    <div id="password-status" style="color: red; font-size: 14px; margin-top: -10px; margin-bottom: 10px;"></div>
                    <div class="row g-2 mb-3">
                        <div class="col-md">
                            <div class="form-floating">
                                <input type="date" class="form-control rounded-3" id="birthday" name="birthday" placeholder="Ngày sinh">
                                <label for="birthday">Ngày sinh</label>
                            </div>
                        </div>
                        <div class="col-md">
                            <div class="form-floating">
                                <select class="form-select rounded-3" id="gender" name="gender">
                                  <option selected disabled value="">Chọn giới tính</option>
                                  <option value="Nam">Nam</option>
                                  <option value="Nữ">Nữ</option>
                                  <option value="Khác">Khác</option>
                                </select>
                                <label for="gender">Giới tính</label>
                            </div>
                        </div>
                    </div>

                    <button class="w-100 btn btn-lg btn-primary rounded-pill mt-4" type="submit">Đăng ký</button>

                    <div class="text-center mt-4">
                        <p class="text-muted mb-0">Đã có tài khoản? <a href="/login" class="text-decoration-none">Đăng nhập ngay</a></p>
                    </div>
                </form>
            </div>
        </div>
    </main>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    function sendCode() {
        const email = document.getElementById("email").value.trim();
        const sendCodeBtn = document.getElementById('sendCodeBtn');
        const emailStatus = document.getElementById('email-status');

        if (email === "") {
            alert("Vui lòng nhập email trước khi gửi mã!");
            return;
        }

        sendCodeBtn.disabled = true;
        emailStatus.style.color = "green";
        emailStatus.textContent = "Đang gửi..."; // Thông báo tạm thời

        // Gọi backend gửi OTP
        fetch("/api/send-otp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ email: email })
        })
        .then(res => res.text().then(text => ({ status: res.status, message: text })))
        .then(data => {
            emailStatus.textContent = data.message;

            if (data.status === 409) {
                // ❌ Email đã tồn tại → không chạy đếm ngược
                emailStatus.style.color = "red";
                sendCodeBtn.disabled = false;
                sendCodeBtn.textContent = 'Gửi mã';
            } else {
                // ✅ Gửi thành công → bắt đầu đếm ngược
                emailStatus.style.color = "green";
                let seconds = 60;
                const interval = setInterval(() => {
                    seconds--;
                    sendCodeBtn.textContent = seconds + " s";

                    if (seconds <= 0) {
                        clearInterval(interval);
                        sendCodeBtn.textContent = 'Gửi mã';
                        sendCodeBtn.disabled = false;
                    }
                }, 1000);
            }
        })
        .catch(err => {
            console.error(err);
            emailStatus.style.color = "red";
            emailStatus.textContent = "❌ Có lỗi xảy ra, vui lòng thử lại.";
            sendCodeBtn.disabled = false;
            sendCodeBtn.textContent = 'Gửi mã';
        });
    }
</script>


<script>
// 👉 Hàm tạo alert nổi góc phải màn hình
function showSuccessAlert(message) {
    const container = document.getElementById("alert-container");

    const alertDiv = document.createElement("div");
    alertDiv.className = "alert alert-success alert-dismissible fade show";
    alertDiv.setAttribute("role", "alert");
    alertDiv.style.minWidth = "300px";

    // Ghép chuỗi theo cách truyền thống, không dùng template string
    alertDiv.innerHTML =
        "<strong>Thành công!</strong> " + message +
        '<button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>';

    container.appendChild(alertDiv);

    // Tự động ẩn sau 3 giây
    setTimeout(function () {
        alertDiv.classList.remove("show");
        alertDiv.classList.add("fade");
        setTimeout(function () {
            alertDiv.remove();
        }, 300);
    }, 3000);
}


document.getElementById("registerForm").addEventListener("submit", async function(event) {
    event.preventDefault(); // ❌ chặn reload

    const fullName = document.getElementById("fullName").value.trim();
    const location = document.getElementById("location").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value.trim();
    const confirmPassword = document.getElementById("confirmPassword").value.trim();
    const birthday = document.getElementById("birthday").value.trim();
    const gender = document.getElementById("gender").value.trim();
    const verificationCode = document.getElementById("verificationCode").value.trim();

    const emailStatus = document.getElementById("email-status");
    const passwordStatus = document.getElementById("password-status");
    const codeStatus = document.getElementById("code-status");

    // reset thông báo
    emailStatus.textContent = "";
    passwordStatus.textContent = "";
    codeStatus.textContent = "";

    try {
        const response = await fetch("/do-register", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: new URLSearchParams({
                fullName: fullName,
                location: location,
                email: email,
                password: password,
                confirmPassword: confirmPassword,
                verificationCode: verificationCode,
                birthday: birthday,
                gender: gender
            })
        });

        const msg = await response.text();

        if (response.ok) {
            window.location.href = "/login";
        } else if (response.status === 400) {
            codeStatus.textContent = msg;           // OTP sai
        } else if (response.status === 401) {
            passwordStatus.textContent = msg;       // Xác nhận mật khẩu sai
        } else if (response.status === 409) {
            emailStatus.textContent = msg;          // Email trùng
        } else {
            codeStatus.textContent = "❌ Có lỗi xảy ra.";
        }

    } catch (err) {
        console.error("Lỗi:", err);
        codeStatus.textContent = "❌ Không thể kết nối server!";
    }
});
</script>
<div id="alert-container" style="position: fixed; top: 20px; right: 20px; z-index: 9999;"></div>

</body>
</html>