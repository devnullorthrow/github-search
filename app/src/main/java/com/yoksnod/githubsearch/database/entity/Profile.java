package com.yoksnod.githubsearch.database.entity;

import android.net.Uri;
import android.provider.BaseColumns;

import com.yoksnod.githubsearch.database.ProfileContentProvider;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class Profile implements BaseColumns {

    public static final Uri CONTENT_URI = Uri.parse("content://" + ProfileContentProvider.AUTHORITY);

    public static final String COL_NAME_SERVER_ID = "server_id";
    public static final String COL_NAME_AVATAR = "avatar";
    public static final String COL_NAME_LOGIN = "login";

    public static final String TABLE_NAME = "profiles";

    public static final String UPGRADE_TABLE_STATEMENT = "DROP TABLE '" + TABLE_NAME + "' ";
    public static final String CREATE_TABLE_STATEMENT =
                            "CREATE TABLE `" + TABLE_NAME + "` (" +
                            "`" + _ID + "` INTEGER PRIMARY KEY AUTOINCREMENT , " +
                            "`" + COL_NAME_AVATAR + "` VARCHAR ," +
                            "`" + COL_NAME_LOGIN + "` VARCHAR ," + "`"
                                    + COL_NAME_SERVER_ID + "` BIGINT , " + "UNIQUE (`"
                                    + COL_NAME_SERVER_ID + "`) )";

    private long mRowId;
    private final long mServerId;
    private final String mAvatarUrl;
    private final String mLogin;

    public Profile(long rowId,
                   long serverId,
                   String avatarUrl,
                   String login) {
        mRowId = rowId;
        mServerId = serverId;
        mAvatarUrl = avatarUrl;
        mLogin = login;
    }


    public long getId() {
        return mRowId;
    }

    public long getServerId() {
        return mServerId;
    }

    public String getAvatarUrl() {
        return mAvatarUrl;
    }

    public String getLogin() {
        return mLogin;
    }

    public void setId(long id) {
        mRowId = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile)) return false;

        Profile profile = (Profile) o;

        if (mServerId != profile.mServerId) return false;
        if (mAvatarUrl != null ? !mAvatarUrl.equals(profile.mAvatarUrl) : profile.mAvatarUrl != null)
            return false;
        return !(mLogin != null ? !mLogin.equals(profile.mLogin) : profile.mLogin != null);

    }

    @Override
    public int hashCode() {
        int result = (int) (mServerId ^ (mServerId >>> 32));
        result = 31 * result + (mAvatarUrl != null ? mAvatarUrl.hashCode() : 0);
        result = 31 * result + (mLogin != null ? mLogin.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Profile{");
        sb.append("mRowId=").append(mRowId);
        sb.append(", mServerId=").append(mServerId);
        sb.append(", mAvatarUrl='").append(mAvatarUrl).append('\'');
        sb.append(", mLogin='").append(mLogin).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
