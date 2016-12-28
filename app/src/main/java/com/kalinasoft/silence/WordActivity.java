package com.kalinasoft.silence;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class WordActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView lv;
    private ArrayList<String> word;
    private int selection = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        lv = (ListView) findViewById(R.id.list);

        word = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.word_list)));

        ArrayList<String> list = new ArrayList<>();
        try {
            FileInputStream fis = openFileInput("words_list");
            ObjectInputStream ois = new ObjectInputStream(fis);
            list = (ArrayList<String>) ois.readObject();
            ois.close();
            word = list;
        } catch (Exception e) {
            e.printStackTrace();
        }



        adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_single_choice,
                word);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent wordInt = new Intent(WordActivity.this,ShowActivity.class);
                String[] s = new String[adapter.getCount()];
                for (int i = 0; i < adapter.getCount(); i++) {
                    s[i] = adapter.getItem(i);
                }
                wordInt.putExtra("wordlist",s);
                wordInt.putExtra("selection",lv.getCheckedItemPosition());
                startActivity(wordInt);
            }
        });

        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            }
        });

    }

    @Override
    protected void onPause() {

        ArrayList<String> s = new ArrayList<>(adapter.getCount());
        for (int i = 0; i < adapter.getCount(); i++) {
            s.add(adapter.getItem(i));
        }
        try {
            FileOutputStream fos = openFileOutput("words_list", Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(s);
            oos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_word, menu);
        return true;
    }


    private void addNew(){
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setTitle(R.string.action_new_sentence);
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        adb.setView(input);
        adb.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String inString = input.getText().toString();
                if (adapter.getPosition(inString) == -1) {
                    if (lv.getCheckedItemPosition() != -1)
                        adapter.insert(inString,lv.getCheckedItemPosition());
                    else
                        adapter.add(inString);
                }
                else {
                    Toast.makeText(WordActivity.this, R.string.explanation_already,Toast.LENGTH_LONG).show();
                }
            }
        });
        adb.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        adb.show();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id){
            case R.id.action_add:
                addNew();
                return true;
            case R.id.action_up:
                moveUp();
                return true;

            case R.id.action_down:
                moveDown();
                return true;

            case R.id.action_delete:
                deleteItem();
                return true;

            case R.id.action_reset:
                final AlertDialog.Builder alert = new AlertDialog.Builder(this);
                alert.setTitle(R.string.action_reset);
                alert.setMessage(R.string.explanation_reset);
                alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.clear();
                        String[] s = getResources().getStringArray(R.array.word_list);
                        for (String next: s) {
                            adapter.add(next);
                        }
                        lv.setItemChecked(lv.getCheckedItemPosition(),false);
                    }
                });
                alert.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                alert.show();

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    private void deleteItem() {
        int position = lv.getCheckedItemPosition();
        String tmp = adapter.getItem(position);
        adapter.remove(tmp);
        if (position >= adapter.getCount())
            lv.setItemChecked(position,false);
    }

    private void moveDown() {
        int position = lv.getCheckedItemPosition();
        if (position >=0 && position<adapter.getCount()-1){
            String tmp = adapter.getItem(position);
            adapter.remove(tmp);
            adapter.insert(tmp,position+1);
            lv.setItemChecked(position,false);
            lv.setItemChecked(position+1,true);

        }
    }

    private void moveUp() {
        int position = lv.getCheckedItemPosition();
        if (position>0 && position<adapter.getCount()){
            String tmp = adapter.getItem(position);
            adapter.remove(tmp);
            adapter.insert(tmp,position-1);
            lv.setItemChecked(position,false);
            lv.setItemChecked(position-1,true);

        }
    }
}
