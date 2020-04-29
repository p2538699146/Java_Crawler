package com.knife.job.controller;

import com.knife.job.pojo.JobResult;
import com.knife.job.service.JobRepositoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {


    @Autowired
    private JobRepositoryService jobRepositoryService;

    //Request URL: http://127.0.0.1:8080/search
    //Request Method: POST
    //salary: 10-15
    //page: 1
    //jobaddr: 杭州
    //keyword: 自动化

    /**
     * 根据条件分页查询招聘信息
     *
     * @param salary
     * @param page
     * @param jobaddr
     * @param keyword
     * @return
     */
    @RequestMapping(value = "search", method = RequestMethod.POST)
    public JobResult search(String salary, Integer page, String jobaddr, String keyword) {
        JobResult jobResult = jobRepositoryService.search(salary, page, jobaddr, keyword);

        return jobResult;
    }

}
