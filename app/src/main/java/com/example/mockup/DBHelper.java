package com.example.mockup;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
    public static final String DATABASE_NAME = "database.db";

    public DBHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS NodesList " +
                "(node_id INTEGER PRIMARY KEY, " +
                "title TEXT, " +
                "time TEXT," +
                "current_episode INTEGER," +
                "total_episodes INTEGER," +
                "status INTEGER," +
                "bookmarked INTEGER)");

        //db.execSQL("INSERT INTO NodesList(title,time,current_episode,total_episodes,status,bookmarked) VALUES('Shadows House','Monday-09:10',0,10,1,0)");
        //db.execSQL("INSERT INTO NodesList(title,time,current_episode,total_episodes,status,bookmarked) VALUES('86','Saturday-21:30',8,24,1,1)");
        //db.execSQL("INSERT INTO NodesList(title,time,current_episode,total_episodes,status,bookmarked) VALUES('Osamake','Wednesday-20:20',8,12,1,1)");
//        ('86','Saturday-21:30',8,24,1,1)
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS NodesList");
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db,oldVersion,newVersion);
    }
}
