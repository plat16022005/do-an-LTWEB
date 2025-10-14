package aloute.com.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "Reports")
public class Reports {
    // Class implementation goes here
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reportId;

    

    @ManyToOne
    @JoinColumn(name = "ReporterID")
    private User reporter;

    @ManyToOne
    @JoinColumn(name = "ReportedID")
    private User reportedUser;

    @Column (name = "Type", length = 50)
    private String type;

    @Column(name = "Reason", length = 500)
    private String reason;

    @Column(name = "CreatedAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "Status", length = 20)
    private String status = "pending"; // pending, in_progress, completed

    @Column(name = "ResolutionStatus", length = 20)
    private String resolutionStatus = "pending"; // pending, resolved, rejected

    @ManyToOne
    @JoinColumn(name = "ResolvedBy")
    private User resolvedBy; // User có vai trò là Manager

    @Column(name = "ResolvedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime resolvedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getter and setter

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
	}

	public User getReporter() {
		return reporter;
	}

	public void setReporter(User reporter) {
		this.reporter = reporter;
	}

	public User getReportedUser() {
		return reportedUser;
	}

	public void setReportedUser(User reportedUser) {
		this.reportedUser = reportedUser;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getResolutionStatus() {
		return resolutionStatus;
	}

	public void setResolutionStatus(String resolutionStatus) {
		this.resolutionStatus = resolutionStatus;
	}

	public User getResolvedBy() {
		return resolvedBy;
	}

	public void setResolvedBy(User resolvedBy) {
		this.resolvedBy = resolvedBy;
	}

	public LocalDateTime getResolvedAt() {
		return resolvedAt;
	}

	public void setResolvedAt(LocalDateTime resolvedAt) {
		this.resolvedAt = resolvedAt;
	}

    // Getters and Setters
    
    
    

}
