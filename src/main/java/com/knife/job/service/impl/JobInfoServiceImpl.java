package com.knife.job.service.impl;

import com.knife.job.dao.JobInfoDao;
import com.knife.job.pojo.JobInfo;
import com.knife.job.service.JobInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobInfoServiceImpl implements JobInfoService {

    @Autowired
    private JobInfoDao jobInfoDao;

    @Override
    @Transactional  //数据库注解，保持事务的一致性
    public void save(JobInfo jobInfo) {
        //根据url和发布时间查询数据
        JobInfo param = new JobInfo();
        param.setUrl(jobInfo.getUrl());
        param.setTime(jobInfo.getTime());

        //执行查询
        List<JobInfo> jobInfoList = this.findJobInfo(param);

        //判断结果是否为空
        if (jobInfoList.size() == 0) {
            //如果查询结果为空，表示招聘信息不存在，或者已经更新了，需要新增或者更新数据库
            this.jobInfoDao.saveAndFlush(jobInfo);
        }
    }

    /**
     * 分页查询数据
     * @param jobInfo
     * @return
     */
    @Override
    public List<JobInfo> findJobInfo(JobInfo jobInfo) {
        //定义返回数据的list
        List<JobInfo> list = null;
        //判断jobInfo
        if (jobInfo != null && jobInfo.toString().length() > 0) {
            //设置查询条件
            Example example = Example.of(jobInfo);
            //执行查询
            list = this.jobInfoDao.findAll(example);
        } else {
            throw new RuntimeException("查询条件为空！jobInfo：" + jobInfo);
        }
        //返回查询到的数据
        return list;
    }

    @Override
    public Page<JobInfo> findJobInfoByPage(int page, int rows) {
        Page<JobInfo> jobInfoPage = this.jobInfoDao.findAll(PageRequest.of(page - 1, rows));
        return jobInfoPage;
    }
}
