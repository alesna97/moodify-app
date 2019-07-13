package com.alesna.moodify.service;

import java.util.ArrayList;
import java.util.List;

public class Mood {
    String playlist_uri;
    String mood;
    public void setPlaylist_uri(String playlist_uri) {
        this.playlist_uri = playlist_uri;
    }

    public String getPlaylist_uri() {
        return playlist_uri;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }

    public void getMoodResult(int mHeartRate){
        if (mHeartRate >= 70 && mHeartRate <95){
            playlist_uri = "37i9dQZF1DX6wbVzPMSvwH";
            mood = "Bahagia";
        }else if (mHeartRate >= 95 && mHeartRate <= 120){
            playlist_uri = "37i9dQZF1DX3rxVfibe1L0";
            mood = "Marah";
        }else if (mHeartRate >= 80 && mHeartRate <= 100){
            playlist_uri = "37i9dQZF1DX86qIyFMUwi4";
            mood = "Sedih";
        }else if (mHeartRate >= 60 && mHeartRate <= 80){
            playlist_uri = "37i9dQZF1DWTRkBYeInhLG";
            mood = "Rileks";
        }
        this.setPlaylist_uri(playlist_uri);
        this.setMood(mood);
    }
}
