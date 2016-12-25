package com.mypackage.lab_5_3_lastfm;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ListView list;
    EditText artistName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = (ListView) findViewById(R.id.listView);
        artistName = (EditText) findViewById(R.id.searchView);
        dbHelper = new DBHelper(this);

        if(!hasConnection()){
            Toast.makeText(this, "Start offline", Toast.LENGTH_SHORT).show();
        } else{
            LoadTask loadTask = new LoadTask(this, this);
            loadTask.execute("queen", "korn", "metallica");
        }
    }

    public boolean hasConnection()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(MainActivity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public void filter(View view) {
        String singer = "";
        try {
            singer = this.artistName.getText().toString();
            updateList(singer);
        } catch (NumberFormatException ex) {
            Toast.makeText(this, "artist", Toast.LENGTH_SHORT).show();
            return;
        }

    }
    public void updateList(String singer) {
        ArtistAdapter artistAdapter;

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor c;
        if(singer.length() == 0)
            c = db.query("tracks", null, null, null, null, null, null);
        else
            c = db.query("tracks", null, "lower(" + "singer"
                    + ") = ?", new String[]{singer.toLowerCase()}, null, null, null, null);

        ArrayList<Artist> tracks = new ArrayList<>();
        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int trackNameColumnIndex = c.getColumnIndex("song");
            int artistNameColumnIndex = c.getColumnIndex("singer");
            int listenersCountColumnIndex = c.getColumnIndex("amount_users");
            int playCountColumnIndex = c.getColumnIndex("amount_plays");

            do {
                // получаем значения по номерам столбцов
                Artist track = new Artist(c.getString(artistNameColumnIndex),c.getString(trackNameColumnIndex),
                        c.getInt(playCountColumnIndex), c.getInt(listenersCountColumnIndex));
                tracks.add(track);
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        }
        c.close();
        if(tracks.size() == 0){
            LoadTask loadTask = new LoadTask(this, this);
            loadTask.execute(singer);
        }
        else {
            artistAdapter = new ArtistAdapter(this, tracks);
            list.setAdapter(artistAdapter);
        }

    }
}
