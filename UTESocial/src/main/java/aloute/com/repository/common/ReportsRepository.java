package aloute.com.repository.common;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Posts;
import aloute.com.entity.Reports;
import aloute.com.entity.User;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> 
{
    long countByStatus(String status);
    List<Reports> findByPostAndReporter(Posts post, User reporter);
    List<Reports> findByReportedUserAndReporter(User reportedUser, User reporter);
    List<Reports> findByReportedUserAndReporterAndType(User reportedUser, User reporter, String type);
    List<Reports> findByStatus(String status);
    List<Reports> findByResolutionStatus(String resolutionStatus);
}
