package com.yoksnod.githubsearch.server;

import android.content.Context;
import android.support.annotation.StringRes;

import com.yoksnod.githubsearch.R;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class RequestResult<T> {

    private final T mData;
    private final ErrorHolder mHolder;

    public RequestResult(T data) {
        this(data, null);
    }

    public RequestResult(Throwable throwable){
        this(null, new ErrorHolder(throwable, R.string.error_loading));
    }

    private RequestResult(T data, ErrorHolder holder) {
        mData = data;
        mHolder = holder;
    }

    public ErrorHolder getException() {
        return mHolder;
    }

    public T getData() {
        return mData;
    }

    public static <T> boolean isOk(RequestResult<T> result){
        return result.mHolder == null;
    }

    private static class BaseError<T> extends RequestResult<T> {

        public BaseError(Throwable exception) {
            this(new ErrorHolder(exception, R.string.error_loading));
        }

        public BaseError(ErrorHolder holder){
            super(null, holder);
        }
    }

    public static class ExecutionException<T> extends BaseError<T> {

        public ExecutionException(Throwable exception) {
            super(exception);
        }
    }

    public static class InterruptedException<T> extends BaseError<T> {

        public InterruptedException(Throwable exception) {
            super(exception);
        }
    }

    public static class ServerError<T> extends BaseError<T> {

        public ServerError(Throwable exception) {
            super(exception);
        }
    }


    public static class RateLimitExceedError<T> extends BaseError<T> {

        public RateLimitExceedError(Throwable exception) {
            super(new ErrorHolder(exception, R.string.rate_limit_exceeded));
        }
    }

    public static class ErrorHolder extends Throwable {
        private final Throwable mError;
        @StringRes
        private final int mErrorMsgId;

        public ErrorHolder(Throwable error, int errorMsgId) {
            mError = error;
            mErrorMsgId = errorMsgId;
        }

        public Throwable getError() {
            return mError;
        }

        @StringRes
        public int getErrorMsgId() {
            return mErrorMsgId;
        }

        public String toString(Context context) {
            return context.getString(getErrorMsgId());
        }
    }
}
