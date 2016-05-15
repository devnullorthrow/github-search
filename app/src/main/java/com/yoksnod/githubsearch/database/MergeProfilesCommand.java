package com.yoksnod.githubsearch.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.yoksnod.githubsearch.server.AbstractRequest;
import com.yoksnod.githubsearch.server.RequestResult;
import com.yoksnod.githubsearch.database.entity.Profile;
import java.util.List;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class MergeProfilesCommand extends AbstractRequest<List<Profile>, RequestResult<List<Profile>>> {

    private final Context mContext;

    public MergeProfilesCommand(Context context, List<Profile> forMerge) {
        super(forMerge);
        mContext = context;
    }

    @Override
    public RequestResult<List<Profile>> execute() {
        SqliteDbHelper dbHelper = ProfileContentProvider.getDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        final List<Profile> forMergeProfiles;
        try {
            cleanupExpired(db);
            forMergeProfiles = insertNewData(db);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            dbHelper.close();
        }

        return new RequestResult<>(forMergeProfiles);
    }

    @NonNull
    private List<Profile> insertNewData(SQLiteDatabase db) {
        ContentValues buffer = new ContentValues();
        List<Profile> forMergeProfiles = getParams();
        for (Profile profile : forMergeProfiles){
            buffer.put(Profile.COL_NAME_SERVER_ID, profile.getServerId());
            buffer.put(Profile.COL_NAME_LOGIN, profile.getLogin());
            buffer.put(Profile.COL_NAME_AVATAR, profile.getAvatarUrl());

            long id = db.insert(Profile.TABLE_NAME, null, buffer);
            profile.setId(id);
            buffer.clear();
        }
        return forMergeProfiles;
    }

    private long cleanupExpired(SQLiteDatabase db) {
        List<String> transformed = Lists.transform(getParams(), new ProfileToIdTransformer());
        String inStatement = prepareInStatement(transformed);
        final long deleteCount = db.delete(
                Profile.TABLE_NAME,
                String.format("%s IN %s", Profile.COL_NAME_SERVER_ID, inStatement), null);
        return deleteCount;
    }

    private static String prepareInStatement(List<String> transformedIds){
        String join = TextUtils.join(", ", transformedIds);
        return String.format("( %s )", join);
    }

    private static class ProfileToIdTransformer implements Function<Profile, String> {
        @Override
        public String apply(Profile from) {
            return String.valueOf(from.getServerId());
        }
    }
}
