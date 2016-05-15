package com.yoksnod.githubsearch.database;

import android.content.Context;

import com.yoksnod.githubsearch.server.AbstractGetProfilesLoader;
import com.yoksnod.githubsearch.server.ProfilesAndTotalCount;
import com.yoksnod.githubsearch.server.RequestResult;
import com.yoksnod.githubsearch.ui.GithubApplication;
/**
 * Created by d.donskoy on 14.05.2016.
 */
public class FromCacheProfilesLoader extends AbstractGetProfilesLoader<RequestResult<ProfilesAndTotalCount>> {

    private final String mQuery;

    public FromCacheProfilesLoader(Context context, String query) {
        super(context.getApplicationContext());
        mQuery = query;
    }

    @Override
    public RequestResult<ProfilesAndTotalCount> loadInBackground() {
        SelectProfilesCommand cmd = new SelectProfilesCommand(getContext(), mQuery);
        RequestResult<ProfilesAndTotalCount> result = ((GithubApplication) getContext())
                .getScheduler()
                .executeDbRequestBlocking(cmd);


        return result;
    }
}
