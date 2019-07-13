package com.alesna.moodify.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    static final String PLAYLIST_ID = "playlist_id";

    public static SharedPreferences getSharedPreferences( Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }



    public static void setPlaylistId(Context context, String playlist_id){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(PLAYLIST_ID,playlist_id);
        editor.apply();

    }

    public static String getPlaylistId(Context context){
        return getSharedPreferences(context).getString(PLAYLIST_ID,"");
    }

    public static void clearPlaylistID(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(PLAYLIST_ID);
    }

}
