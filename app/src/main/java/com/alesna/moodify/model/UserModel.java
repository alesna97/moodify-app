package com.alesna.moodify.model;

public class UserModel {

    private String playlist_id, playlist_id_spotify;

    public void setPlaylist_id(String playlist_id) {
        this.playlist_id = playlist_id;
    }

    public String getPlaylist_id() {
        return playlist_id;
    }

    public void setPlaylist_id_spotify(String playlist_id_spotify) {
        this.playlist_id_spotify = playlist_id_spotify;
    }

    public String getPlaylist_id_spotify() {
        return playlist_id_spotify;
    }
}
