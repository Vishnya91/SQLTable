package com.yasya.sqltable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class DBAdapter {

    private static final String DATABASE_TABLE = "person";

    public static final String KEY_ROWID = "id";
    public static final String KEY_LAST = "last";
    public static final String KEY_FIRST = "first";
    public static final String KEY_AGE = "age";
    public static final String KEY_STREET = "street";

    private Context context;
    private SQLiteDatabase database;
    private DBHelper dbHelper;

    public DBAdapter(Context context) {
        this.context = context;
    }

    public DBAdapter open() throws SQLException {
        dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        dbHelper.close();
    }

    public int createRow(String last, String first, String age, String street) {
        ContentValues initialValues = createContentValues(last, first,
                age, street);
        return (int) database.insert(DATABASE_TABLE, null, initialValues);
    }

    public boolean updateRow(int rowId, String last, String first, String age, String street) {
        ContentValues updateValues = createContentValues(last, first,
                age, street);
        return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "="
                + rowId, null) > 0;
    }


    public boolean deleteRow(int rowId) {
        return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    public Cursor fetchRow(int rowId) throws SQLException {

        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{
                KEY_ROWID, KEY_LAST, KEY_FIRST, KEY_AGE, KEY_STREET},
                KEY_ROWID + "=" + rowId, null, null, null, null, null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public void deleteAll() {
        database.delete(DATABASE_TABLE, null, null);
    }

    public Cursor fetchAll() {

        return database.query(DATABASE_TABLE, new String[]{KEY_ROWID,
                KEY_LAST, KEY_FIRST, KEY_AGE, KEY_STREET}, null, null, null,
                null, null);
    }

    private ContentValues createContentValues(String last, String first, String age, String street) {

        ContentValues values = new ContentValues();
        values.put(KEY_LAST, last);
        values.put(KEY_FIRST, first);
        values.put(KEY_AGE, age);
        values.put(KEY_STREET, street);

        return values;
    }
}

