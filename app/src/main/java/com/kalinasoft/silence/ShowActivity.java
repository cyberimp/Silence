package com.kalinasoft.silence;

import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ShowActivity extends Activity {

    private String[] s;
    private TextView tv;
    private int selection;

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
        tv.setText(s[selection]);


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
                        tv.setText(s[selection]);
                    }
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (selection > 0) {
                        selection--;
                        tv.setText(s[selection]);
                    }
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
