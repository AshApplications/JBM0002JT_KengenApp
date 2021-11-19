package com.water.alkaline.kengen.library.downloader.core;


import java.util.concurrent.Executor;

public interface ExecutorSupplier {

    DownloadExecutor forDownloadTasks();

    Executor forBackgroundTasks();

    Executor forMainThreadTasks();

}
