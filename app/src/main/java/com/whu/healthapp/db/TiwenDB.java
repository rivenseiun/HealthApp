package com.whu.healthapp.db;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.whu.healthapp.bean.jqb.TestDataBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 47462 on 2016/11/15.
 */
public class TiwenDB extends SQLiteOpenHelper {

    private static final String TABLE = "CREATE TABLE TIWEN(" +
            "ID INTEGER PRIMARY KEY AUTOINCREMENT," +
            "USERID INT," +
            "TIME TIME," +
            "TIWEN VARCHAR(10))";

    public static final int VERSION = 1;

    public TiwenDB(Context context){
        this(context,"TIWEN",null,VERSION);
    }

    public TiwenDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        this(context, name, factory, version, null);
    }

    public TiwenDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<TestDataBean> getDatas(String userId){
        List<TestDataBean> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
            Cursor cursor = db.rawQuery("select * from TIWEN where USERID=? ", new String[]{userId + ""});
            while (cursor!=null && cursor.moveToNext()){
                TestDataBean bean = new TestDataBean();
                bean.setLocalId(cursor.getInt(cursor.getColumnIndex("ID")));;
                bean.setTime(cursor.getString(cursor.getColumnIndex("TIME")));
                bean.setUserId(userId);
                bean.setTiwen(cursor.getString(cursor.getColumnIndex("TIWEN")));;
                datas.add(bean);
            }
            if(cursor!=null){
                cursor.close();
            }
            db.close();
        }

        return datas;
    }

    public void addData(TestDataBean data){
        List<TestDataBean> datas = new ArrayList<>();
        SQLiteDatabase db = null;
        db = getReadableDatabase();
        if(db!=null){
           db.execSQL("INSERT INTO TIWEN(USERID,TIME,TIWEN) VALUES(?,?,?)",new Object[]{data.getUserId(), data.getTime(), data.getTiwen()});
            db.close();
        }
    }

}
