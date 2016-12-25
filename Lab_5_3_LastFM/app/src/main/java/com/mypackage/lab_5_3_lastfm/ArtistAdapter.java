package com.mypackage.lab_5_3_lastfm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by user on 11.12.2016.
 */

public class ArtistAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<Artist> objects;

    public ArtistAdapter(Context context, ArrayList<Artist> artists) {
        ctx = context;
        objects = artists;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return objects.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.row_layout, parent, false);
        }

        Artist artist = getRegion(position);
        ((TextView) view.findViewById(R.id.name)).setText(artist.name);
        ((TextView) view.findViewById(R.id.song)).setText(artist.song);
        ((TextView) view.findViewById(R.id.amountPlayers)).setText(String.valueOf(artist.amountPlayers));
        ((TextView) view.findViewById(R.id.amountUsers)).setText(String.valueOf(artist.amountUsers));

        return view;
    }

    Artist getRegion(int position) {
        return ((Artist) getItem(position));
    }

    ArrayList<Artist> getAll() {
        return objects;
    }

}
