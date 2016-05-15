package com.yoksnod.githubsearch.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yoksnod.githubsearch.database.FromCacheProfilesLoader;
import com.yoksnod.githubsearch.server.ProfilesAndTotalCount;
import com.yoksnod.githubsearch.database.entity.Profile;
import com.yoksnod.githubsearch.server.RequestResult;
import com.yoksnod.githubsearch.server.GetProfilesLoader;
import com.yoksnod.githubsearch.R;
import com.yoksnod.githubsearch.server.Loaders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by 1 on 13.05.2016.
 */
public class GithubProfilesFragment extends Fragment implements LoaderManager.LoaderCallbacks<RequestResult<ProfilesAndTotalCount>> {

    private static final String BUNDLE_SEARCH_QUERY = "bundle_search_query";
    private static final int START_SEARCH_MSG_ID = 0x520;
    private static final long ONE_SECOND = 1000;

    private RecyclerView mRecyclerView;
    private TextView mEmptyView;
    private EditText mSearchView;
    private GithubProfilesAdapter mAdapter;

    private ProfilesFromCacheCallback mFromCacheCallback;
    private Handler mStartSearchHandler = new Handler();
    private Runnable mStartSearchEvent = new Runnable() {
        @Override
        public void run() {
            Log.d("GithubProfilesFragment", "start search");
            Bundle bundle = new Bundle();
            bundle.putString(BUNDLE_SEARCH_QUERY, mSearchView.getText().toString());
            startLocalSearch(bundle);
            startServerSearch(bundle);
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View root = inflater.inflate(R.layout.fragmet_github_profiles, container, true);
        mRecyclerView = (RecyclerView) root.findViewById(R.id.profiles_recycler);
        mSearchView = (EditText) root.findViewById(R.id.search_view);
        mSearchView.addTextChangedListener(new SearchTextWatcher());
        mEmptyView = (TextView) root.findViewById(R.id.empty_view);
        setupAdapter();
        return root;
    }

    private void setupAdapter() {
        mAdapter = new GithubProfilesAdapter(new ArrayList<Profile>());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void startSearch() {
        if (mStartSearchHandler.hasMessages(START_SEARCH_MSG_ID)){
            return;
        }
        Message msg = Message.obtain(mStartSearchHandler, mStartSearchEvent);
        msg.what = START_SEARCH_MSG_ID;
        mStartSearchHandler.sendMessageDelayed(msg, ONE_SECOND);
    }

    private void startLocalSearch(Bundle bundle){
        getActivity()
                .getSupportLoaderManager()
                .restartLoader(
                        Loaders.SEARCH_PROFILES_IN_CACHE.getId(),
                        bundle,
                        mFromCacheCallback);
    }

    private void startServerSearch(Bundle bundle){
        getActivity()
                .getSupportLoaderManager()
                .restartLoader(GetProfilesLoader.ID, bundle, this);
    }

    @Override
    public Loader<RequestResult<ProfilesAndTotalCount>> onCreateLoader(int id, Bundle args) {
        if (args == null || !args.containsKey(BUNDLE_SEARCH_QUERY)){
            throw new IllegalArgumentException("Don't used loader without args");
        }
        GetProfilesLoader loader = new GetProfilesLoader(getContext(), args.getString(BUNDLE_SEARCH_QUERY));
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<RequestResult<ProfilesAndTotalCount>> loader,
                               RequestResult<ProfilesAndTotalCount> result) {

        if (result.getException() == null){
            List<Profile> profiles = result.getData().getProfiles();
            mRecyclerView.setVisibility(profiles.isEmpty() ? View.GONE : View.VISIBLE);
            mEmptyView.setVisibility(profiles.isEmpty() ? View.VISIBLE : View.GONE);
            mAdapter.setProfiles(profiles);
        } else {
            Toast.makeText(getContext(), result.getException().toString(getContext()), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<RequestResult<ProfilesAndTotalCount>> loader) {
        mAdapter.setProfiles(Collections.<Profile>emptyList());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mFromCacheCallback = new ProfilesFromCacheCallback(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mStartSearchHandler.removeMessages(START_SEARCH_MSG_ID);
        mFromCacheCallback.detach();
    }

    private static class ProfilesFromCacheCallback implements LoaderManager.LoaderCallbacks<RequestResult<ProfilesAndTotalCount>> {

        private GithubProfilesFragment mFragment;

        private ProfilesFromCacheCallback(GithubProfilesFragment fragment) {
            mFragment = fragment;
        }

        public void detach() {
            mFragment = null;
        }

        @Override
        public Loader<RequestResult<ProfilesAndTotalCount>> onCreateLoader(int id, Bundle args) {
            if (args == null || !args.containsKey(BUNDLE_SEARCH_QUERY)){
                throw new IllegalArgumentException("Don't used loader without args");
            }

            return new FromCacheProfilesLoader(mFragment.getContext(), args.getString(BUNDLE_SEARCH_QUERY));
        }

        @Override
        public void onLoadFinished(Loader<RequestResult<ProfilesAndTotalCount>> loader, RequestResult<ProfilesAndTotalCount> data) {
            if (mFragment == null){
                return;
            }
            mFragment.onLoadFinished(loader, data);
        }

        @Override
        public void onLoaderReset(Loader<RequestResult<ProfilesAndTotalCount>> loader) {
            if (mFragment == null){
                return;
            }
            mFragment.onLoaderReset(loader);
        }
    }

    private class SearchTextWatcher implements TextWatcher {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (TextUtils.isEmpty(s)){
                return;
            }
            startSearch();
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }
}
