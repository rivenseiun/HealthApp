package com.whu.healthapp.bluetooth.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;

//数据库类
public class HealthDBhelper extends SQLiteOpenHelper
{
	private static final String TAG = "healthDBAdapter";
	private static final String DATABASE_NAME = Environment.getExternalStorageDirectory()+"/databases/easyhealth.db";
	private static final String HEALTH_TABLE = "HealthTable";
	private static final String USER_TABLE = "UserTable";
	private static final int DATABASE_VERSION = 2;
	private static final String HEALTHTABLE_CREATE =
			"create table HealthTable (_id integer primary key autoincrement, "
					+ "name text not null, date text ,"
					+ "tiwen float, xueyang integer ,"
					+ "mailv integer , shuzhangya integer,"
					+ "shousuoya integer, dongmaya integer, resprate integer,xinlv integer,ecgcad text,jieguo text,xindian text,savname text);";
	//数据表：姓名、日期、身高、体重、bmi、血氧、呼吸率、体温、心率、高压、低压、血糖、心电文件名称
	private static final String USERTABLE_CREATE =
			"create table UserTable (_id integer primary key autoincrement, "
					+ "name text not null, passwd text, "
					+ "gender text, age integer, date text);";
//数据表：姓名、密码、性别、年龄、日期、身高、体重、bmi、血氧、呼吸率、体温、心率、高压、低压、血糖、心电文件名称

	private SQLiteDatabase db;
	//构造函数
	public HealthDBhelper (Context c) {
		super(c,DATABASE_NAME,null,DATABASE_VERSION);
	}
	//
	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		Log.d(TAG, "Create database");
		db.execSQL(HEALTHTABLE_CREATE);
		db.execSQL(USERTABLE_CREATE);
	}
	public void insert(ContentValues values, int tableindex) {
		String selectedtable = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				break;
			case 1:
				selectedtable = USER_TABLE;
				break;
			default:
				break;
		}
		db = getWritableDatabase();
		db.insert(selectedtable, null, values);
		db.close();
	}
	public Cursor query(int tableindex, String loginuser)
	{
		String selectedtable = null;
		String sqlQuery = null;
		db = this.getReadableDatabase();
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				sqlQuery = "select * from "+ selectedtable + " where name = " + "'"+loginuser +"'" ;
				break;
			case 1:
				selectedtable = USER_TABLE;
				sqlQuery = "select * from "+ selectedtable ;
				break;
			default:
				break;
		}
		Cursor cursor = db.rawQuery(sqlQuery,null);
		return cursor;
	}
	public Cursor query(String sql, String rule, int tableindex, String loginuser)
	{
		String selectedtable = null;
		db = this.getReadableDatabase();
		String sqlQuery = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				sqlQuery = sql+ selectedtable + " where name = " + "'"+loginuser +"' " + rule ;
				break;
			case 1:
				selectedtable = USER_TABLE;
				sqlQuery = sql+ selectedtable +rule;
				break;
			default:
				break;
		}
		Cursor cursor = db.rawQuery(sqlQuery, null);
		return cursor;
	}

	public void del(int id, int tableindex) {
		String selectedtable = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				break;
			case 1:
				selectedtable = USER_TABLE;
				break;
			default:
				break;
		}
		if (db == null)
			db = getWritableDatabase();
		db.delete(selectedtable, "_id=?", new String[] { String.valueOf(id) });
	}

	public void close() {
		if (db != null)
			db.close();
	}
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql1 = "drop table "+HEALTH_TABLE;
		String sql2 = "drop table "+USER_TABLE;
		db.execSQL(sql1);
		db.execSQL(sql2);
		onCreate(db);
	}

	//删除整个数据库文件
	public void deleteDatabase() {
		File dbfile = new File(DATABASE_NAME);
		if(dbfile.exists()){
			dbfile.delete();
		}
	}

	//清除数据库数据中全部数据并
	public void clearAllData() {
		if (db == null)
			db = getWritableDatabase();
		db.rawQuery("delete from 'HEALTH_TABLE'", null);
		db.rawQuery("delete from 'USER_TABLE'", null);
		db.rawQuery("update sqlite_sequence SET seq = 0 where name = 'HEALTH_TABLE'", null);
		db.rawQuery("update sqlite_sequence SET seq = 0 where name = 'USER_TABLE'", null);
	}

	//获取范围内的记录 limit A offset B 表示跳过B行选择A行
	public Cursor getScrollDataCursor(long startIndex, long maxCount, int tableindex, String loginuser) {
		String selectedtable = null;
		db = this.getReadableDatabase();
		String sql = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				sql = "select * from " + selectedtable + " where name = " + "'" + loginuser + "'" +" order by _id desc limit ? offset ?";
				break;
			case 1:
				selectedtable = USER_TABLE;
				sql = "select * from " + selectedtable +" order by _id desc limit ? offset ?";
				break;
			default:
				break;
		}
		String[] selectionArgs = {String.valueOf(startIndex), String.valueOf(maxCount)};
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		return cursor;
	}
	//获取数据库的记录数
	public long getDbSize(int tableindex){
		String selectedtable = null;
		db = this.getReadableDatabase();
		String sql = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				sql = "select count(*) from " + selectedtable;
				break;
			case 1:
				selectedtable = USER_TABLE;
				sql = "select count(*) from " + selectedtable;
				break;
			default:
				break;
		}
		int count = 0;
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		return count;
	}

	//获取数据库的记录数
	public long getRecordsNumberInDB(int tableindex,String loginuser){
		String selectedtable = null;
		db = this.getReadableDatabase(); //leakage
		String sql = null;
		switch (tableindex) {
			case 0:
				selectedtable = HEALTH_TABLE;
				sql = "select count(*) from " + selectedtable + " where name = " + "'"+loginuser +"'";
				break;
			case 1:
				selectedtable = USER_TABLE;
				sql = "select count(*) from " + selectedtable;
				break;
			default:
				break;
		}
		int count = 0;
		Cursor cursor = db.rawQuery(sql,null);
		if(cursor.moveToNext()){
			count = cursor.getInt(0);
		}
		return count;
	}

}
