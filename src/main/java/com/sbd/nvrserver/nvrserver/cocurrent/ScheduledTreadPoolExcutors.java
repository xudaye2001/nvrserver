package com.sbd.nvrserver.nvrserver.cocurrent;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;

@Data
@Service(value = "ScheduledTreadPoolExcutors" )
public class ScheduledTreadPoolExcutors {

    public static ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;


    public void init() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setDaemon(true).setNameFormat("ScheduledThreadPoolExecutor-pool").build();
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(2,threadFactory, new ThreadPoolExecutor.CallerRunsPolicy());
    }
}
