package com.water.alkaline.kengen.library.downloader.core;


import com.water.alkaline.kengen.library.downloader.Priority;
import com.water.alkaline.kengen.library.downloader.internal.DownloadRunnable;

import java.util.concurrent.FutureTask;

public class DownloadFutureTask extends FutureTask<DownloadRunnable> implements Comparable<DownloadFutureTask> {

    private final DownloadRunnable runnable;

    DownloadFutureTask(DownloadRunnable downloadRunnable) {
        super(downloadRunnable, null);
        this.runnable = downloadRunnable;
    }

    @Override
    public int compareTo(DownloadFutureTask other) {
        Priority p1 = runnable.priority;
        Priority p2 = other.runnable.priority;
        return (p1 == p2 ? runnable.sequence - other.runnable.sequence : p2.ordinal() - p1.ordinal());
    }
}
