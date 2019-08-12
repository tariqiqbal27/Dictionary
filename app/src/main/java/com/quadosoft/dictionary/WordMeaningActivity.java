package com.quadosoft.dictionary;

import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import com.quadosoft.dictionary.fragment.FragmentAntonyms;
import com.quadosoft.dictionary.fragment.FragmentDefinition;
import com.quadosoft.dictionary.fragment.FragmentExample;
import com.quadosoft.dictionary.fragment.FragmentSynonyms;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordMeaningActivity extends AppCompatActivity {

    private ViewPager viewPager;
    DatabaseHelper myDbHelper;
    Cursor c = null;
    public String enWord,enDefinition,example,synonyms,antonyms;
    TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_meaning);

        Intent intent = getIntent();
        String action = getIntent().getAction();
        String type = getIntent().getType();

        if(Intent.ACTION_SEND.equals(action) && type!=null) {
            if("text/plain".equals(type)) {
                String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                if(sharedText!=null) {
                    Pattern p = Pattern.compile("[A-Za-z ]{1,25}");
                    Matcher m = p.matcher(sharedText);
                    if(m.matches()) {
                        enWord = sharedText;
                    }
                    else {
                        enWord = "Not Available";
                    }
                }
            }

        }

        //Recieved Values
        Bundle bundle = getIntent().getExtras();
        enWord = bundle.getString("en_word");

        myDbHelper = new DatabaseHelper(this);
        try {

            myDbHelper.openDatabase();
        }catch (SQLException e) {throw  e;}

        c = myDbHelper.getMeaning(enWord);
        if(c.moveToFirst()) {
            enDefinition = c.getString(c.getColumnIndex("en_definition"));
            example = c.getString(c.getColumnIndex("example"));
            synonyms = c.getString(c.getColumnIndex("synonyms"));
            antonyms = c.getString(c.getColumnIndex("antonyms"));
            myDbHelper.insertHistory(enWord);
        }
        else {
            enWord = "Not Available";
        }


        ImageButton btnSpeak = findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener((v)-> {

            tts = new TextToSpeech(WordMeaningActivity.this,(status -> {
                if(status==TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.getDefault());
                    if(result==TextToSpeech.LANG_MISSING_DATA||result== TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.e("Error", "onCreate: Language Not Supported");
                    }
                    else {
                        tts.speak(enWord,TextToSpeech.QUEUE_FLUSH,null,null);
                    }
            }
                else {
                    Log.e("Error", "onCreate: Initialization Failed");
                }
            }));
        });

        Toolbar toolbar = findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(enWord);

        toolbar.setNavigationIcon(R.drawable.ic_back);

        viewPager = findViewById(R.id.tab_viewpager);
        if(viewPager!=null) {
            setupViewPager(viewPager);
        }

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public Fragment getItem(int i) {
            return mFragmentList.get(i);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public CharSequence getPageTitle(int pos) {
            return mFragmentTitleList.get(pos);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new FragmentDefinition(),"Definition");
        adapter.addFrag(new FragmentSynonyms(),"Synonyms");
        adapter.addFrag(new FragmentAntonyms(),"Antonyms");
        adapter.addFrag(new FragmentExample(),"Example");
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
