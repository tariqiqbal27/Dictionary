package com.quadosoft.dictionary;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SearchView search;
    private static DatabaseHelper myDbHelper;
    static boolean databaseOpened = false;

    SimpleCursorAdapter suggestionAdapter;

    ArrayList<History> historyList;
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    RecyclerVIewAdapterHistory historyAdapter;

    RelativeLayout emptyHistory;
    Cursor cursorHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        myDbHelper = new DatabaseHelper(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        search = findViewById(R.id.search_view);
        emptyHistory = findViewById(R.id.empty_history);
        recyclerView = findViewById(R.id.recycler_view_history);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);

        search.setOnClickListener((v)->{
            search.setIconified(false);
        });


        if(myDbHelper.checkDatabase()) {
            openDatabase();
        }
        else {
            LoadDatabaseAsync task = new LoadDatabaseAsync(this);
            task.execute();
        }

        //Simple Cursor Adapter
        final String[] from = new String[]{"en_word"};
        final int[] to = new int[] {R.id.suggestion_text};

        suggestionAdapter = new SimpleCursorAdapter(MainActivity.this,R.layout.suggestion_row,null,from,to,0) {
            @Override
            public void changeCursor(Cursor cursor) {
                super.swapCursor(cursor);
            }
        };
        search.setSuggestionsAdapter(suggestionAdapter);
        search.setOnSuggestionListener(new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int i) {
                return false;
            }

            @Override
            public boolean onSuggestionClick(int i) {
                CursorAdapter ca = search.getSuggestionsAdapter();
                Cursor cursor = ca.getCursor();
                cursor.moveToPosition(i);
                String clicked_word = cursor.getString(cursor.getColumnIndex("en_word"));
                search.setQuery(clicked_word,false);

                search.clearFocus();
                search.setFocusable(false);

                Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("en_word",clicked_word);
                intent.putExtras(bundle);
                startActivity(intent);


                return false;
            }
        });

        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                String text = search.getQuery().toString();
                Cursor c = myDbHelper.getMeaning(text);
                if(c.getCount()==0) {
                    search.setQuery("",false);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
                    builder.setTitle("Word Not Found");
                    builder.setMessage("Please search again");

                    String posText = getString(android.R.string.ok);
                    builder.setPositiveButton(posText, (dialog, which) -> {

                    });
                    String negText = getString(android.R.string.cancel);
                    builder.setNegativeButton(negText, (dialog, which) -> {
                        search.clearFocus();
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }
                else {
                    search.clearFocus();
                    search.setFocusable(false);

                    Intent intent = new Intent(MainActivity.this,WordMeaningActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("en_word",text);
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                search.setIconifiedByDefault(false); //Give suggestion list margin
                Cursor cursorSuggestion = myDbHelper.getSuggestion(s);
                suggestionAdapter.changeCursor(cursorSuggestion);
                return false;
            }
        });
        fetchHistory();

    }

    @Override
    protected void onStart() {
       // fetchHistory();
        super.onStart();

    }

    protected static void openDatabase() {
        try {
            myDbHelper.openDatabase();
            databaseOpened = true;
        }catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.action_settings) {
            Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if(id==R.id.action_exit) {
            System.exit(0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void fetchHistory(){
        historyList = new ArrayList<>();
        historyAdapter = new RecyclerVIewAdapterHistory(historyList,this);
        recyclerView.setAdapter(historyAdapter);
        History h;
        if(databaseOpened) {
            cursorHistory = myDbHelper.getHistory();
            if(cursorHistory.moveToFirst()) {
                do{
                    h = new History(cursorHistory.getString(cursorHistory.getColumnIndex("word")));
                    historyList.add(h);

                }while (cursorHistory.moveToNext());
            }
            historyAdapter.notifyDataSetChanged();

            if(historyAdapter.getItemCount()==0) {
               emptyHistory.setVisibility(View.VISIBLE);
            }
            else {
               emptyHistory.setVisibility(View.GONE);
            }


        } // Close Outer if
    }// close function

    @Override
    protected void onResume() {
        super.onResume();
        fetchHistory();
    }
}
