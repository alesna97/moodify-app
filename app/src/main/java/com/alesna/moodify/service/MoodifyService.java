package com.alesna.moodify.service;

import com.alesna.moodify.model.FcmModel;
import com.alesna.moodify.model.MoodMoodifyModel;
import com.alesna.moodify.model.UserMoodifyModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;


public interface MoodifyService {

    String API_KEY = "?key=kmzway87aamoodify";

    @FormUrlEncoded
    @POST("api/user"+API_KEY)
    Call<UserMoodifyModel> getUserById(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/history-mood"+API_KEY)
    Call<MoodMoodifyModel> PostMood(@Field("id") String id, @Field("id_mood") String id_mood, @Field("heart_rate") String heart_rate);

    @FormUrlEncoded
    @POST("api/get-history-mood"+API_KEY)
    Call<MoodMoodifyModel> getMoodById(@Field("id") String id);

    @FormUrlEncoded
    @POST("api/send-fcm-notification"+API_KEY)
    Call<FcmModel> sendFcmNotification(@Field("token") String token, @Field("heart_rate") String heart_rate,@Field("id_mood") String id_mood);
}
