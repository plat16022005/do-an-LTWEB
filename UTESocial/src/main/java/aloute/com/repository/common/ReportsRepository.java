package aloute.com.repository.common;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import aloute.com.entity.Reports;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer> 
{
    long countByStatus(String status);
}
