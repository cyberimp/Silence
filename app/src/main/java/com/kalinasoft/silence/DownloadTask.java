package com.kalinasoft.silence;

import android.app.DownloadManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

/**
 * Created by Andrey Kalikin on 29.12.2016.
 */

public class DownloadTask extends AsyncTask<String, Integer, Void> {

    private Context context;
    private ProgressBar bar;
    private TextView done;
    private String lang;

    public DownloadTask(Context context, ProgressBar widget, TextView done, String lang) {
        super();
        this.context = context;
        this.bar = widget;
        this.done = done;
        this.lang = lang;
    }

    @Override
    protected Void doInBackground(String... strings) {
        DownloadManager dm = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Integer fileValue = 100 / strings.length;
        Integer value = 0;
        for (String word : strings) {
            if (isCancelled())
                break;
            String name = Uri.encode(word);
            String prettyName = URLUtil.guessFileName("http://asdf.com/" + name + ".mp3", null, null);
            File f = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + prettyName);
            Log.d("foo", "download: " + context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + prettyName);
            if (!f.exists()) {
                boolean good = false;
                int fails = 0;
                while (!good) {
                    if (isCancelled())
                        break;
                    String query = "http://translate.google.com/translate_tts?ie=UTF-8&tl=" + lang + "&client=tw-ob&q=" + name;
                    Log.d("foo", "download: " + query);
                    DownloadManager.Request request = new DownloadManager.Request(
                            Uri.parse(query));
                    request.addRequestHeader("Referer", "http://translate.google.com/");
                    request.addRequestHeader("User-Agent", "stagefright/1.2 (Linux;Android 5.0)");
                    request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, prettyName);
                    long enqueue = dm.enqueue(request);
                    boolean finished = false;
                    while (!finished) {
                        if (isCancelled())
                            break;
                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(enqueue);
                        Cursor cursor = dm.query(q);
                        if (cursor.moveToFirst()) {
                            switch (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                                case DownloadManager.STATUS_PAUSED:
                                case DownloadManager.STATUS_PENDING:
                                case DownloadManager.STATUS_RUNNING:
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;

                                case DownloadManager.STATUS_SUCCESSFUL:
                                    finished = true;
                                    if (f.exists()) {
                                        good = true;
                                    } else {
                                        //наебать меня вздумал, а?
                                        fails++;
                                    }
                                    try {
                                        Thread.sleep(3000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;

                                case DownloadManager.STATUS_FAILED:
                                    finished = true;
                                    if (f.exists()) {
                                        //наебать меня вздумал, а?
                                        good = true;
                                        try {
                                            Thread.sleep(3000);
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    }
                                    fails++;
                                    try {
                                        Thread.sleep(2000 * fails);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    break;
                            }
                        } else {
                            Log.wtf("eggog", "doInBackground: something wrong with cursor");
                        }
                        cursor.close();
                    }
                }

            }
            value += fileValue;
            publishProgress(value);
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        if (bar != null)
            bar.setProgress(values[0]);
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        bar.setVisibility(View.INVISIBLE);
        done.setVisibility(View.VISIBLE);
        super.onPostExecute(aVoid);
    }
}
