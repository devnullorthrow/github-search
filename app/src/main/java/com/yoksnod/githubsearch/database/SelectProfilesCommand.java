package com.yoksnod.githubsearch.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yoksnod.githubsearch.server.AbstractRequest;
import com.yoksnod.githubsearch.server.ProfilesAndTotalCount;
import com.yoksnod.githubsearch.server.RequestResult;
import com.yoksnod.githubsearch.database.entity.Profile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class SelectProfilesCommand extends AbstractRequest<String, RequestResult<ProfilesAndTotalCount>> {

    private final Context mContext;

    public SelectProfilesCommand(Context context, String params) {
        super(params);
        mContext = context.getApplicationContext();
    }

    @Override
    public RequestResult<ProfilesAndTotalCount> execute() {
        SqliteDbHelper dbHelper = ProfileContentProvider.getDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        final String likeTemplate = new StringBuilder()
                .append("%")
                .append(getParams())
                .append("%")
                .toString();
        final List<Profile> profiles = new ArrayList<>();
        final long totalCount;
        Cursor cursor = null;
        try {
            cursor = db.query(
                    Profile.TABLE_NAME,
                    null,
                    String.format("%s LIKE ?", Profile.COL_NAME_LOGIN),
                    new String[]{likeTemplate},
                    null,
                    null,
                    null,
                    null);
            cursor.moveToFirst();
            totalCount = cursor.getCount();
            while (cursor.moveToNext()){
                String avatar = cursor.getString(cursor.getColumnIndex(Profile.COL_NAME_AVATAR));
                String login = cursor.getString(cursor.getColumnIndex(Profile.COL_NAME_LOGIN));
                long serverId = cursor.getLong(cursor.getColumnIndex(Profile.COL_NAME_SERVER_ID));
                long dbId = cursor.getLong(cursor.getColumnIndex(Profile._ID));
                Profile item = new Profile(dbId, serverId, avatar, login);
                profiles.add(item);
            }

        } finally {
            if (cursor != null){
                cursor.close();
            }
            dbHelper.close();
        }

        return new RequestResult<>(new ProfilesAndTotalCount(profiles, totalCount));
    }


}
