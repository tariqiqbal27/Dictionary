package com.quadosoft.dictionary;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {
    private String DB_PATH = null;
    private static String DB_NAME = "eng_dictionary.db";
    private SQLiteDatabase myDatabase;
    private final Context myContext;

    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null,2);
        this.myContext = context;
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = context.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + context.getPackageName() + "/databases/";
        }
        //this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "database/";
    }

    public void createDatabase() throws IOException {
        boolean dbExists = checkDatabase();
        if(!dbExists) {
            this.getReadableDatabase();
            try {
                copyDatabase();
            }catch (IOException e) {throw new Error("Error Copying Database");}
        }
    }

    public boolean checkDatabase() {
        boolean checkDB = false;
            String myPath = DB_PATH + DB_NAME;
            //checkDB = SQLiteDatabase.openDatabase(myPath,null,SQLiteDatabase.OPEN_READONLY);
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        
        //return checkDB!=null ? true:false;
        return checkDB;
    }

    private void copyDatabase() throws IOException {
        InputStream inputStream = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[1024];
        int length;
        while ((length = inputStream.read(buffer))>0) {
            myOutput.write(buffer,0,length);
        }
        myOutput.flush();
        myOutput.close();
        inputStream.close();
        Log.e("Database Copy", "Database Copied");
    }

    public void openDatabase() throws SQLException {
        String path = DB_PATH + DB_NAME;
        myDatabase = SQLiteDatabase.openDatabase(path,null,SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if(myDatabase!=null)
            myDatabase.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try{
            this.getReadableDatabase();
            myContext.deleteDatabase(DB_NAME);
            copyDatabase();
        }catch (IOException e){e.printStackTrace();}
    }

    public Cursor getMeaning(String text) {
        Cursor c = myDatabase.rawQuery("SELECT en_definition,example,synonyms,antonyms FROM words WHERE en_word==UPPER('"+text+"')",null);
        return c;
    }
    public Cursor getSuggestion(String text) {
        Cursor c = myDatabase.rawQuery("SELECT _id,en_word FROM words WHERE en_word LIKE '"+text+"%' LIMIT 50",null);
        return c;
    }
    public void insertHistory(String text) {
        this.getWritableDatabase().execSQL("INSERT INTO history(word) VALUES(UPPER('"+text+"'))");
    }

    public Cursor getHistory() {
        Cursor c = myDatabase.rawQuery("SELECT DISTINCT word FROM history ORDER BY _id DESC",null);
        return c;
    }
    public void deleteHistory() {
        this.getWritableDatabase().execSQL("DELETE FROM history");
    }
}
