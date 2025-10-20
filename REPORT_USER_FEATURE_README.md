# 📋 Chức năng Báo cáo Người dùng - Hướng dẫn

## ✅ Tổng quan

Chức năng **Báo cáo Người dùng** đã được bổ sung hoàn chỉnh vào hệ thống UTESocial, cho phép người dùng báo cáo các hành vi vi phạm của người dùng khác.

---

## 🎯 Các thành phần đã triển khai

### 1️⃣ **Entity: Reports**

- ✅ Đã có sẵn, hỗ trợ cả báo cáo **bài viết** và **người dùng**
- Các trường quan trọng:
  - `reportedUser` - Người dùng bị báo cáo
  - `reporter` - Người thực hiện báo cáo
  - `type` - Loại báo cáo: "post" hoặc "user"
  - `reason` - Lý do báo cáo
  - `status` - Trạng thái xử lý
  - `resolutionStatus` - Kết quả xử lý

### 2️⃣ **Repository: ReportsRepository**

**Đã thêm method mới:**

```java
List<Reports> findByReportedUserAndReporter(User reportedUser, User reporter);
```

- Kiểm tra người dùng đã báo cáo người khác chưa
- Tránh báo cáo trùng lặp

### 3️⃣ **Service: ReportService**

**Đã thêm method:**

```java
public Reports createUserReport(String reportedUsername, Integer reporterId, String reason)
```

**Các validation:**

- ✅ Kiểm tra người dùng tồn tại
- ✅ Không cho phép tự báo cáo bản thân
- ✅ Kiểm tra báo cáo trùng lặp
- ✅ Tự động set type = "user"

### 4️⃣ **Controller: ReportController**

**Endpoint mới:**

```
POST /report-user
Parameters:
  - username: Tên người dùng bị báo cáo (e.g., @tuandeptrai)
  - reason: Lý do báo cáo
```

**Response:**

- ✅ Success (200): "Báo cáo người dùng đã được gửi thành công..."
- ❌ Error (400): Thông báo lỗi cụ thể
- ❌ Unauthorized (401): "Bạn cần đăng nhập để báo cáo"

### 5️⃣ **Frontend**

**UI đã có sẵn trong `profile.html`:**

- Modal form `#reportUserModal`
- Nút "Báo cáo" trong menu dropdown

**JavaScript xử lý (`report.js`):**

- ✅ Submit form qua AJAX
- ✅ Hiển thị toast notification
- ✅ Xử lý lỗi validation
- ✅ Tự động đóng modal sau khi gửi

---

## 🚀 Cách sử dụng

### **Người dùng thường:**

1. Vào trang profile của người dùng muốn báo cáo
2. Click vào menu "⋮" (3 chấm)
3. Chọn "🚩 Báo cáo"
4. Nhập lý do báo cáo
5. Click "Gửi báo cáo"

### **Admin:**

Xem và xử lý báo cáo tại:

```
GET /admin/reports
```

Admin có thể:

- ✅ Xem tất cả báo cáo (bài viết + người dùng)
- ✅ Phân loại theo type
- ✅ Xử lý: Chấp nhận hoặc từ chối
- ✅ Xem chi tiết: Người báo cáo, người bị báo cáo, lý do

---

## 📊 Luồng xử lý

```
User → Click "Báo cáo"
     → Nhập lý do
     → Submit form
     → POST /report-user
     → Validation (đăng nhập? trùng lặp? tự báo mình?)
     → Lưu vào DB (type="user", status="pending")
     → Response success
     → Hiển thị toast notification
     → Admin xem tại /admin/reports
     → Admin xử lý (resolve/reject)
```

---

## 🔒 Bảo mật & Validation

### ✅ Đã kiểm tra:

- Người dùng phải đăng nhập
- Không cho phép tự báo cáo bản thân
- Không cho phép báo cáo trùng lặp
- Kiểm tra người dùng bị báo cáo tồn tại
- HTML encode để tránh XSS

---

## 📝 Database Schema

### Bảng `Reports`

```sql
ReportID (PK)
PostID (FK, nullable)          -- NULL nếu báo cáo user
ReporterID (FK)                -- Người báo cáo
ReportedID (FK)                -- Người bị báo cáo
Type                           -- "post" hoặc "user"
Reason                         -- Lý do
Status                         -- "pending", "in_progress", "completed"
ResolutionStatus               -- "pending", "resolved", "rejected"
ResolvedBy (FK)               -- Admin xử lý
CreatedAt
ResolvedAt
```

---

## 🧪 Test Cases

### ✅ Test 1: Báo cáo thành công

```
Input: username = @tuandeptrai, reason = "Spam"
Expected: Success message, record created with type="user"
```

### ✅ Test 2: Tự báo cáo bản thân

```
Input: Báo cáo chính mình
Expected: Error "Bạn không thể báo cáo chính mình"
```

### ✅ Test 3: Báo cáo trùng

```
Input: Báo cáo cùng user 2 lần
Expected: Error "Bạn đã báo cáo người dùng này rồi"
```

### ✅ Test 4: Chưa đăng nhập

```
Input: Session không có user
Expected: 401 Unauthorized
```

### ✅ Test 5: User không tồn tại

```
Input: username = @khongtontai
Expected: Error "Không tìm thấy người dùng"
```

---

## 🎨 UI/UX

### Modal báo cáo:

- Tiêu đề: "Báo cáo người dùng"
- Input: Textarea để nhập lý do
- Button: "Gửi báo cáo" (màu đỏ - danger)
- Toast notification sau khi gửi (3 giây)

### Vị trí:

- Trang profile → Menu dropdown → "🚩 Báo cáo"

---

## 🔧 Maintenance

### Để thêm loại báo cáo mới:

1. Thêm constant trong Entity
2. Cập nhật validation trong Service
3. Thêm UI tương ứng

### Để xem thống kê:

```java
// Đếm số báo cáo người dùng
reportRepository.count("user");

// Lấy báo cáo chưa xử lý
reportRepository.findByTypeAndStatus("user", "pending");
```

---

## ✅ Checklist hoàn thành

- [x] Entity hỗ trợ báo cáo user
- [x] Repository method mới
- [x] Service createUserReport
- [x] Controller endpoint /report-user
- [x] JavaScript xử lý AJAX
- [x] UI modal form
- [x] Validation đầy đủ
- [x] Toast notification
- [x] Admin xem và xử lý
- [x] Test cases
- [x] Documentation

---

## 📞 Liên hệ

Nếu có vấn đề, vui lòng kiểm tra:

1. Console log trong browser (F12)
2. Server log trong terminal
3. Database để xem record đã được tạo chưa

**Status:** ✅ Hoàn thành và sẵn sàng sử dụng!
