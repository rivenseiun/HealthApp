package com.whu.healthapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whu.healthapp.bean.jqb.HeightAndWeight;
import com.whu.healthapp.bean.jqb.TestDataBean;

import java.util.ArrayList;
import java.util.List;


public class HeightWeightDB extends SQLiteOpenHelper {

    private static final String TABLE = "CREATE TABLE HEIGHTANDWEIGHT(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "USERID INT," +
            "TIME TIME," +
            "WEIGHT VARCHAR(10)," +
            "HEIGHT VARCHAR(10))";

    public static final int VERSION = 1;

    public HeightWeightDB(Context context){
        this(context,"HEIGHTANDWEIGHT",null,VERSION);
    }

    public HeightWeightDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    public HeightWeightDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<HeightAndWeight> getDatas(String userId){
        List<HeightAndWeight> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
            Cursor cursor = db.rawQuery("select * from HEIGHTANDWEIGHT where USERID=? ", new String[]{userId + ""});
            while (cursor!=null && cursor.moveToNext()){
                HeightAndWeight bean = new HeightAndWeight();
                bean.setId(cursor.getInt(cursor.getColumnIndex("ID")));;
                bean.setTime(cursor.getString(cursor.getColumnIndex("TIME")));
                bean.setUserId(userId);
                bean.setHeight(cursor.getString(cursor.getColumnIndex("HEIGHT")));;
                bean.setWeight(cursor.getString(cursor.getColumnIndex("WEIGHT")));;
                datas.add(bean);
            }
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }

        return datas;
    }

    public void addData(HeightAndWeight data){
        List<TestDataBean> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
            db.execSQL("INSERT INTO HEIGHTANDWEIGHT(USERID,TIME,WEIGHT,HEIGHT) VALUES(?,?,?,?)",new Object[]{data.getUserId(), data.getTime(), data.getWeight(),data.getHeight()});
            db.close();
        }
    }

}
