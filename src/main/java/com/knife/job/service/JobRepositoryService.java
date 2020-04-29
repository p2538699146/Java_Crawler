package com.knife.job.service;

import com.knife.job.pojo.JobInfoField;
import com.knife.job.pojo.JobResult;

import java.util.List;

public interface JobRepositoryService {

    /**
     * 保存一条数据
     * @param jobInfoField
     */
    public void save(JobInfoField jobInfoField);

    /**
     * 保存多条数据
     * @param fieldList
     */
    public void saveAll(List<JobInfoField> fieldList);

    /**
     * 根据条件分页查询数据
     * @param salary
     * @param page
     * @param jobaddr
     * @param keyword
     * @return
     */
    JobResult search(String salary, Integer page, String jobaddr, String keyword);
}
