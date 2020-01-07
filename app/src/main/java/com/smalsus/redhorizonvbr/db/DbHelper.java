package com.smalsus.redhorizonvbr.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DbHelper extends SQLiteOpenHelper {

    private String TAG = DbHelper.class.getSimpleName();

    private static final String DB_NAME = "groupchatwebrtcDB";

    public static final String DB_TABLE_NAME = "users";
    public static final String DB_COLUMN_ID = "ID";
    public static final String DB_COLUMN_USER_FULL_NAME = "userFullName";
    public static final String DB_COLUMN_USER_LOGIN = "userLogin";
    public static final String DB_COLUMN_USER_ID = "userID";
    public static final String DB_COLUMN_USER_PASSWORD = "userPass";
    public static final String DB_COLUMN_USER_TAG = "userTag";

    public static final String DB_TABLE_CALL_HISTORY = "call_history";
    public static final String HISTORY_TABLE_ID_COL = "callHistory_ID";
    public static final String HISTORY_TABLE_CALLTYPE_COL = "call_type";
    public static final String HISTORY_TABLE_GNAME_COL = "call_history_name";
    public static final String HISTORY_TABLE_TIME_COL = "call_time";
    public static final String HISTORY_TABLE_DURATION_COL = "call_duration";
    public static final String HISTORY_TABLE_PARTICIPTE_COL = "call_participate";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "--- onCreate database ---");
        db.execSQL("create table " + DB_TABLE_NAME + " ("
                + DB_COLUMN_ID + " integer primary key autoincrement,"
                + DB_COLUMN_USER_ID + " integer,"
                + DB_COLUMN_USER_LOGIN + " text,"
                + DB_COLUMN_USER_PASSWORD + " text,"
                + DB_COLUMN_USER_FULL_NAME + " text,"
                + DB_COLUMN_USER_TAG + " text"
                + ");");


        db.execSQL("create table " + DB_TABLE_CALL_HISTORY + " ("
                + HISTORY_TABLE_ID_COL + " integer primary key autoincrement,"
                + HISTORY_TABLE_CALLTYPE_COL + " integer,"
                + HISTORY_TABLE_GNAME_COL + " text,"
                + HISTORY_TABLE_TIME_COL + " text,"
                + HISTORY_TABLE_DURATION_COL + " text,"
                + HISTORY_TABLE_PARTICIPTE_COL + " text"
                + ");");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}