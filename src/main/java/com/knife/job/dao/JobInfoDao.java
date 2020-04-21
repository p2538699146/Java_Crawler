package com.knife.job.dao;

import com.knife.job.pojo.JobInfo;
import org.springframework.data.jpa.repository.JpaRepository;


public interface JobInfoDao extends JpaRepository<JobInfo, Long> {
}
