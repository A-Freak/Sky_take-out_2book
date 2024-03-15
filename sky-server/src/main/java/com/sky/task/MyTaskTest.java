package com.sky.task;


import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class MyTaskTest {

    // @Scheduled(cron = "0/10 * * * * ?")
    public  void test(){
        log.info("执行test定时任务，{}", new Date());
    }
}
