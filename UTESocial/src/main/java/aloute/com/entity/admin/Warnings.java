/*package aloute.com.entity.admin;

import aloute.com.entity.Users;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "Warnings")
public class Warnings 
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer warningId;

    @ManyToOne
    @JoinColumn(name = "UserID", nullable = false)
    private Users user;

    @Column(name = "Reason")
    private String reason;

    @Column(name = "Type")
    private String type;

    @Column(name = "Count", nullable = false)
    private Integer count = 1;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "IssuedBy")
    private Users issuedBy;

    @Column(name = "Status", nullable = false)
    private String status = "active";






	public Integer getWarningId() {
		return warningId;
	}

	public void setWarningId(Integer warningId) {
		this.warningId = warningId;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Users getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(Users issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
*/