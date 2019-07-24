package com.alesna.moodify.model;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;

public class AuthToken {

    public final String mToken;

    public AuthToken(String mToken){
        this.mToken = mToken;
    }

    public String getToken() {
        return mToken;
    }

    public SpotifyApi getAuthToken(){
        String token = this.getToken();
        return new SpotifyApi().setAccessToken(token);
    }

    public SpotifyService spotifyService(){
        return this.getAuthToken().getService();
    }

}
