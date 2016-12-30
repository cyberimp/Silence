package com.kalinasoft.silence;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class Loading extends AppCompatActivity {

    String[] wordList;
    private FloatingActionButton fab;
    private ListView lv;
    private String lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wordList = getIntent().getStringArrayExtra("word_list");

        lv = (ListView) findViewById(R.id.languages);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice,
                this.getResources().getStringArray(R.array.languages_pretty)));

        lv.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
        lv.setItemChecked(0, true);

        fab = (FloatingActionButton) findViewById(R.id.fab_download);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doPhraseSelect();
                //doDownload();
            }
        });

    }

    void doPhraseSelect() {
        lang = (Loading.this.getResources().getStringArray(R.array.languages)[lv.getCheckedItemPosition()]);
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_multiple_choice,
                wordList));
        lv.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        for (int i = 0; i < lv.getCount(); i++) {
            lv.setItemChecked(i, true);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doDownload();
            }
        });

    }

    void doDownload() {
        int realsize = 0;
        SparseBooleanArray arr = lv.getCheckedItemPositions();
        for (int i = 0; i < arr.size(); i++) {
            if (arr.valueAt(i))
                realsize++;
        }
        if (realsize > 0) {
            String[] words = new String[realsize];
            int index = 0;
            for (int i = 0; i < arr.size(); i++) {
                if (arr.valueAt(i)) {
                    words[index] = wordList[arr.keyAt(i)];
                    index++;
                }

            }

            Log.d("asdf", "doDownload: " + words.length);
            fab.setVisibility(View.INVISIBLE);
            lv.setVisibility(View.INVISIBLE);
            ProgressBar pb = (ProgressBar) findViewById(R.id.progressBar);
            TextView done = (TextView) findViewById(R.id.done);
            pb.setVisibility(View.VISIBLE);
            DownloadTask task = new DownloadTask(Loading.this, pb, done, lang);
            task.execute(words);
        }

    }

}
