package com.alesna.moodify.service;

import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RequestSpotify {

    public static JSONObject getData(String token){

            OkHttpClient client = new OkHttpClient();
            RequestSpotify requestSpotify = new RequestSpotify();
            Response response = null;

        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            final Request request = new Request.Builder()
                    .url("https://api.spotify.com/v1/playlists/37i9dQZF1DX6wbVzPMSvwH/tracks")
                    .addHeader("Authorization","Bearer " + token)
                    .build();
            try {
                response = client.newCall(request).execute();
                String jsonData = response.body().string();
                try {
                    JSONObject object = new JSONObject(jsonData);
                    return object;
                } catch (JSONException e){
                    e.printStackTrace();
                }
            } catch (IOException e){
                e.printStackTrace();
            }
        }
        return null;
    }
}
