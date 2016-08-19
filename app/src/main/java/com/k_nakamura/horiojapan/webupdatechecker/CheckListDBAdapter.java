package com.k_nakamura.horiojapan.webupdatechecker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by user on 2016/08/18.
 */
public class CheckListDBAdapter {
    static final String DATABASE_NAME = "mychecklist.db";
    static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "checklist";
    public static final String COL_ID = "_id";
    public static final String COL_TITLE = "title";
    public static final String COL_URL = "URL";
    public static final String COL_LASTHTML = "lasthtml";
    public static final String COL_LASTUPDATE = "lastupdate";
    public static final String COL_IGNOREWARDS = "ignorewards";

    protected final Context context;

    protected DatabaseHelper dbHelper;
    protected SQLiteDatabase db;

    public CheckListDBAdapter(Context context){
        this.context = context;
        dbHelper = new DatabaseHelper(this.context);
    }

    //
    // SQLiteOpenHelper
    //
    private static class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override public void onCreate(SQLiteDatabase db) {
            db.execSQL(
                    "CREATE TABLE " + TABLE_NAME + " ("
                            + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                            + COL_TITLE + " TEXT NOT NULL,"
                            + COL_LASTUPDATE + " TEXT NOT NULL,"
                            + COL_URL + " TEXT NOT NULL,"
                            + COL_LASTHTML + " TEXT NOT NULL,"
                            + COL_IGNOREWARDS + " TEXT NOT NULL);"
            );
        }

        @Override public void onUpgrade( SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            onCreate(db);
        }
    }

    //
    // Adapter Methods
    //
    public CheckListDBAdapter open() {
        db = dbHelper.getWritableDatabase();
        return this;
    }
    public void close(){
        dbHelper.close();
    }

    //
    // App Methods
    //
    public boolean deleteAllCheckListDatas(){
        return db.delete(TABLE_NAME, null, null) > 0;
    }
    public boolean deleteCheckListData(int id){
        return db.delete(TABLE_NAME, COL_ID + "=" + id, null) > 0;
    }
    public Cursor getAllCheckListDatas(){
        return db.query(TABLE_NAME, null, null, null, null, null, null);
    }
    public void saveCheckListData(CheckListData checkListData)
    {
        ContentValues values = new ContentValues();
        values.put(COL_LASTHTML, checkListData.getLastHtml());
        values.put(COL_LASTUPDATE, checkListData.getLastupdate());
        values.put(COL_TITLE,checkListData.getTitle());
        values.put(COL_URL,checkListData.getUrl());
        values.put(COL_IGNOREWARDS,checkListData.getIgnoreWards());
        db.insertOrThrow(TABLE_NAME, null, values);
    }

    public void update(CheckListData checkListData)
    {
        ContentValues values = new ContentValues();
        values.put(COL_LASTHTML, checkListData.getLastHtml());
        values.put(COL_LASTUPDATE, checkListData.getLastupdate());
        values.put(COL_TITLE,checkListData.getTitle());
        values.put(COL_URL,checkListData.getUrl());
        values.put(COL_IGNOREWARDS,checkListData.getIgnoreWards());
        db.update(TABLE_NAME, values,COL_ID + "=?", new String[]{""+ checkListData.getId()});
    }
}
