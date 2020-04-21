package com.knife.job.service;

import com.knife.job.pojo.JobInfo;

import java.util.List;

public interface JobInfoService {

    /**
     * 保存工作信息
     * @param jobInfo
     */
    public void save(JobInfo jobInfo);

    /**
     * 根据条件查询工作信息
     * @param jobInfo
     * @return
     */
    public List<JobInfo> findJobInfo(JobInfo jobInfo);
}
