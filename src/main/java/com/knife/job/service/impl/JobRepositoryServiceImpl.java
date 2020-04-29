package com.knife.job.service.impl;


import com.knife.job.dao.JobRepository;
import com.knife.job.pojo.JobInfoField;
import com.knife.job.pojo.JobResult;
import com.knife.job.service.JobRepositoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class JobRepositoryServiceImpl implements JobRepositoryService {


    @Autowired
    private JobRepository jobRepository;

    @Override
    public void save(JobInfoField jobInfoField) {
        //保存
        this.jobRepository.save(jobInfoField);
    }

    @Override
    public void saveAll(List<JobInfoField> fieldList) {
        //全部保存
        this.jobRepository.saveAll(fieldList);
    }

    //salary: 10-15
    //page: 1
    //jobaddr: 杭州
    //keyword: 自动化
    @Override
    public JobResult search(String salary, Integer page, String jobaddr, String keyword) {
        //解析参数薪资
        String[] salarys = salary.split("-");

        int salaryMin = 0, salaryMax = 0;

        //获取最低工资
        if ("*".equals(salarys[0])) {
            //如果最低薪资是*，那就代表是0
        } else {
            //如果最低薪资不是*，就转换为数字，再乘10000
            salaryMin = Integer.parseInt(salarys[0]) * 10000;
        }
        //获取最高薪资
        if ("*".equals(salarys[1])) {
            //如果最高工资是*，那就代表最大数也包含。设置为1000万
            salaryMax = 10000000;
        } else {
            //如果最高薪资不是*，就转换为数字，再乘10000
            salaryMax = Integer.parseInt(salarys[1]) * 10000;
        }

        //判断工作地点是否为空
        if (StringUtils.isBlank(jobaddr)) {
            //如果为空设置为*
            jobaddr = "*";
        }

        //判断查询关键字是否为空
        if (StringUtils.isBlank(keyword)) {
            //如果为空设置为*
            keyword = "*";
        }

        //利用dao方法进行查询
        Page<JobInfoField> pages = jobRepository.findBySalaryMinBetweenAndSalaryMaxBetweenAndJobAddrAndJobNameAndJobInfo(
                salaryMin, salaryMax, salaryMin, salaryMax, jobaddr, keyword, keyword, PageRequest.of(page - 1, 30)
        );

        //封装结果对象JobResult
        JobResult jobResult = new JobResult();

        //设置结果集
        jobResult.setRows(pages.getContent());
        //设置总页数
        jobResult.setPageTotal(pages.getTotalPages());
        return jobResult;
    }

}
