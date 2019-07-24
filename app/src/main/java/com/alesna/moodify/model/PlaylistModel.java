package com.alesna.moodify.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PlaylistModel {

    private String artist,song,playlist_id,track_id,duration,album_url;

    public PlaylistModel(String song,String artist,String duration,String album_url){
        this.song = song;
        this.artist = artist;
        this.duration = duration;
        this.album_url = album_url;
    };

    public void setAlbum_url(String album_url) {
        this.album_url = album_url;
    }

    public String getAlbum_url() { return album_url; }

    public String getArtist() {
        return artist;
    }

    public String getDuration() {
        return duration;
    }

    public String getTrack() {
        return song;
    }

    public String getPlaylist_Id() { return playlist_id; }

    public String getTrack_id() { return playlist_id; }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(String duration) { this.duration = duration; }

    public void setTrack(String song) {
        this.song = song;
    }

    public void setPlaylist_Id(String playlist_id) { this.playlist_id = playlist_id; }

    public void setTrack_id(String track_id) { this.track_id = track_id; }






}
