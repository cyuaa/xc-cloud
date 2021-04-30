package com.chenyu.cloud.common.thread.refuse;


import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * 自定义线程有界队列
 * Created by JackyChen on 2021/04/29.
 */
@Slf4j
public class AsyncProcessQueueReFuse {

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    /**
     * Task 包装类
     * 此类型的意义是记录可能会被 Executor 吃掉的异常
     */
    public static class TaskWrapper implements Runnable {

       private final Runnable gift;

       public TaskWrapper(final Runnable target) {
       this.gift = target;
      }

       @Override
       public void run() {
          // 捕获异常，避免在 Executor 里面被吞掉了
          if (gift != null) {
             try {
                gift.run();
             } catch (Exception e) {
                String errMsg = StrUtil.format("线程池-包装的目标执行异常: {}", e.getMessage());
                log.error(errMsg, e);
             }
          }
       }
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /**
     * 执行指定的任务
     *
     * @param task
     * @return
     */
    public static boolean execute(final Runnable task) {
        return AsyncProcessorReFuse.executeTask(new TaskWrapper(task));
    }
}
