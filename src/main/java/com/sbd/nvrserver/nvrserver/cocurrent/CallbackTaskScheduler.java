package com.sbd.nvrserver.nvrserver.cocurrent;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author xxf
 */
@Slf4j
public class CallbackTaskScheduler extends Thread {
    // 任务队列
    private ConcurrentLinkedQueue<CallbackTask> executeTaskQueue =
            new ConcurrentLinkedQueue<CallbackTask>();
    // 线程休眠时间
    private long sleepTime = 200;
    private ExecutorService jPool = Executors.newCachedThreadPool();

    ListeningExecutorService gPool = MoreExecutors.listeningDecorator(jPool);


    private static CallbackTaskScheduler inst = new CallbackTaskScheduler();

    private CallbackTaskScheduler() {
        this.start();
    }

    /**
     * 添加任务
     *
     * @param executeTask
     */
    public static <R> void add(CallbackTask<R> executeTask) {
        inst.executeTaskQueue.add(executeTask);
    }

    @Override
    public void run() {
        while (true) {
            handleTask();// 处理任务
            threadSleep(sleepTime);
        }
    }

    private void threadSleep(long time) {
        try {
            sleep(time);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 处理任务队列，检查其中是否有任务
     */
    private void handleTask() {
        try {
            CallbackTask executeTask = null;
            while (executeTaskQueue.peek() != null) {
                executeTask = executeTaskQueue.poll();
                handleTask(executeTask);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 执行任务操作
     *
     * @param executeTask
     */
    private <R> void handleTask(CallbackTask<R> executeTask) {

        ListenableFuture<R> future = gPool.submit(new Callable<R>() {
            @Override
            public R call() throws Exception {

                return executeTask.execute();
            }

        });

        Futures.addCallback(future, new FutureCallback<R>() {
            @Override
            public void onSuccess(R r) {
                executeTask.onBack(r);
            }

            @Override
            public void onFailure(Throwable t) {
                executeTask.onException(t);
            }
        });

    }

}
