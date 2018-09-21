package com.yoksnod.githubsearch.ui;

import android.app.Application;

import com.yoksnod.githubsearch.server.RequestScheduler;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class GithubApplication extends Application {

    private RequestScheduler mScheduler;

    @Override
    public void onCreate() {
        super.onCreate();
        mScheduler = new RequestScheduler();
    }

    public RequestScheduler getScheduler() {
        return mScheduler;
    }
}
