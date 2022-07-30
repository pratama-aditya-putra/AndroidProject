package com.example.temp2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "resep.db";
    private static final int DATABASE_VERSION = 1;

    public Database (Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE resep("
            + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
            + "author TEXT,"
            + "judul TEXT, "
            + "bahan TEXT, "
            + "langkah TEXT, "
            + "keterangan TEXT," +
                "image TEXT)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db0, int db1, int db2) {
        db0.execSQL("DROP TABLE IF EXISTS resep");
        onCreate(db0);
    }
}
