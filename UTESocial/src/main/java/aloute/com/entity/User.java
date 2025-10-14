package aloute.com.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Users")
public class User 
{
    public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	
	public String getPasswordHash() {
		return passwordHash;
	}
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}

	
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}

	
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	
	public Boolean getIsActivated() {
		return isActivated;
	}
	public void setIsActivated(Boolean isActivated) {
		this.isActivated = isActivated;
	}

	
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}
	
	
	public Boolean getIsLocked() {
        return isLocked;
    }
    public void setIsLocked(Boolean isLocked) {
        this.isLocked = isLocked;
    }

    
    public String getLockedReason() {
        return lockedReason;
    }
    public void setLockedReason(String lockedReason) {
        this.lockedReason = lockedReason;
    }

    
    public LocalDateTime getLockedAt() {
        return lockedAt;
    }
    public void setLockedAt(LocalDateTime lockedAt) {
        this.lockedAt = lockedAt;
    }
    public String getNameUser() {
		return nameUser;
	}
	public void setNameUser(String nameUser) {
		this.nameUser = nameUser;
	}

    

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserID")
    private Integer userId;

    @Column(name = "Email", nullable = false, unique = true, length = 255)
    @NotNull(message = "Email không được để trống")
    @Size(max = 255, message = "Email không được vượt quá 255 ký tự")
    private String email;

    @Column(name = "PasswordHash", nullable = false, length = 255)
    @NotNull(message = "Mật khẩu không được để trống")
    @Size(min = 8, message = "Mật khẩu phải ít nhất 8 ký tự")
    private String passwordHash;
    

	@Column(name = "NameUser", length = 50)
    private String nameUser;
    
    @Column(name = "FullName", length = 100)
    private String fullName;

    @Column(name = "Location", length = 100)
    private String location;

    @Column(name = "Birthday")
    private LocalDate birthday;

    @Column(name = "Gender", length = 20)
    private String gender;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "Avatar", length = 255)
    private String avatar;

    @Column(name = "IsActivated")
    private Boolean isActivated = true;

    @Column(name = "Role", length = 20)
    private String role;
    
    
    @Column(name = "IsLocked")
    private Boolean isLocked = false;

    @Column(name = "LockedReason")
    private String lockedReason;

    @Column(name = "LockedAt")
    private LocalDateTime lockedAt;
 // Trả về đường dẫn hợp lệ cho frontend
    @jakarta.persistence.Transient // Không lưu cột này vào DB
    public String getAvatarUrl() {
        if (avatar == null || avatar.isBlank()) {
            // fallback ảnh mặc định nếu user chưa có avatar
            return "/uploads/avatars/default.jpg";
        }

        // Nếu thiếu dấu "/" ở đầu → thêm vào
        if (!avatar.startsWith("/")) {
            return "/" + avatar;
        }

        return avatar;
    }

}
