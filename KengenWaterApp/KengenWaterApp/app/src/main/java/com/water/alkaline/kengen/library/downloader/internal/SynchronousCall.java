package com.water.alkaline.kengen.library.downloader.internal;

import com.water.alkaline.kengen.library.downloader.Response;
import com.water.alkaline.kengen.library.downloader.request.DownloadRequest;

public class SynchronousCall {

    public final DownloadRequest request;

    public SynchronousCall(DownloadRequest request) {
        this.request = request;
    }

    public Response execute() {
        DownloadTask downloadTask = DownloadTask.create(request);
        return downloadTask.run();
    }

}
