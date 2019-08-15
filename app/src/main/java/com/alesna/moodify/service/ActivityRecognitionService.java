package com.alesna.moodify.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;
import com.alesna.moodify.model.FcmModel;
import com.alesna.moodify.model.Preferences;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityRecognitionService extends IntentService {

    private static final String TAG = "ActivityRecognitionServ";

    public ActivityRecognitionService(){
        super("ActivityRecognitionService");
    }

    public ActivityRecognitionService(String name){
        super(name);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)) {
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            handleDetectedActivity( result.getProbableActivities() );
        }
    }


    private void handleDetectedActivity(List<DetectedActivity> probableActivities){
        for(DetectedActivity activity:probableActivities){
            switch (activity.getType()){
                case DetectedActivity.IN_VEHICLE : {
                    Log.d(TAG,"handleDetectedActivity : IN_VEHICLE " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Driving "+activity.getConfidence()+" %"));
                        //PostActivity("Driving");
                    }
                    break;
                }case DetectedActivity.ON_BICYCLE : {
                    Log.d(TAG,"handleDetectedActivity : ON_BICYCLE " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Cycling "+activity.getConfidence()+" %"));
                        //PostActivity("Cycling");
                    }
                    break;
                }case DetectedActivity.ON_FOOT : {
                    Log.d(TAG,"handleDetectedActivity : ON_FOOT " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("On Foot "+activity.getConfidence()+" %"));
                        //PostActivity("On Foot");
                    }
                    break;
                }case DetectedActivity.RUNNING: {
                    Log.d(TAG,"handleDetectedActivity : RUNNING " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Running "+activity.getConfidence()+" %"));
                        //PostActivity("Running");
                    }
                    break;
                }case DetectedActivity.STILL : {
                    Log.d(TAG,"handleDetectedActivity : STILL " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Laying "+activity.getConfidence()+" %"));
                        //PostActivity("Laying");
                    }
                    break;
                }case DetectedActivity.WALKING : {
                    Log.d(TAG,"handleDetectedActivity : WALKING " + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Walking "+activity.getConfidence()+" %"));
                        //PostActivity("Walking");
                    }
                    break;
                }case DetectedActivity.TILTING : {
                    Log.d(TAG,"handleDetectedActivity : TILTING" + activity.getConfidence());
                    if (activity.getConfidence() >= 75){
                        EventBus.getDefault().postSticky(new EventActivityType("Tilting "+activity.getConfidence()+" %"));
                        //PostActivity("Tilting");
                    }
                    break;
                }case DetectedActivity.UNKNOWN : {
                    Log.d(TAG,"handleDetectedActivity : UNKNOWN" + activity.getConfidence());
                    break;
                }
            }
        }

    }

    private void PostActivity(String activity){
        MoodifyService service = ApiClient.retrofitClient().create(MoodifyService.class);
        Call<FcmModel> call = service.sendFcmNotificationActivity(Preferences.getFcmToken(getApplicationContext()),activity);
        call.enqueue(new Callback<FcmModel>() {
            @Override
            public void onResponse(Call<FcmModel> call, Response<FcmModel> response) {
                FcmModel res = response.body();
                if(response.isSuccessful()){
                    Log.d("test",res.getSuccess());
                }else{
                    Log.d("test",res.getSuccess());
                }
            }

            @Override
            public void onFailure(Call<FcmModel> call, Throwable t) {
                Log.d(TAG,t.getMessage());
            }
        });
    }
}
