package com.yoksnod.githubsearch.server;

import android.content.Context;

import com.yoksnod.githubsearch.database.MergeProfilesCommand;
import com.yoksnod.githubsearch.database.entity.Profile;
import com.yoksnod.githubsearch.ui.GithubApplication;

import java.util.List;

/**
 * Created by 1 on 13.05.2016.
 */
public class GetProfilesLoader extends AbstractGetProfilesLoader<RequestResult<ProfilesAndTotalCount>> {

    public static final int ID = Loaders.SEARCH_PROFILES_ON_SERVER.getId();

    private final String mSearchQuery;

    public GetProfilesLoader(Context context, String searchQuery) {
        super(context.getApplicationContext());
        mSearchQuery = searchQuery;
    }

    @Override
    public RequestResult<ProfilesAndTotalCount> loadInBackground() {
        final RequestScheduler scheduler = ((GithubApplication) getContext()).getScheduler();
        SearchGithubProfilesCommand cmd = new SearchGithubProfilesCommand(mSearchQuery);
        RequestResult<ProfilesAndTotalCount> requestResult = scheduler.executeNetworkRequestBlocking(cmd);

        final ProfilesAndTotalCount data = requestResult.getData();

        if (requestResult.isOk() && !data.getProfiles().isEmpty()) {
            MergeProfilesCommand mergeCmd = new MergeProfilesCommand(getContext(), data.getProfiles());
            RequestResult<List<Profile>> mergeResult = scheduler.executeDbRequestBlocking(mergeCmd);
            final long totalCount = data.getTotalCount();
            return mergeResult.isOk()
                    ? new RequestResult<ProfilesAndTotalCount>(new ProfilesAndTotalCount(mergeResult.getData(), totalCount))
                    : new RequestResult<ProfilesAndTotalCount>(mergeResult.getException());


        }
        return requestResult;

    }
}
