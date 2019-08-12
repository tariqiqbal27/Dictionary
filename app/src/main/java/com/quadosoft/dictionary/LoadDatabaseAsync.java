package com.quadosoft.dictionary;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;

import java.io.IOException;

public class LoadDatabaseAsync  extends AsyncTask<Void,Void,Boolean> {
    private final Context context;
    private AlertDialog alertDialog;
    private DatabaseHelper myDbHelper;

    public LoadDatabaseAsync(Context conxt) {
        this.context = conxt;

    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
        myDbHelper = new DatabaseHelper(context);
        AlertDialog.Builder d = new AlertDialog.Builder(context,R.style.MyDialogTheme);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_dialog_database_copy,null);
        d.setTitle("Loading Database...");
        d.setView(dialogView);
        alertDialog = d.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        try {
            myDbHelper.createDatabase();
        }catch (IOException e) {}
        myDbHelper.close();
        return true;
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        alertDialog.dismiss();
        MainActivity.openDatabase();
    }
}
