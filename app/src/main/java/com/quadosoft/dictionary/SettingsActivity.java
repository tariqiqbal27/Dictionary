package com.quadosoft.dictionary;

import android.database.SQLException;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    DatabaseHelper myDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Settings");

        toolbar.setNavigationIcon(R.drawable.ic_back);

        TextView clearHistory = findViewById(R.id.clear_history);
        clearHistory.setOnClickListener((v)->{
            myDbHelper = new DatabaseHelper(this);
            try {
                myDbHelper.openDatabase();
            }catch (SQLException e) {e.printStackTrace();}
            showAlertDialog();
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
    public void showAlertDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this,R.style.MyDialogTheme);
        builder.setTitle("Are you Sure?");
        builder.setMessage("All History will be Deleted");

        String positiveText = "Yes";
        String negativeText = "No";
        builder.setPositiveButton(positiveText,(dialog, which) ->
            myDbHelper.deleteHistory());
        builder.setNegativeButton(negativeText,(dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
