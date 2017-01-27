package com.techpalle.zomatoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by DELL on 27-Jan-17.
 */
public class MyDataBase {
    MyHelper myHelper;
    SQLiteDatabase sqLiteDatabase;
    public MyDataBase(Context c){
        myHelper = new MyHelper(c, "zomato.db", null, 1);
    }
    public void openDatabase(){
        sqLiteDatabase = myHelper.getWritableDatabase();
    }
    public void closeDataBase(){
        sqLiteDatabase.close();
    }
    public void insert(String name, String locality,String address, String image, String latitude,String longitude ){
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("locality", locality);
        contentValues.put("address", address);
        contentValues.put("image", image);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        sqLiteDatabase.insert("restaurants",null, contentValues);
    }
    public Cursor query(){
        Cursor c;
        c = sqLiteDatabase.query("restaurants", null, null, null, null, null, null);
        return c;
    }
    public class MyHelper extends SQLiteOpenHelper{
        public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table restaurants(_id integer primary key, name text, locality text, " +
                    "address text, image text, latitude text, longitude text);");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
