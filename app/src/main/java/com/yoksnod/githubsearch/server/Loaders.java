package com.yoksnod.githubsearch.server;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public enum Loaders {
    SEARCH_PROFILES_ON_SERVER(0x65),
    SEARCH_PROFILES_IN_CACHE(0x67);

    private int mId;

    Loaders(int id) {
        mId = id;
    }

    public int getId() {
        return mId;
    }
}
