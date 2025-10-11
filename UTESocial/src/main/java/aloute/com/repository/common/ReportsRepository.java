package aloute.com.repository.common;

import aloute.com.entity.manager.Reports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> 
{
    long countByStatus(String status);
}
