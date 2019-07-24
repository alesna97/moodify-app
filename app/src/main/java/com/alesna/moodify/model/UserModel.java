package com.alesna.moodify.model;

import android.util.Log;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserModel{

    private String userId, userName, ProfilePict, email, birthDate, product;

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public void setProfilePict(String profilePict) {
        ProfilePict = profilePict;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getUserId() {
        return userId;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public String getEmail() {
        return email;
    }

    public String getProduct() {
        return product;
    }

    public String getProfilePict() {
        return ProfilePict;
    }

    public String getUserName() {
        return userName;
    }

    public void getUserSpotify(SpotifyService spotifyService){
        spotifyService.getMe(new Callback<UserPrivate>() {
            @Override
            public void success(UserPrivate userPrivate, Response response) {
                String id = userPrivate.id;
                setUserId(id);
                setBirthDate(userPrivate.birthdate);
                //setProfilePict(userPrivate.images.get(0).url);
                setProduct(userPrivate.product);
                setEmail(userPrivate.email);
                setUserName(userPrivate.display_name);
                Log.d("test",userPrivate.id + " - " + userPrivate.email );
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("test", error.toString());
            }
        });
    }

}
