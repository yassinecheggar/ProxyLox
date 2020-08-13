package com.upem.proxyloc.services;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProxyLoc.db";
    public static final String CONTACTS_TABLE_NAME = "mylocation";


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, 6);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table mylocation (id integer primary key autoincrement, mac text,latitude text,longitude text, date text)");
        db.execSQL("create table expose (id integer primary key autoincrement, mac text, sec integer, startdate text, lastdate text)");
        db.execSQL("create table myactivities (id integer primary key autoincrement, category text,activity text, startdate text)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS mylocation");
        db.execSQL("DROP TABLE IF EXISTS expose");
        db.execSQL("DROP TABLE IF EXISTS myactivities");
        onCreate(db);
    }

    public boolean insertLocation(String mac, String latitude, String longitude, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mac", mac);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("date", date);

        db.insert("mylocation", null, contentValues);
        return true;
    }

    public Cursor getexpo(String mac) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from expose where mac=" + "'"+mac + "'", null);

        Log.e("db", "getexpo: " + res.getCount() );
        return res;
    }

    public int numberOfRows() {
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateExpose(String mac, int sec , String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        //contentValues.put("mac", mac);
        contentValues.put("sec", sec);
        contentValues.put("lastdate", lastdate);


        db.update("expose", contentValues, "mac = " +"'"+ mac + "'", null);
        return true;
    }

    public Integer deleteContact(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public void deleteall() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM mylocation");
    }

    public void deleteallexpose() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM expose");
    }

    public void deleteallactivities() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM myactivities");
    }

    public JSONArray getAll() {

        JSONArray array_list = new JSONArray();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from Mylocation", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("mac", res.getString(res.getColumnIndex("mac")));
                obj.put("latitude", res.getString(res.getColumnIndex("latitude")));
                obj.put("longitude", res.getString(res.getColumnIndex("longitude")));
                obj.put("TimeColumn", res.getString(res.getColumnIndex("date")));
                obj.put("UsrStatus", "0");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            array_list.put(obj);
            res.moveToNext();
        }
        return array_list;
    }

    public boolean insertExpose(String mac, int sec,String startdate,  String lastdate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mac", mac);
        contentValues.put("sec", sec);
        contentValues.put("startdate", startdate);
        contentValues.put("lastdate", lastdate);
        db.insert("expose", null, contentValues);
        return true;
    }

    public boolean insertactivity(String category ,String activity , String startdate ) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("category", category);
        contentValues.put("activity", activity);
        contentValues.put("startdate", startdate);

        db.insert("myactivities", null, contentValues);
        return true;
    }


    public JSONArray getAllexpo() {

        JSONArray array_list = new JSONArray();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from expose", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("mac", res.getString(res.getColumnIndex("mac")));
                obj.put("sec", res.getString(res.getColumnIndex("sec")));
                obj.put("startdate", res.getString(res.getColumnIndex("startdate")));
                obj.put("lastdate", res.getString(res.getColumnIndex("lastdate")));


            } catch (JSONException e) {
                e.printStackTrace();
            }
            array_list.put(obj);
            res.moveToNext();
        }
        return array_list;
    }


    public JSONArray getActivities() {

        JSONArray array_list = new JSONArray();
        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from myactivities", null);
        res.moveToFirst();

        while (res.isAfterLast() == false) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("category", res.getString(res.getColumnIndex("category")));
                obj.put("activity", res.getString(res.getColumnIndex("activity")));
                obj.put("startdate", res.getString(res.getColumnIndex("startdate")));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            array_list.put(obj);
            res.moveToNext();
        }
        return array_list;
    }

}
