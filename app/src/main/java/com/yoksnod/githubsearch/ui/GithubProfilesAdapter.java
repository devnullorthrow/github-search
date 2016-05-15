package com.yoksnod.githubsearch.ui;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yoksnod.githubsearch.R;
import com.yoksnod.githubsearch.database.entity.Profile;

import java.util.List;

/**
 * Created by 1 on 13.05.2016.
 */
public class GithubProfilesAdapter extends RecyclerView.Adapter<GithubProfilesAdapter.ProfileViewHolder> {

    private final List<Profile> mProfiles;

    public GithubProfilesAdapter(List<Profile> profiles) {
        mProfiles = profiles;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        Profile profile = mProfiles.get(position);
        holder.profileText.setText(profile.getLogin());
        Glide
                .with(holder.itemView.getContext().getApplicationContext())
                .load(profile.getAvatarUrl())
                .dontTransform()
                .dontAnimate()
                .into(holder.avatar);
    }

    @Override
    public int getItemCount() {
        return mProfiles.size();
    }

    public void setProfiles(List<Profile> data) {
        mProfiles.clear();
        mProfiles.addAll(data);
        notifyDataSetChanged();
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder {

        final TextView profileText;
        final ImageView avatar;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profileText = (TextView) itemView.findViewById(R.id.profile);
            avatar = (ImageView) itemView.findViewById(R.id.avatar);
        }
    }
}
