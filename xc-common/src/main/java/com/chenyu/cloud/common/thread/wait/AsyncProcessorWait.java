package com.chenyu.cloud.common.thread.wait;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * 自定义线程执行器 - 等待线程执行完毕不拒绝
 */
public class AsyncProcessorWait {

    private static final Logger log = LoggerFactory.getLogger(AsyncProcessorWait.class);

    /**
     * 默认最大并发数<br>
     */
    private static final int DEFAULT_MAX_CONCURRENT = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 线程池名称格式
     */
    private static final String THREAD_POOL_NAME = "ExternalConvertProcessPool-Wait-%d";

    /**
     * 线程工厂名称
     */
    private static final ThreadFactory FACTORY = new BasicThreadFactory.Builder()
            .namingPattern(THREAD_POOL_NAME)
            .daemon(true).build();

    /**
     * 默认队列大小
     */
    private static final int DEFAULT_SIZE = 500;

    /**
     * 默认线程等待时间 秒
     */
    private static final int DEFAULT_WAIT_TIME = 99999;

    /**
     * 默认线程存活时间
     */
    private static final long DEFAULT_KEEP_ALIVE = 60L;

    /**NewEntryServiceImpl.java:689
     * Executor
     */
    private static final ExecutorService EXECUTOR;

    /**
     * 执行队列
     */
    private static final BlockingQueue<Runnable> EXECUTOR_QUEUE = new ArrayBlockingQueue<>(DEFAULT_SIZE);

    static {
        // 创建 Executor
        // 此处默认最大值改为处理器数量的 4 倍
        try {
           EXECUTOR = new ThreadPoolExecutor(DEFAULT_MAX_CONCURRENT, DEFAULT_MAX_CONCURRENT * 4, DEFAULT_KEEP_ALIVE,
               TimeUnit.SECONDS, EXECUTOR_QUEUE, FACTORY);
           // 这里不会自动关闭线程， 当线程超过阈值时 抛异常
            // 关闭事件的挂钩
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                log.info("AsyncProcessorWait 异步处理器关闭");

                EXECUTOR.shutdown();

                try {
                    // 等待1秒执行关闭
                    if (!EXECUTOR.awaitTermination(DEFAULT_WAIT_TIME, TimeUnit.SECONDS)) {
                        log.error("AsyncProcessorWait 由于等待超时，异步处理器立即关闭");
                        EXECUTOR.shutdownNow();
                    }
                } catch (InterruptedException e) {
                    log.error("AsyncProcessorWait 异步处理器关闭中断");
                    EXECUTOR.shutdownNow();
                }

                log.info("AsyncProcessorWait 异步处理器关闭完成");
            }));
        } catch (Exception e) {
            log.error("AsyncProcessorWait 异步处理器初始化错误", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * 此类型无法实例化
     */
    private AsyncProcessorWait() {
    }

    /**
     * 执行任务，不管是否成功<br>
     * 其实也就是包装以后的 {@link } 方法
     *
     * @param task
     * @return
     */
    public static boolean executeTask(Runnable task) {
        try {
            EXECUTOR.execute(task);
        } catch (RejectedExecutionException e) {
            log.error("AsyncProcessorWait 执行任务被拒绝", e);
            return false;
        }
        return true;
    }

    /**
     * 提交任务，并可以在稍后获取其执行情况<br>
     * 当提交失败时，会抛出 {@link }
     *
     * @param task
     * @return
     */
    public static <T> Future<T> submitTask(Callable<T> task) {
        try {
            return EXECUTOR.submit(task);
        } catch (RejectedExecutionException e) {
            log.error("AsyncProcessorWait 执行任务被拒绝", e);
            throw new UnsupportedOperationException("AsyncProcessorWait 无法提交任务，已被拒绝", e);
        }
    }
}
