# Chức năng Chặn Người Dùng (Block User Feature)

## 📋 Tổng quan

Chức năng chặn người dùng cho phép user chặn những người dùng khác trong hệ thống AloUTE Social với các đặc điểm:

- ✅ Không cho phép chặn khi đã là bạn bè
- ✅ Chỉ có 1 người chặn được (ai chặn trước)
- ✅ Khi bị chặn: không xem được bài viết, không tìm kiếm được nhau
- ✅ Vẫn truy cập được profile qua tab "Đã chặn"
- ✅ Có thể bỏ chặn bất kỳ lúc nào

## 🗂️ Cấu trúc File

### Backend (Java)

#### 1. Entity

- **`BlockedUser.java`**: Entity đại diện cho mối quan hệ chặn giữa 2 user
  - `blockId`: ID của record
  - `blocker`: User chặn
  - `blocked`: User bị chặn
  - `blockedAt`: Thời gian chặn

#### 2. Repository

- **`BlockedUserRepository.java`**: Xử lý truy vấn database
  - `findByBlockerAndBlocked()`: Tìm mối quan hệ chặn cụ thể
  - `findBlockRelationship()`: Kiểm tra có mối quan hệ chặn không (bất kể ai chặn ai)
  - `findBlockedUsersByBlocker()`: Lấy danh sách user đã chặn
  - `getBlockedUserIds()`: Lấy danh sách ID để filter nhanh

#### 3. Service

- **`BlockService.java`**: Logic nghiệp vụ
  - `blockUser()`: Chặn người dùng (kiểm tra điều kiện: không phải bạn bè, chưa có ai chặn)
  - `unblockUser()`: Bỏ chặn
  - `isBlocked()`: Kiểm tra A có chặn B không
  - `hasBlockRelationship()`: Kiểm tra có mối quan hệ chặn giữa A và B không
  - `getBlockedUsers()`: Lấy danh sách user đã chặn

#### 4. Controller

- **`UserBlockController.java`**: REST API endpoints
  - `POST /user/block`: Chặn người dùng
  - `POST /user/unblock`: Bỏ chặn người dùng

#### 5. Cập nhật Controllers khác

- **`UserProfileController.java`**: Thêm logic kiểm tra blocked khi hiển thị profile
- **`UserFriendController.java`**: Thêm danh sách blocked users vào trang friend
- **`UserSearchController.java`**: Lọc kết quả tìm kiếm, ẩn user đã chặn
- **`PostsRepository.java`**: Thêm điều kiện NOT EXISTS BlockedUser vào các query

### Frontend (HTML/JavaScript)

#### 1. Giao diện Profile

- **`profile.html`**:
  - Hiển thị nút "Chặn" khi không phải bạn bè
  - Hiển thị nút "Bỏ chặn" khi đã chặn
  - Ẩn tất cả nút khác khi đã chặn hoặc bị chặn
  - JavaScript xử lý gọi API `/user/block` và `/user/unblock`

#### 2. Trang Bạn bè

- **`friend.html`**:
  - Tab mới "Đã chặn" hiển thị danh sách người đã chặn
  - Nút "Bỏ chặn" trong danh sách
  - JavaScript xử lý bỏ chặn

### Database

- **`database_update_blocked_users.sql`**: Script tạo bảng BlockedUsers

## 🚀 Hướng dẫn Cài đặt

### Bước 1: Cập nhật Database

Chạy script SQL để tạo bảng mới:

```bash
mysql -u root -p aloutedb < database_update_blocked_users.sql
```

Hoặc copy nội dung file và chạy trong MySQL Workbench/phpMyAdmin.

### Bước 2: Restart Application

Vì Spring Boot sử dụng `spring.jpa.hibernate.ddl-auto=update`, bảng sẽ tự động được tạo khi khởi động lại ứng dụng.

```bash
# Nếu đang chạy từ IDE: Stop và Run lại
# Nếu đang chạy từ terminal:
mvn spring-boot:run
```

### Bước 3: Test chức năng

1. Đăng nhập 2 tài khoản khác nhau (dùng 2 trình duyệt)
2. Truy cập profile của nhau
3. Thử chặn và kiểm tra:
   - Nút chặn xuất hiện khi không phải bạn bè
   - Sau khi chặn, chỉ còn nút "Bỏ chặn"
   - Không thấy bài viết của nhau trên trang chủ
   - Không tìm kiếm được nhau
   - Vẫn truy cập được profile qua tab "Đã chặn"

## 📖 Luồng hoạt động

### 1. Chặn người dùng

```
User A click "Chặn" trên profile User B
    ↓
JavaScript gọi POST /user/block với {blockedUserId: B.id}
    ↓
BlockController nhận request
    ↓
BlockService.blockUser() kiểm tra:
    - A và B không phải bạn bè? ✓
    - Chưa có ai chặn ai? ✓
    - A ≠ B? ✓
    ↓
Tạo record mới trong BlockedUsers
    ↓
Trả về {success: true}
    ↓
JavaScript reload trang → Hiển thị nút "Bỏ chặn"
```

### 2. Ảnh hưởng sau khi chặn

- **Trang chủ**: Không hiển thị bài viết của B
- **Tìm kiếm**: Không hiển thị B trong kết quả
- **Profile B**: A vẫn truy cập được nhưng:
  - Không thấy bài viết (đã filter trong query)
  - Chỉ thấy nút "Bỏ chặn"
- **Profile A**: B cũng không thấy bài viết của A

### 3. Bỏ chặn

```
User A click "Bỏ chặn"
    ↓
Confirm dialog
    ↓
POST /user/unblock
    ↓
Xóa record trong BlockedUsers
    ↓
Reload → Mọi thứ trở lại bình thường
```

## 🔧 Các điểm quan trọng

### 1. Logic nghiệp vụ

- **Không cho chặn bạn bè**: Phải hủy kết bạn trước
- **Chỉ 1 người chặn**: Nếu B đã chặn A, thì A không thể chặn B nữa
- **Hai chiều ảnh hưởng**: Khi A chặn B, cả A và B đều không thấy bài viết của nhau

### 2. Query optimization

Tất cả các query lấy bài viết đều có điều kiện:

```sql
AND NOT EXISTS (
    SELECT 1 FROM BlockedUser b
    WHERE (b.blocker.userId = :currentUserId AND b.blocked.userId = u.userId)
       OR (b.blocker.userId = u.userId AND b.blocked.userId = :currentUserId)
)
```

### 3. Security

- Kiểm tra session trước khi cho phép block/unblock
- Validate userId để tránh SQL injection
- Không cho phép tự chặn mình (constraint trong DB)

## 🎨 Giao diện

### Nút trên Profile

**Khi chưa chặn:**

```
[Kết bạn] [Nhắn tin] [Báo cáo] [🚫 Chặn]
```

**Khi đã chặn:**

```
[🔓 Bỏ chặn]
```

**Khi bị chặn:**

```
⚠️ Bạn không thể tương tác với người dùng này
```

### Tab "Đã chặn" trong trang Bạn bè

```
┌─────────────────────────────────────┐
│  [Avatar]  Nguyễn Văn A             │
│            @nguyenvana       [Bỏ chặn] │
├─────────────────────────────────────┤
│  [Avatar]  Trần Thị B               │
│            @tranthib         [Bỏ chặn] │
└─────────────────────────────────────┘
```

## 🐛 Troubleshooting

### Lỗi: Không tạo được bảng BlockedUsers

**Nguyên nhân**: Database chưa có bảng, JPA tự tạo nhưng bị lỗi constraint

**Giải pháp**: Chạy script SQL thủ công

### Lỗi: Vẫn thấy bài viết của người đã chặn

**Nguyên nhân**: Query chưa được cập nhật hoặc cache

**Giải pháp**:

1. Restart server
2. Clear cache trình duyệt
3. Kiểm tra query trong PostsRepository

### Lỗi: Không gọi được API /user/block

**Nguyên nhân**: CORS hoặc session timeout

**Giải pháp**: Check console JavaScript, đảm bảo đã login

## 📝 Checklist Test

- [ ] Tạo 2 user: UserA và UserB
- [ ] UserA chặn UserB → Thành công
- [ ] UserA thử chặn lại UserB → Không được (đã chặn rồi)
- [ ] UserB thử chặn UserA → Không được (UserA đã chặn)
- [ ] UserA không thấy UserB trong tìm kiếm
- [ ] UserA không thấy bài viết của UserB trên trang chủ
- [ ] UserA vẫn truy cập được profile UserB
- [ ] UserA thấy UserB trong tab "Đã chặn"
- [ ] UserA bỏ chặn UserB → Mọi thứ trở lại bình thường
- [ ] UserA và UserB kết bạn
- [ ] UserA thử chặn UserB → Không được (đang là bạn bè)

## 💡 Tính năng mở rộng (Nếu cần)

- [ ] Thêm lý do chặn (optional)
- [ ] Thông báo khi bị chặn/bỏ chặn
- [ ] Giới hạn số lượng chặn tối đa
- [ ] Admin có thể xem ai chặn ai
- [ ] Xuất báo cáo số lượng chặn theo thời gian

---

**Ngày tạo**: October 20, 2025  
**Version**: 1.0  
**Developer**: GitHub Copilot + Your Team
