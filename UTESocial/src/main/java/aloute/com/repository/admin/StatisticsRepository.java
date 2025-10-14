package aloute.com.repository.admin;

import org.springframework.data.jpa.repository.JpaRepository;

import aloute.com.entity.Statistics;

public interface StatisticsRepository extends JpaRepository<Statistics, Integer> {
}