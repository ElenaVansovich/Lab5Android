package com.mypackage.lab_5_3_lastfm;

/**
 * Created by user on 11.12.2016.
 */

public class Artist {
    String name;
    String song;
    int amountPlayers;
    int amountUsers;

    public Artist(){}

    public Artist(String name, String song, int amountPlayers, int amountUsers){
        this.name = name;
        this.song = song;
        this.amountPlayers = amountPlayers;
        this.amountUsers = amountUsers;
    }

}
