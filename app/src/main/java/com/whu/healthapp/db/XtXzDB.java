package com.whu.healthapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whu.healthapp.bean.jqb.TestDataBean;
import com.whu.healthapp.bean.jqb.XtXz;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 47462 on 2016/11/15.
 */
public class XtXzDB extends SQLiteOpenHelper {

    private static final String TABLE = "CREATE TABLE XtXz(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "USERID INT," +
            "TIME TIME," +
            "XT VARCHAR(10)," +
            "XZ VARCHAR(10))";

    public static final int VERSION = 1;

    public XtXzDB(Context context){
        this(context,"XtXz",null,VERSION);
    }

    public XtXzDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    public XtXzDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<XtXz> getDatas(String userId){
        List<XtXz> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
            Cursor cursor = db.rawQuery("select * from XtXz where USERID=? ", new String[]{userId + ""});
            while (cursor!=null && cursor.moveToNext()){
                XtXz bean = new XtXz();
                bean.setId(cursor.getInt(cursor.getColumnIndex("ID")));;
                bean.setTime(cursor.getString(cursor.getColumnIndex("TIME")));
                bean.setUserId(userId);
                bean.setXt(cursor.getString(cursor.getColumnIndex("XT")));;
                bean.setXz(cursor.getString(cursor.getColumnIndex("XZ")));;
                datas.add(bean);
            }
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }

        return datas;
    }

    public void addData(XtXz data){
        List<TestDataBean> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
           db.execSQL("INSERT INTO XtXz(USERID,TIME,XT,XZ) VALUES(?,?,?,?)",new Object[]{data.getUserId(), data.getTime(), data.getXt(),data.getXz()});
            db.close();
        }
    }

}
