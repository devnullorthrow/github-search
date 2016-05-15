package com.yoksnod.githubsearch.database;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.yoksnod.githubsearch.BuildConfig;
import com.yoksnod.githubsearch.database.entity.Profile;

import java.util.List;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class ProfileContentProvider extends ContentProvider {

    public static final String AUTHORITY = String.format("%s.data", BuildConfig.APPLICATION_ID);

    private SqliteDbHelper mHelper;

    @Override
    public boolean onCreate() {
        mHelper = new SqliteDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(Profile.TABLE_NAME);

        SQLiteDatabase db = mHelper.getReadableDatabase();
        return builder.query(db, projection, selection, selectionArgs, null, null, sortOrder);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return AUTHORITY + Profile.TABLE_NAME;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = mHelper.getWritableDatabase().insertOrThrow(Profile.TABLE_NAME, null, values);
        Uri newUri = ContentUris.withAppendedId(Profile.CONTENT_URI, id);
        return newUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        long id = getProfileId(uri);
        int count = mHelper.getWritableDatabase().delete(Profile.TABLE_NAME, String.format("%s=?", Profile._ID), new String[]{String.valueOf(id)});
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        /*simple implementation*/
        long count = delete(uri, selection, selectionArgs);
        Uri insertResult = insert(uri, values);
        return getProfileId(insertResult);
    }


    private int getProfileId(Uri uri) {
        List<String> pathSegments = uri.getPathSegments();
        if (pathSegments.size() < 1) {
            throw new IllegalArgumentException("Invalid uri" + uri);
        }
        return Integer.valueOf(pathSegments.get(pathSegments.size() - 1));
    }

    private static ProfileContentProvider getFromContext(Context context, String authority) {
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentProviderClient client = resolver.acquireContentProviderClient(authority);
            ContentProvider cp = client.getLocalContentProvider();
            return (ProfileContentProvider) cp;
        } catch (ClassCastException e) {
            String message = String.format("Content provider '%s' has unsupported type ProfileContentProvider!!!", authority);
            throw new RuntimeException(message, e);
        } catch (Exception e) {
            throw new RuntimeException(String.format("Error instantiate provider for %s", authority), e);
        }
    }

    public static SqliteDbHelper getDbHelper(Context context){
        return getFromContext(context, AUTHORITY).mHelper;
    }
}
