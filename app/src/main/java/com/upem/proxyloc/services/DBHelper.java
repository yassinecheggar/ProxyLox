package com.upem.proxyloc.services;

import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import org.json.JSONException;
import org.json.JSONObject;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "ProxyLoc.db";
    public static final String CONTACTS_TABLE_NAME = "mylocation";




    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table mylocation (id integer primary key autoincrement, mac text,latitude text,longitude text, date text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS mylocation");
        onCreate(db);
    }

    public boolean insertLocation ( String mac, String latitude, String longitude,String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("mac", mac);
        contentValues.put("latitude", latitude);
        contentValues.put("longitude", longitude);
        contentValues.put("date", date);

        db.insert("mylocation", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from contacts where id="+id+"", null );
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, CONTACTS_TABLE_NAME);
        return numRows;
    }

    public boolean updateContact (Integer id, String name, String phone, String email, String street,String place) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("phone", phone);
        contentValues.put("email", email);
        contentValues.put("street", street);
        contentValues.put("place", place);
        db.update("contacts", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public Integer deleteContact (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("contacts",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }

    public void deleteall () {
        SQLiteDatabase db = this.getWritableDatabase();
          db.execSQL("DELETE FROM mylocation");
    }

    public Vector<JSONObject> getAll() {
        Vector<JSONObject> array_list = new Vector<>();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from Mylocation", null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            JSONObject obj = new JSONObject();
            try {
                obj.put("mac",res.getColumnIndex("mac"));
                obj.put("altitude",res.getColumnIndex("latitude"));
                obj.put("longitude",res.getColumnIndex("latitude"));
                obj.put("date",res.getColumnIndex("date"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array_list.add(obj);
            res.moveToNext();
        }
        return array_list;
    }
}
