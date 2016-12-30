package com.kalinasoft.silence;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.URLUtil;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ShowActivity extends Activity {

    private String[] s;
    private TextView tv;
    private int selection;
    private String filename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_show);
        s = getIntent().getStringArrayExtra("wordlist");
        tv = (TextView)findViewById(R.id.textView);
        selection = getIntent().getIntExtra("selection",0);
        if (selection == -1)
            selection = 0;
        setText();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.play_fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String word = s[selection];
                File filePath = new File(ShowActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/"
                        + word + ".mp3");
                try {
                    MediaPlayer mp = new MediaPlayer();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            mediaPlayer.release();
                        }
                    });
                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mediaPlayer) {
                            mediaPlayer.start();

                        }
                    });
                    FileInputStream is = new FileInputStream(filePath);
                    mp.setDataSource(is.getFD());
                    mp.prepare();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });



    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (selection < s.length-1) {
                        selection++;
                        setText();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (selection > 0) {
                        selection--;
                        setText();
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    private void setText() {
        String text = s[selection];
        tv.setText(text);
        String prettyName = URLUtil.guessFileName("http://asdf.com/" + text + ".mp3", null, null);
        filename = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + prettyName;
        File f = new File(filename);
        View v = findViewById(R.id.play_fab);
        if (f.exists())
            v.setVisibility(View.VISIBLE);
        else
            v.setVisibility(View.INVISIBLE);

    }
}
