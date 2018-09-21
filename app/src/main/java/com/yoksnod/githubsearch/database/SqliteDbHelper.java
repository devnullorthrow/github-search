package com.yoksnod.githubsearch.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.yoksnod.githubsearch.database.entity.Profile;

/**
 * Created by d.donskoy on 14.05.2016.
 */
public class SqliteDbHelper extends SQLiteOpenHelper {

    private static final String GITHUB_YOKSNOD_DB = "github_yoksnod_db";
    private static final int DB_VERSION = 1;

    public SqliteDbHelper(Context context) {
        super(context, GITHUB_YOKSNOD_DB, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Profile.CREATE_TABLE_STATEMENT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /*simple upgrade implementation*/
        db.execSQL(Profile.UPGRADE_TABLE_STATEMENT);
        onCreate(db);
    }
}
