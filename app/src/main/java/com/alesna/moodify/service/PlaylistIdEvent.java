package com.alesna.moodify.service;

public class PlaylistIdEvent {

    public final String playlistId;

    public PlaylistIdEvent(String playlistId){
        this.playlistId = playlistId;
    }

    public String getPlaylistId() {
        return playlistId;
    }
}
