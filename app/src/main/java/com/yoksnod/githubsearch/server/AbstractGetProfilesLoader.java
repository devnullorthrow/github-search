package com.yoksnod.githubsearch.server;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
/**
 * Created by d.donskoy on 14.05.2016.
 */
public abstract class AbstractGetProfilesLoader<T> extends AsyncTaskLoader<T> {

    @Nullable
    private T mProfiles;

    public AbstractGetProfilesLoader(Context context) {
        super(context.getApplicationContext());
    }

    @Override
    public void deliverResult(T data) {
        if (isReset()) {
            if (data != null) {
                onComplete(data);
            }
        }

        mProfiles = data;

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        if (mProfiles == null){
            forceLoad();
        }else {
            deliverResult(mProfiles);
        }
    }

    @Override
    public void onCanceled(T result) {
        super.onCanceled(result);
        onComplete(result);
    }

    protected void onComplete(T result) {
    }


    @Override
    protected void onReset() {
        super.onReset();

        onStopLoading();

        if (mProfiles != null) {
            onComplete(mProfiles);
            mProfiles = null;
        }
    }

    @Nullable
    protected T getProfiles() {
        return mProfiles;
    }
}
