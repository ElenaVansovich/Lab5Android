package com.mypackage.lab_5_3_lastfm;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by user on 24.12.2016.
 */

public class LoadTask  extends AsyncTask<String, Void, List<Artist>> {
    private static boolean firstTime = true;
    DBHelper dbHelper;
    MainActivity mainActivity;
    private final Context context;
    private ProgressDialog progress;
    private static final String urlString =
            "http://ws.audioscrobbler.com/2.0/?method=artist.gettoptracks&artist=%s&" +
                    "api_key=905818929f0f2671325988ce97db6a87&format=json&limit=10";
    private static final String tag = "LoadTask";

    LoadTask(Context c, MainActivity mainActivity) {
        this.context = c;
        this.mainActivity = mainActivity;
    }

    @Override
    protected void onPreExecute() {
        Log.d(tag, "onPreExecute");
        super.onPreExecute();
        progress= new ProgressDialog(this.context);
        progress.setMessage("Loading");
        progress.show();
    }

    @Override
    protected void onPostExecute(List<Artist> tracks) {
        Log.d(tag, "onPostExecute");
        super.onPostExecute(tracks);
        progress.dismiss();
        saveTracks(tracks);
        mainActivity.updateList("");
    }

    @Override
    protected List<Artist> doInBackground(String... params) {
        Log.d(tag, "doInBackground");
        List<Artist> tracks = new LinkedList<>();
        for (String artist : params) {
            List<Artist> nextTracks = loadTracksByArtist(artist);
            if (nextTracks != null) {
                tracks.addAll(nextTracks);
            }
        }
        return tracks;
    }

    private void saveTracks(List<Artist> tracks) {
        Log.d(tag, "Saving " + tracks.size() + " tracks.");
        if (tracks.isEmpty()) {
            Toast.makeText(context, "Error occurred. Working with local version.", Toast.LENGTH_LONG).show();
            return;
        }
        dbHelper = new DBHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        if(firstTime) {
            db.delete("tracks", null, null);
            firstTime = false;
        }
        for (Artist track : tracks) {
            ContentValues values = new ContentValues();
            values.put("singer", track.name);
            values.put("song", track.song);
            values.put("amount_plays", track.amountPlayers);
            values.put("amount_users", track.amountUsers);
            db.insert("tracks", null, values);
        }
    }
    public static List<Artist> loadTracksByArtist(String artistName) {
        Log.d(tag, "Start load tracks by artist.");
        try {
            URL url = new URL(String.format(urlString, artistName));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            int responseCode = connection.getResponseCode();
            final StringBuilder output = new StringBuilder();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line;
                while ((line = br.readLine()) != null) {
                    output.append(line);
                }
                br.close();
            } else {
                return null;
            }
            JSONArray tracks = new JSONObject(output.toString()).getJSONObject("toptracks").getJSONArray("track");
            List<Artist> tracksList = new LinkedList<>();
            for (int i = 0; i < tracks.length(); i++) {
                JSONObject track = tracks.getJSONObject(i);
                Artist track1 = new Artist();
                track1.song = track.optString("name", "");
                track1.name = track.getJSONObject("artist").optString("name", "");
                track1.amountPlayers = track.optInt("playcount", 0);
                track1.amountUsers = track.optInt("listeners", 0);

                tracksList.add(track1);
            }
            return tracksList;
        } catch (Exception e) {
            return null;
        }
    }
}