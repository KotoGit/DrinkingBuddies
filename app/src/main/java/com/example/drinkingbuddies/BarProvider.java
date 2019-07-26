package com.example.drinkingbuddies;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

public class BarProvider extends ContentProvider {
    public static final int DBVERSION = 1;
    private static final String DBNAME = "BARDB";
    private static final Uri CONTENT_URI = Uri.parse("content://com.example.drinkingbuddies.provider");
    DataHelper mDataHelper;

    //Login table constants
    private static final String TABLE_LOGINTABLE = "LoginTable";
    private static final String COLUMN_USERNAME = "Username";
    private static final String COLUMN_PASSWD = "Passwd";
    private static final String COLUMN_FAVLIST = "Favlist";
    private static final String COLUMN_PRICEPREF = "Pricepref";
    private static final String COLUMN_HOURPREF = "Hourpref";
    private static final String SQL_CREATE_LOGIN = "CREATE TABLE " + TABLE_LOGINTABLE + "(" + "_ID INTEGER PRIMARY KEY, " +
            COLUMN_USERNAME + " TEXT," + COLUMN_PASSWD + " TEXT," +
            COLUMN_FAVLIST + " TEXT," + COLUMN_PRICEPREF + " TEXT," +
            COLUMN_HOURPREF + " TEXT)";

    //Location table constants
    private static final String TABLE_LOCATIONTABLE = "LocationTable";
    private static final String COLUMN_NAME = "Name";
    private static final String COLUMN_COORDINATES = "Coordinates";
    private static final String COLUMN_HOUR = "Hour";
    private static final String COLUMN_PRICE = "Price";
    private static final String SQL_CREATE_LOCATION = "CREATE TABLE " + TABLE_LOCATIONTABLE + "(" + "_ID INTEGER PRIMARY KEY, " +
            COLUMN_NAME + " TEXT," + COLUMN_COORDINATES + " TEXT," +
            COLUMN_HOUR + " TEXT," + COLUMN_PRICE + " TEXT)";

    protected static final class DataHelper extends SQLiteOpenHelper{
        DataHelper(Context context){
            super(context, DBNAME, null, DBVERSION);
        }
        @Override
        public void onCreate(SQLiteDatabase db){
            db.execSQL(SQL_CREATE_LOGIN);
            db.execSQL(SQL_CREATE_LOCATION);
        }
        @Override
        public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2){

        }
    }

    /****** DELETE METHODS ******/
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        //program uses different delete methods
        return 0;
    }

    public int delete_login(Uri uri, String selection, String[] selectionArgs){
        return mDataHelper.getWritableDatabase().delete(TABLE_LOGINTABLE, selection, selectionArgs);
    }

    public int delete_location(Uri uri, String selection, String[] selectionArgs){
        return mDataHelper.getWritableDatabase().delete(TABLE_LOCATIONTABLE, selection, selectionArgs);
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    /****** INSERT METHODS ******/
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //program uses different insert methods
        return uri;
    }

    public Uri insert_login(Uri uri, ContentValues values){
        String username = values.getAsString(COLUMN_USERNAME);
        String passwd = values.getAsString(COLUMN_PASSWD);
        if(username == null || passwd == null){
            return null;
        }
        else if(username.equals("") || passwd.equals("")){
            return null;
        }

        long id = mDataHelper.getWritableDatabase().insert(TABLE_LOGINTABLE, null, values);
        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }

    public Uri insert_location(Uri uri, ContentValues values){
        String name = values.getAsString(COLUMN_NAME);
        String coord = values.getAsString(COLUMN_COORDINATES);
        if(name == null || coord == null){
            return null;
        }
        else if(name.equals("") || coord.equals("")){
            return null;
        }

        long id = mDataHelper.getWritableDatabase().insert(TABLE_LOCATIONTABLE, null, values);
        return Uri.withAppendedPath(CONTENT_URI, "" + id);
    }

    @Override
    public boolean onCreate() {
        mDataHelper = new DataHelper(getContext());
        return true;
    }

    /****** QUERY METHODS ******/
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        //program uses different query methods
        return null;
    }

    public Cursor query_login(Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder){
        return mDataHelper.getReadableDatabase().query(TABLE_LOGINTABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    public Cursor query_location(Uri uri, String[] projection, String selection,
                              String[] selectionArgs, String sortOrder){
        return mDataHelper.getReadableDatabase().query(TABLE_LOCATIONTABLE, projection, selection, selectionArgs, null, null, sortOrder);
    }

    /****** UPDATE METHODS ******/
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        //program uses different update methods
        return 0;
    }

    public int update_login(Uri uri, ContentValues values, String selection,
                            String[] selectionArgs){
        String username = values.getAsString(COLUMN_USERNAME);
        String passwd = values.getAsString(COLUMN_PASSWD);
        if(username == null || passwd == null){
            return -1;
        }
        else if(username.equals("") || passwd.equals("")){
            return -1;
        }
        return mDataHelper.getWritableDatabase().update(TABLE_LOGINTABLE, values, selection, selectionArgs);
    }

    public int update_location(Uri uri, ContentValues values, String selection,
                            String[] selectionArgs){
        String name = values.getAsString(COLUMN_NAME);
        String coord = values.getAsString(COLUMN_COORDINATES);
        if(name == null || coord == null){
            return -1;
        }
        else if(name.equals("") || coord.equals("")){
            return -1;
        }
        return mDataHelper.getWritableDatabase().update(TABLE_LOCATIONTABLE, values, selection, selectionArgs);
    }
}
