package com.example.double2.studentinfomanager.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DateBaseHelper extends SQLiteOpenHelper {

    public static final String CreateInfo = "create table company ("
            + "number text , "
            + "gender text , "
            + "name text,"
            + "birth text,"
            + "native_place text,"
            + "specialty text,"
            + "grade text)";

    public DateBaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CreateInfo);
    }

    @Override
    public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub

    }


}