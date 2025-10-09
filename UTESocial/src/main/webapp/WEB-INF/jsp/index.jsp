<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="vi" data-bs-theme="dark">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Khám Phá - AloUTE (Bootstrap)</title>
    <!-- Bootstrap CSS -->
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/css/bootstrap.min.css"
	rel="stylesheet"
	xintegrity="sha384-sRIl4kxILFvY47J16cr9ZwB07vP4J8+LH7qKQnuqkuIAvNWLzeN8tE5YBujZqJLB"
	crossorigin="anonymous">

<!-- Bootstrap Icons -->
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">
    <style>
        /* Tùy chỉnh màu nền chính */
        body {
            background-color: #0f172a; 
        }

        /* Tùy chỉnh thanh cuộn */
        ::-webkit-scrollbar {
            width: 8px;
        }
        ::-webkit-scrollbar-track {
            background: #0f172a;
        }
        ::-webkit-scrollbar-thumb {
            background: #334155;
            border-radius: 4px;
        }
        ::-webkit-scrollbar-thumb:hover {
            background: #475569;
        }

        /* Tùy chỉnh component Card của Bootstrap */
        .card {
            background-color: #1e293b; /* Slate 800 */
            border: 1px solid #334155; /* Slate 700 */
        }
        
        /* Tùy chỉnh thanh điều hướng trên cùng */
        .navbar {
            background-color: rgba(15, 23, 42, 0.8);
            backdrop-filter: blur(10px);
            border-bottom: 1px solid #334155;
        }
        
        .navbar-nav .nav-link.active {
            color: #fff !important;
            border-bottom: 2px solid #0ea5e9; /* Sky 500 */
        }
        .navbar-nav .nav-link {
            border-bottom: 2px solid transparent;
        }
        
        /* Tùy chỉnh các nút tương tác trong bài viết */
        .post-actions .btn:hover {
            background-color: rgba(255, 255, 255, 0.1);
        }
    </style>
</head>
<body class="text-light">

    <!-- THANH ĐIỀU HƯỚNG TRÊN CÙNG -->
    <nav class="navbar sticky-top">
        <div class="container-xl">
            <!-- Logo & Tên trang -->
            <a class="navbar-brand fw-bold fs-4 d-flex align-items-center" href="#">
                 <svg class="bi me-2" width="32" height="32" fill="#0ea5e9"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6.253v11.494m-9-5.747h18" stroke="currentColor"></path></svg>
                AloUTE
            </a>

            <!-- Menu điều hướng chính (Ở giữa) -->
            <ul class="navbar-nav flex-row gap-4 position-absolute start-50 translate-middle-x">
                <li class="nav-item">
                    <a class="nav-link active" aria-current="page" href="#" id="home-link">Trang chủ</a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" href="#" id="policy-link">Chính sách</a>
                </li>
            </ul>

            <!-- Nút Đăng nhập -->
            <a href="/login" class="btn btn-primary rounded-pill">
                Đăng nhập
            </a>
        </div>
    </nav>

    <!-- BỐ CỤC CHÍNH -->
    <div class="container-xl mt-4">
        <div class="row g-4">

            <!-- THANH ĐIỀU HƯỚNG BÊN TRÁI -->
            <div class="col-lg-3 d-none d-lg-block">
                <aside class="sticky-top" style="top: 80px;">
                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a id="home-sidebar-button" class="nav-link active bg-dark rounded-pill fs-5 fw-bold d-flex align-items-center gap-3 p-2" href="#">
                                <i class="bi bi-house-door-fill fs-3"></i>
                                <span>Trang chủ</span>
                            </a>
                        </li>
                    </ul>
                </aside>
            </div>

            <!-- NỘI DUNG CHÍNH (BÊN PHẢI) -->
            <div class="col-lg-9">
                <!-- NỘI DUNG TRANG CHỦ -->
                <div id="home-content">
                    <main class="d-grid gap-3">
                        <!-- Bài viết mẫu 1 (Có ảnh) -->
                        <article class="card rounded-4 overflow-hidden">
                            <div class="card-body">
                                <div class="d-flex align-items-center gap-3 mb-3">
                                    <img class="rounded-circle" src="https://placehold.co/48x48/34d399/FFFFFF?text=A" alt="Avatar">
                                    <div>
                                        <p class="fw-bold mb-0">An Nhiên</p>
                                        <p class="text-muted small mb-0">@annhien · 15 phút trước</p>
                                    </div>
                                </div>
                                <p>Một buổi chiều thật đẹp để đi dạo bên bờ hồ. Không khí trong lành và cảnh hoàng hôn thật tuyệt vời! 🌅 #thiênnhiên #thưgiãn</p>
                            </div>
                            <img src="https://placehold.co/600x400/67e8f9/0f172a?text=Cảnh+Hoàng+Hôn" class="card-img-bottom" alt="Ảnh hoàng hôn bên hồ">
                            <div class="card-body d-flex justify-content-between post-actions">
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-chat"></i> 15</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-repeat"></i> 3</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-heart"></i> 210</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-upload"></i></button>
                            </div>
                        </article>
                        
                        <!-- Bài viết mẫu 2 -->
                        <article class="card rounded-4 overflow-hidden">
                            <div class="card-body">
                                <div class="d-flex align-items-center gap-3 mb-3">
                                    <img class="rounded-circle" src="https://placehold.co/48x48/f9a8d4/FFFFFF?text=B" alt="Avatar">
                                    <div>
                                        <p class="fw-bold mb-0">Bảo Trân</p>
                                        <p class="text-muted small mb-0">@baotran · 1 giờ trước</p>
                                    </div>
                                </div>
                                <p>Vừa đọc xong một cuốn sách rất hay. "Atomic Habits" thực sự đã thay đổi cách tôi nhìn nhận về việc xây dựng thói quen tốt. Mọi người nên đọc thử nhé! Highly recommended. 👍</p>
                            </div>
                            <div class="card-body d-flex justify-content-between post-actions">
                               <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-chat"></i> 98</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-repeat"></i> 21</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-heart"></i> 1.8K</button>
                                <button class="btn text-muted d-flex align-items-center gap-2"><i class="bi bi-upload"></i></button>
                            </div>
                        </article>
                    </main>
                </div>

                <!-- NỘI DUNG TRANG CHÍNH SÁCH -->
                <div id="policy-content" class="d-none">
                     <div class="card rounded-4">
                        <div class="card-body p-4 p-md-5">
                            <h2>Chính sách và Điều khoản Sử dụng</h2>
                            <p class="text-muted">Chào mừng bạn đến với trang web Khám Phá. Bằng việc truy cập và sử dụng trang web, bạn đồng ý tuân thủ các điều khoản và điều kiện được nêu dưới đây.</p>
                            
                            <h3 class="mt-4">1. Quyền sở hữu trí tuệ</h3>
                            <p>Tất cả nội dung, hình ảnh, và thiết kế trên trang web này thuộc bản quyền của chúng tôi (trừ nội dung do người dùng đăng tải). Mọi hành vi sao chép, phân phối lại mà không có sự cho phép bằng văn bản đều bị nghiêm cấm.</p>

                            <h3 class="mt-4">2. Nội dung người dùng</h3>
                            <p>Bạn chịu hoàn toàn trách nhiệm về nội dung bạn đăng tải. Bạn không được phép đăng tải nội dung vi phạm pháp luật, có tính chất xúc phạm, hoặc vi phạm bản quyền của người khác. Chúng tôi có quyền xóa bất kỳ nội dung nào vi phạm mà không cần báo trước.</p>

                            <h3 class="mt-4">3. Giới hạn trách nhiệm</h3>
                            <p>Chúng tôi không chịu trách nhiệm cho bất kỳ thiệt hại nào phát sinh từ việc sử dụng hoặc không thể sử dụng trang web. Nội dung trên trang web chỉ mang tính chất tham khảo.</p>
                            
                            <p class="mt-4">Chúng tôi có thể thay đổi các điều khoản này bất cứ lúc nào. Vui lòng kiểm tra lại trang này thường xuyên để cập nhật. Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>
                        </div>
                    </div>
                </div>
            </div>

        </div>
    </div>

    <!-- Bootstrap JS -->
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.8/dist/js/bootstrap.bundle.min.js" xintegrity="sha384-FKyoEForCGlyvwx9Hj09JcYn3nv7wiPVlz7YYwJrWVcXK/BmnVDxM+D2scQbITxI" crossorigin="anonymous"></script>
    <script>
        document.addEventListener('DOMContentLoaded', () => {
            const homeLink = document.getElementById('home-link');
            const policyLink = document.getElementById('policy-link');
            const homeSidebarButton = document.getElementById('home-sidebar-button');

            const homeContent = document.getElementById('home-content');
            const policyContent = document.getElementById('policy-content');

            function showHome() {
                homeContent.classList.remove('d-none');
                policyContent.classList.add('d-none');
                
                homeLink.classList.add('active');
                policyLink.classList.remove('active');
            }

            function showPolicy() {
                homeContent.classList.add('d-none');
                policyContent.classList.remove('d-none');

                homeLink.classList.remove('active');
                policyLink.classList.add('active');
            }

            // Gán sự kiện click
            homeLink.addEventListener('click', (e) => {
                e.preventDefault();
                showHome();
            });
            
            homeSidebarButton.addEventListener('click', (e) => {
                e.preventDefault();
                showHome();
            });

            policyLink.addEventListener('click', (e) => {
                e.preventDefault();
                showPolicy();
            });
        });
    </script>
</body>
</html>

