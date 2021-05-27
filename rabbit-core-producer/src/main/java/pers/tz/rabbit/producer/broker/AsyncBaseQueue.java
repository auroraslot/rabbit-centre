package pers.tz.rabbit.producer.broker;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * @Author twz
 * @Date 2021-05-12
 * @Desc TODO
 */
@Slf4j
public class AsyncBaseQueue {

    private static final int THREAD_SIZE = Runtime.getRuntime().availableProcessors();

    private static final int QUEUE_SIZE = 10000;

    private static ExecutorService senderAsync =
            new ThreadPoolExecutor(THREAD_SIZE,
                    THREAD_SIZE * 2,
                    60L,
                    TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(QUEUE_SIZE),
                    runnable -> {
                        Thread thread = new Thread(runnable);
                        thread.setName("rabbitmq_client_async_sender");
                        return thread;
                    }, (r, executor) -> log.error("async sender is error rejected, runnable: {}, executor: {}", r, executor));


    public static void submit(Runnable runnable) {
        senderAsync.submit(runnable);
    }

}
