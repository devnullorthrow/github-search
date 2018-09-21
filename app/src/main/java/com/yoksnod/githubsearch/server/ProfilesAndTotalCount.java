package com.yoksnod.githubsearch.server;

import com.yoksnod.githubsearch.database.entity.Profile;

import java.util.List;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class ProfilesAndTotalCount {

    private final List<Profile> mProfiles;
    private final long mTotalCount;

    public ProfilesAndTotalCount(List<Profile> profiles, long totalCount) {
        mProfiles = profiles;
        mTotalCount = totalCount;
    }

    public long getTotalCount() {
        return mTotalCount;
    }

    public List<Profile> getProfiles() {
        return mProfiles;
    }
}
