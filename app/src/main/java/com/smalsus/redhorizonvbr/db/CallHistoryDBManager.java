package com.smalsus.redhorizonvbr.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.smalsus.redhorizonvbr.model.CallHistoryModelClass;

import java.util.ArrayList;

public class CallHistoryDBManager {

    private static String TAG = CallHistoryDBManager.class.getSimpleName();

    private static CallHistoryDBManager instance;
    private Context mContext;

    private CallHistoryDBManager(Context context) {
        this.mContext = context;
    }

    public static CallHistoryDBManager getInstance(Context context) {
        if (instance == null) {
            instance = new CallHistoryDBManager(context);
        }

        return instance;
    }

    public boolean isDataTableAvailable(){
        boolean tableExists = false;
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try
        {
            Cursor c = db.query(DbHelper.DB_TABLE_CALL_HISTORY, null, null, null, null, null, null);
            tableExists = true;
        }
        catch (Exception e) {
            Log.d(TAG, " doesn't exist :(((");
        }

        return tableExists;
    }

    public ArrayList<CallHistoryModelClass> getAllUsers() {
        ArrayList<CallHistoryModelClass> allHistory = new ArrayList<>();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        Cursor c = db.query(DbHelper.DB_TABLE_CALL_HISTORY, null, null, null, null, null, null);
        if (c.moveToFirst()) {
            int historyIdColIndex = c.getColumnIndex(DbHelper.HISTORY_TABLE_ID_COL);
            int historyNameColIndex = c.getColumnIndex(DbHelper.HISTORY_TABLE_GNAME_COL);
            int historyCallTypeColIndex=c.getColumnIndex(DbHelper.HISTORY_TABLE_CALLTYPE_COL);
            int historyTimeColIndex = c.getColumnIndex(DbHelper.HISTORY_TABLE_TIME_COL);
            int historyDurationColIndex = c.getColumnIndex(DbHelper.HISTORY_TABLE_DURATION_COL);
            int historyParticipateColIndex = c.getColumnIndex(DbHelper.HISTORY_TABLE_PARTICIPTE_COL);
            do {
                CallHistoryModelClass history = new CallHistoryModelClass();
                history.setHistoryId(c.getInt(historyIdColIndex));
                history.setGroupName(c.getString(historyNameColIndex));
                history.setCallType(c.getInt(historyCallTypeColIndex));
                history.setCallTime(c.getString(historyTimeColIndex));
                history.setCallDuration(c.getString(historyDurationColIndex));
                history.setCallParticipate(c.getString(historyParticipateColIndex));
                allHistory.add(history);
            } while (c.moveToNext());
        }

        c.close();
        dbHelper.close();
        return allHistory;
    }

    public void saveCallHistory(CallHistoryModelClass historyModelClass) {
        ContentValues cv = new ContentValues();
        DbHelper dbHelper = new DbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        cv.put(DbHelper.HISTORY_TABLE_GNAME_COL, historyModelClass.getGroupName());
        cv.put(DbHelper.HISTORY_TABLE_TIME_COL, historyModelClass.getCallTime());
        cv.put(DbHelper.HISTORY_TABLE_DURATION_COL, historyModelClass.getCallDuration());
        cv.put(DbHelper.HISTORY_TABLE_PARTICIPTE_COL, historyModelClass.getCallParticipate());
        cv.put(DbHelper.HISTORY_TABLE_CALLTYPE_COL,historyModelClass.getCallType());
        db.insert(DbHelper.DB_TABLE_CALL_HISTORY, null, cv);
        dbHelper.close();
    }

}
