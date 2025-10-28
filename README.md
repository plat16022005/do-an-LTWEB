<div align="center">

# AloUTE - Trang Mạng Xã Hội

Dự án **AloUTE** là một ứng dụng web mạng xã hội, được xây dựng với mục tiêu mô phỏng các chức năng cơ bản của một nền tảng mạng xã hội hiện đại, tập trung vào việc kết nối người dùng, chia sẻ nội dung và tương tác.  
Dự án này là đồ án môn học Lập trình Web tại Trường Đại học Sư phạm Kỹ thuật TP. Hồ Chí Minh.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-8.x-blue.svg)](https://www.mysql.com/)
[![Thymeleaf](https://img.shields.io/badge/Thymeleaf-3.x-green.svg)](https://www.thymeleaf.org/)
[![Bootstrap](https://img.shields.io/badge/Bootstrap-5.x-purple.svg)](https://getbootstrap.com/)
[![WebSocket](https://img.shields.io/badge/WebSocket-STOMP/SockJS-lightgrey.svg)](https://spring.io/guides/gs/messaging-stomp-websocket/)

**TRƯỜNG ĐẠI HỌC SƯ PHẠM KỸ THUẬT TP. HỒ CHÍ MINH** **KHOA CÔNG NGHỆ THÔNG TIN** **Năm học: 2025 - 2026**



</div>

---

## 📑 Mục lục

1. [🎯 Tổng quan (Overview)](#-tổng-quan-overview)
2. [✨ Tính năng nổi bật](#-tính-năng-nổi-bật)
3. [🛠️ Công nghệ sử dụng](#️-công-nghệ-sử-dụng)
4. [🚀 Hướng dẫn cài đặt (Getting Started)](#-hướng-dẫn-cài-đặt-getting-started)
   - [1. Yêu cầu (Prerequisites)](#1-yêu-cầu-prerequisites)
   - [2. Cài đặt (Installation)](#2-cài-đặt-installation)
   - [3. Cấu hình Cơ sở dữ liệu (Database Setup)](#3-cấu-hình-cơ-sở-dữ-liệu-database-setup)
   - [4. Chạy ứng dụng (Usage)](#4-chạy-ứng-dụng-usage)
5. [📂 Cấu trúc thư mục (Project Structure)](#-cấu-trúc-thư-mục-project-structure)
6. [🚀 Hướng phát triển](#-hướng-phát-triển)
7. [🤝 Đóng góp](#-đóng-góp)

---

## 🎯 Tổng quan (Overview)

**AloUTE** là đồ án môn học Lập trình Web, xây dựng một ứng dụng mạng xã hội sử dụng **Java Spring Boot**. Dự án áp dụng **Spring Data JPA** để tương tác với **MySQL**, **Thymeleaf** và **Bootstrap 5** cho giao diện người dùng. Chức năng chat thời gian thực được triển khai bằng **Spring WebSocket (STOMP qua SockJS)**. Hệ thống cũng tích hợp **Spring Security** (nếu có) cho việc xác thực và phân quyền.

---

## ✨ Tính năng nổi bật

| **Nhóm Chức năng** | **Chi tiết** |
| ------------------------- | ------------ |
| 👤 **Quản lý Người dùng** | - Đăng ký, Đăng nhập, Đăng xuất.<br>- Quản lý thông tin cá nhân.<br>- Thay đổi mật khẩu, Quên mật khẩu. |
| 👥 **Quản lý Bạn bè** | - Gửi/Chấp nhận/Hủy lời mời kết bạn.<br>- Xem danh sách bạn bè, Hủy kết bạn. |
| 📰 **Bảng tin (News Feed)**| - Đăng bài viết (văn bản, ảnh, video).<br>- Hiển thị bài viết từ bạn bè.<br>- Tương tác: Thích, Bình luận. |
| 💬 **Chat Thời gian thực**| - Chat 1-1 với bạn bè.<br>- **Chat Nhóm:** Tạo nhóm, Mời/Thêm thành viên, Xem thông tin, Rời nhóm.<br>- **Admin Nhóm:** Sửa thông tin, Loại thành viên, Xóa nhóm.<br>- Gửi tệp đính kèm (ảnh, video, file).<br>- Hiển thị tin nhắn theo ngày, kèm thời gian gửi.<br>- Thông báo tin nhắn chưa đọc (tổng quan & chi tiết).<br>- Tự động đánh dấu đã đọc. |
| 🔔 **Thông báo** | - Nhận thông báo (lượt thích, bình luận, kết bạn, tin nhắn mới...). |
| 🔍 **Tìm kiếm** | - Tìm kiếm bạn bè, bài viết. |
| 🛠️ **Quản trị (Admin/Mgr)**| - Quản lý bài đăng (duyệt, xóa).<br>- Quản lý người dùng (khóa, mở khóa).<br>- Xử lý báo cáo vi phạm. |

---

## 🛠️ Công nghệ sử dụng

| **Thành phần** | **Phiên bản / Công nghệ** | **Ghi chú** |
| ------------------ | ---------------------------------------- | ----------- |
| **Backend** | Java 17+, Spring Boot 3+                 | Logic nghiệp vụ, API, Bảo mật, CSDL |
|                    | Spring Web (MVC, REST)                   |             |
|                    | Spring Data JPA (Hibernate)              |             |
|                    | Spring Security (nếu có)                 | Xác thực, Phân quyền |
|                    | Spring WebSocket (STOMP, SockJS)         | Chat real-time |
| **Frontend** | Thymeleaf, Bootstrap 5, JavaScript (ES6+)| Giao diện người dùng |
|                    | SockJS Client, StompJS Client            | Kết nối WebSocket |
| **Database** | MySQL                                    | Lưu trữ dữ liệu |
| **Build Tool** | Apache Maven                             | Quản lý thư viện, Build |
| **Web Server** | Apache Tomcat (Embedded)                 | Chạy ứng dụng web |

---

## 🚀 Hướng dẫn cài đặt (Getting Started)

### 1. Yêu cầu (Prerequisites)
* **JDK:** 17 hoặc mới hơn
* **Maven:** 3.6+
* **MySQL:** 8.0+
* **IDE:** IntelliJ IDEA hoặc Eclipse (tùy chọn)

### 2. Cài đặt (Installation)
```bash
# Clone repository về máy
git clone <URL_REPOSITORY_CUA_BAN>
cd <TEN_THU_MUC_DU_AN>

# Build dự án (tùy chọn, nếu IDE không tự build)
mvn clean install
3. Cấu hình Cơ sở dữ liệu (Database Setup)Tạo một database mới trong MySQL (ví dụ: aloute_db) với collation utf8mb4_unicode_ci.Mở file src/main/resources/application.properties (hoặc .yml).Cập nhật các thuộc tính sau với thông tin database của bạn:Propertiesspring.datasource.url=jdbc:mysql://localhost:3306/aloute_db?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Cấu hình Hibernate (cho phép Spring Boot tự tạo bảng nếu chưa có)
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
spring.jpa.show-sql=true # Hiển thị câu lệnh SQL (tùy chọn)
4. Chạy ứng dụng (Usage)Sử dụng IDE: Mở dự án, tìm class chính (...Application.java) và chạy (Run As Java/Spring Boot Application).Sử dụng Maven:Bashmvn spring-boot:run
Mở trình duyệt và truy cập: http://localhost:8090 (hoặc cổng bạn cấu hình).📂 Cấu trúc thư mục (Project Structure)aloute-social-network/
 ┣ src/main/java/aloute/com
 ┃ ┣ config           # Cấu hình (WebSocket, Security...)
 ┃ ┣ controller       # Xử lý request HTTP (User, Group, Message...)
 ┃ ┣ dto              # Data Transfer Objects
 ┃ ┣ entity           # Các Entity JPA (ánh xạ bảng CSDL)
 ┃ ┣ exception        # Xử lý ngoại lệ tùy chỉnh (nếu có)
 ┃ ┣ repository       # Interfaces Spring Data JPA
 ┃ ┣ scheduler        # Tác vụ định kỳ (vd: gửi tin nhắn hẹn giờ)
 ┃ ┣ service          # Logic nghiệp vụ (Business Logic)
 ┃ ┗ ...Application.java # File chạy chính
 ┣ src/main/resources
 ┃ ┣ static           # Tài nguyên tĩnh (CSS, JS, Images)
 ┃ ┃ ┣ css
 ┃ ┃ ┣ js
 ┃ ┃ ┗ images
 ┃ ┣ templates        # Giao diện Thymeleaf (.html)
 ┃ ┃ ┣ user           # Giao diện người dùng (message, profile...)
 ┃ ┃ ┣ admin          # Giao diện quản trị (nếu có)
 ┃ ┃ ┣ fragments      # Thành phần tái sử dụng (header, footer...)
 ┃ ┃ ┗ layouts        # Layout chính (nếu có)
 ┃ ┗ application.properties # File cấu hình chính
 ┣ uploads            # Thư mục lưu file tải lên (avatar, attachments)
 ┗ pom.xml            # File quản lý thư viện Maven
🚀 Hướng phát triểnTự động hóa kiểm duyệt: Sử dụng AI để phát hiện nội dung vi phạm.Nâng cao hệ thống thông báo: Thêm thông báo đẩy (push notification) và qua email.Xây dựng thuật toán gợi ý (Feed-AI): Cá nhân hóa bảng tin người dùng.Phát triển API: Xây dựng API cho phép ứng dụng bên thứ ba tương tác.

## Đóng góp

| Thành viên          | MSSV     |
| ------------------- | -------- |
| Huỳnh Hoài Bảo      | 23110178 |
| Nguyễn Trọng Phúc    | 23110288 |
| Võ Thanh Nhã        | 23110277 |
| Nguyễn Thành Huy    | 23110227 |

**Giáo viên hướng dẫn:** Nguyễn Hữu Trung
