<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>AloUTE</title>
<style type="text/css">
body {
	margin: 0; /* bỏ viền trắng mặc định */
	height: 100vh; /* full màn hình */
	background: url("../images/background.jpeg") no-repeat center center;
	background-size: cover; /* ảnh phủ kín màn hình */
}

.box {
	width: 500px; /* chiều rộng ô trắng */
	height: 100vh; /* chiều cao ô trắng */
	position: absolute; /* định vị tuyệt đối theo body */
	top: 0; /* sát mép trên */
	right: 0; /* cách mép trái 1000px */
	background-color: white; /* màu trắng */
	box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* đổ bóng */
	padding: 20px; /* khoảng cách trong */
}

.logo {
	display: block;
	margin: 0 auto; /* căn giữa ảnh trong ô */
	width: 150px; /* chiều rộng mong muốn */
	height: auto;
	margin: 0 auto; /* tự co theo tỉ lệ */
}

.title {
	text-align: center; /* căn giữa chữ */
	font-size: 48px;
	margin-bottom: 10px;
}

.desc {
	font-family: Tahoma, sans-serif;
	text-align: center;
	font-size: 36px;
	color: #333;
	text-align: center;
}

.btn {
	display: block;
	margin: 20px auto; /* căn giữa */
	padding: 10px 20px; /* khoảng cách trong */
	width: 250px;
	height: 50px;
	font-size: 24px;
	font-weight: bold;
	color: white;
	background-color: #007BFF; /* màu xanh */
	border: none; /* bỏ viền */
	border-radius: 8px; /* bo góc */
	cursor: pointer; /* đổi con trỏ chuột */
	transition: 0.3s; /* hiệu ứng hover */
}

.btn:hover {
	background-color: #0056b3; /* đổi màu khi hover */
}

.input-box {
	display: block;
	width: 250px; /* full chiều rộng ô trắng */
	height: 40px; /* chiều cao cụ thể */
	margin: 5px auto; /* khoảng cách giữa các ô */
	padding: 0 10px; /* khoảng cách trong (trái/phải) */
	font-size: 16px; /* cỡ chữ */
	border: 1px solid #ccc; /* viền xám */
	border-radius: 6px; /* bo góc */
	box-sizing: border-box; /* tính cả padding vào width */
}

.input-box:focus {
	border-color: #007BFF; /* viền xanh khi focus */
	outline: none; /* bỏ viền xanh mặc định */
}
</style>
</head>
<body>
	<div class="box">
		<img src="../images/logo.png" alt="Logo" class="logo">
		<h1 class="title">REGISTER</h1>
		<form action="../register" method="post">
			<input type="text" name="username" placeholder="Tên đăng nhập"
				class="input-box"> <input type="password" name="password"
				placeholder="Mật khẩu" class="input-box"> <input
				type="password" name="repassword" placeholder="Nhập lại mật khẩu"
				class="input-box"> <input type="text" name="email"
				placeholder="Email" class="input-box"> <input type="text"
				name="hovaten" placeholder="Họ và tên" class="input-box"> <input
				type="text" name="sodienthoai" placeholder="Số điện thoại"
				class="input-box">

			<!-- Giới tính -->
			<select name="gioitinh" class="input-box">
				<option value="">-- Chọn giới tính --</option>
				<option value="Nam">Nam</option>
				<option value="Nữ">Nữ</option>
			</select>

			<!-- Ngày sinh -->
			<input type="date" name="ngaysinh" class="input-box">

			<button type="submit" class="btn">Register</button>
		</form>

		<form action="auth.jsp" method="post">
			<button type="submit" class="btn">BACK</button>
		</form>
	</div>
</body>
<script>
	
</script>
</html>