package com.water.alkaline.kengen.library.downloader.core;

import com.water.alkaline.kengen.library.downloader.internal.DownloadRunnable;

import java.util.concurrent.Future;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class DownloadExecutor extends ThreadPoolExecutor {

    DownloadExecutor(int maxNumThreads, ThreadFactory threadFactory) {
        super(maxNumThreads, maxNumThreads, 0, TimeUnit.MILLISECONDS,
                new PriorityBlockingQueue<Runnable>(), threadFactory);
    }

    @Override
    public Future<?> submit(Runnable task) {
        DownloadFutureTask futureTask = new DownloadFutureTask((DownloadRunnable) task);
        execute(futureTask);
        return futureTask;
    }
}
