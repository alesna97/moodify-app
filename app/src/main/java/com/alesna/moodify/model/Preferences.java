package com.alesna.moodify.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Preferences {

    static final String PLAYLIST_ID = "playlist_id";
    static final String ID_USER = "id_user";
    static final String FCM_TOKEN = "fcm_token";
    static final String USERNAME = "username";
    static final String SPOTIFY_TOKEN = "token";
    public static final String session_status = "session_status";
    public static final String first_time = "first_time";

    public static SharedPreferences getSharedPreferences( Context context){
        return PreferenceManager.getDefaultSharedPreferences(context);
    }


    //ID PLAYLIST
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

    // ID USER
    public static void setIdUser(Context context, String id_user){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(ID_USER,id_user);
        editor.apply();

    }

    public static String getIdUser(Context context){
        return getSharedPreferences(context).getString(ID_USER,"");
    }

    public static void clearIdUser(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(ID_USER);
    }

    // USERNAME

    public static void setUsername(Context context, String username){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(USERNAME,username);
        editor.apply();

    }

    public static String getUsername(Context context){
        return getSharedPreferences(context).getString(USERNAME,"");
    }

    public static void clearUsername(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(USERNAME);
    }

    // FCM TOKEN
    public static String getFcmToken(Context context){
        return getSharedPreferences(context).getString(FCM_TOKEN,"");
    }

    public static void clearFcmToken(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(FCM_TOKEN);
    }

    public static void setFcmToken(Context context, String token){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(FCM_TOKEN,token);
        editor.apply();

    }

    // SPOTIFY TOKEN
    public static String getSpotifyToken(Context context){
        return getSharedPreferences(context).getString(SPOTIFY_TOKEN,"");
    }

    public static void clearSpotifyToken(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.remove(SPOTIFY_TOKEN);
    }

    public static void setSpotiftoken(Context context, String token){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString(SPOTIFY_TOKEN,token);
        editor.apply();

    }

    // SESSION
    public static Boolean getSession(Context context){
        return getSharedPreferences(context).getBoolean(session_status, false);
    }

    public static void clearSession(Context context){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(session_status,false);
        editor.remove(session_status);
        editor.clear();
        editor.commit();
    }

    public static void setSession(Context context, Boolean status){
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean(session_status,status);
        editor.apply();

    }


}
