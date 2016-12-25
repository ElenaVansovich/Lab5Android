package com.mypackage.lab_5_3_lastfm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by user on 11.12.2016.
 */

public class DBHelper extends SQLiteOpenHelper {
    final String LOG_TAG = "myLogs";


    public DBHelper(Context context) {
        super(context, "tracks", null, 1);
    }
    @Override
    public void onCreate(SQLiteDatabase db2) {
        db2.execSQL("create table tracks ("
                + "id integer primary key autoincrement, "
                + "singer text, "
                + "song text, "
                + "amount_plays text, "
                + "amount_users text);");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db2, int oldVersion, int
            newVersion) {
        Log.d(LOG_TAG, " --- onUpgrade database from " + oldVersion
                + " to " + newVersion + " version --- ");
        if (oldVersion < newVersion) {
            db2.execSQL("drop table " + "tracks" + ";");
            onCreate(db2);
        }
    }
}