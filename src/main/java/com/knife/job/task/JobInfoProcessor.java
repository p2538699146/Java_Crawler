package com.knife.job.task;

import com.knife.job.pojo.JobInfo;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.pipeline.FilePipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.BloomFilterDuplicateRemover;
import us.codecraft.webmagic.scheduler.QueueScheduler;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.List;

@Component
public class JobInfoProcessor implements PageProcessor {

    //爬取目标地址的url
    private String url = "https://search.51job.com/list/080200,000000,2720,00,9,99,%2520,2,1.html?lang=c&stype=&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=99&companysize=99&providesalary=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=&dibiaoid=0&address=&line=&specialarea=00&from=&welfare=";

    //抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me()
            .setCharset("gbk") //设置编码格式
            .setTimeOut(10*1000) //设置超时时间，单位是毫秒
            .setRetrySleepTime(3000) //设置重试时间，单位是毫秒
            .setSleepTime(3) //设置重试次数
            ;

    @Override
    //process是定制爬虫逻辑的核心接口，在这里编写抽取逻辑
    public void process(Page page) {

        //解析页面，获取招聘信息详情的url地址
        List<Selectable> list = page.getHtml().css("div#resultList div.el").nodes();

        //判断获取到的集合是否为空
        if (list.size() == 0) {

            //如果为空，表示获取到的是详情页，解析页面，获取招聘信息，保存数据
            this.saveJobInfo(page);
        } else {

            //删除第一条标题数据
            list.remove(0);

            //如果不为空表示这是列表页,解析出页面的url地址，放到任务队列中
            for (Selectable selectable : list) {

                //获取详情页url的地址
                String jobInfoUrl = selectable.links().toString();

                //将详url地址添加到队列
                page.addTargetRequest(jobInfoUrl);
            }
        }

       /* //获取总页数
        Integer hidTotalPage = Integer.parseInt(page.getHtml().css("input#hidTotalPage", "value").get());
        //获取当前页数
        Integer on = Integer.parseInt(page.getHtml().css("div.p_in ul li.on", "text").get());*/
        //if (on < hidTotalPage) {
        //获取下一页的url
        List<String> bkUrlList = page.getHtml().css("div.p_in li.bk").links().all();
        //把url放到队列中
        page.addTargetRequests(bkUrlList);
    }

    //解析页面，获取招聘信息，保存数据
    private void saveJobInfo(Page page) {
        //创建招聘对象信息
        JobInfo jobInfo = new JobInfo();
        Html html = page.getHtml();

        //获取公司名称
        jobInfo.setCompanyName(html.css("div.cn p.cname a","text").toString());
        //获取公司地址
        jobInfo.setCompanyAddr(Jsoup.parse(html.css("div.bmsg").nodes().get(1).toString()).text());
        //获取公司信息
        String info = Jsoup.parse(html.css("div.tmsg").toString()).text();
        info = (info == null) ? "" : info;
        jobInfo.setCompanyInfo(info);
        //获取职位名称
        jobInfo.setJobName(html.css("div.cn h1","text").toString());

        //杭州  |  在校生/应届生  |  大专  |  招2人  |  04-20发布
        String jobAddr = Jsoup.parse(html.css("div.cn p.msg").toString()).text();
        jobAddr  = jobAddr.substring(0,jobAddr.indexOf("|"));
        //获取工作地址
        jobInfo.setJobAddr(jobAddr);
        //获取职位信息
        jobInfo.setJobInfo(Jsoup.parse(html.css("div.bmsg").nodes().get(0).toString()).text());
        //调用MathSalary.getSalary类处理薪资，salary[0]最低工资，salary[1]最高工资
        Integer[] salary = MathSalary.getSalary(html.css("div.cn strong", "text").toString());
        //获取最少工资
        jobInfo.setSalaryMin(salary[0]);
        //获取最高工资
        jobInfo.setSalaryMax(salary[1]);
        //获取职位详情url
        jobInfo.setUrl(page.getUrl().toString());
        //获取职位发布时间
        String time = Jsoup.parse(html.css("div.cn p.msg").regex("[0-9]{2}[-][0-9]{2}.*").toString()).text();
        time = time.substring(0,time.indexOf("发布"));
        jobInfo.setTime(time);

        //把结果保存
        page.putField("jobInfo",jobInfo);
    }

    @Override
    public Site getSite() {
        return site;
    }

    @Autowired
    private SpringDataPipeline springDataPipeline;

    //initialDelay当前任务启动后，等等多久执行方法
    //fixedDelay每隔多久执行方法
    @Scheduled(initialDelay = 1000, fixedDelay = 100 * 1000)
    public void process() {
        Spider.create(new JobInfoProcessor())
        .addUrl(url) //添加爬取目标url
        .setScheduler(new QueueScheduler()
        .setDuplicateRemover(
                new BloomFilterDuplicateRemover(100000))) //修改过滤器为布隆过滤器，当前过滤10万条数据
        .thread(10) //设置多线程执行
                //.addPipeline(new FilePipeline("")) //添加保存文件路径
                .addPipeline(springDataPipeline) //添加pipeline
                .run();
    }
}
