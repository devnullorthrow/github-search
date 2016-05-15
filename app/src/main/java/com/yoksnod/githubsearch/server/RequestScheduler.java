package com.yoksnod.githubsearch.server;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class RequestScheduler {

    private final ExecutorService mDbExecutor = Executors.newFixedThreadPool(1);
    private final ExecutorService mNetworkExecutor = Executors.newFixedThreadPool(4);

    public Executor getDbExecutor() {
        return mDbExecutor;
    }

    public ExecutorService getNetworkExecutor() {
        return mNetworkExecutor;
    }

    public <Data> RequestResult<Data> executeDbRequestBlocking(final AbstractRequest<?, RequestResult<Data>> cmd) {
        return executeRequestBlocking(cmd, mDbExecutor);
    }

    public <Data> RequestResult<Data> executeNetworkRequestBlocking(final AbstractRequest<?, RequestResult<Data>> cmd) {
        return executeRequestBlocking(cmd, mNetworkExecutor);
    }

    public <Data> RequestResult<Data> executeRequestBlocking(final AbstractRequest<?, RequestResult<Data>> cmd, ExecutorService executor){
        final Callable<RequestResult<Data>> task = new Callable<RequestResult<Data>>() {
            @Override
            public RequestResult<Data> call() throws Exception {
                return cmd.execute();
            }
        };
        Future<RequestResult<Data>> future = executor.submit(task);
        try {
            return future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return new RequestResult.InterruptedException<Data>(e);
        } catch (ExecutionException e) {
            e.printStackTrace();
            return new RequestResult.ExecutionException<Data>(e);
        }
    }
}
