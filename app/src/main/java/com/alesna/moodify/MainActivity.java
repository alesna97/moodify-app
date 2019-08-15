package com.alesna.moodify;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alesna.moodify.model.AuthToken;
import com.alesna.moodify.model.ClientCredentials;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.model.UserModel;
import com.alesna.moodify.service.ActivityRecognitionService;
import com.alesna.moodify.service.EventActivityTracking;
import com.alesna.moodify.service.EventLogout;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionClient;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private List<ActivityTransition> transitions;
    String id, username,status;
    SharedPreferences sharedpreferences;
    private String mToken, KEY_AUTH_TOKEN = "token";
    public static final String TAG_ID = "id_user";
    public static final String TAG_USERNAME = "username";
    public static final String STATUS = "STATUS_CONNECT";
    private BluetoothAdapter mBluetoothAdapter;
    ClientCredentials client = new ClientCredentials();
    final Fragment fragment1 = new PlayerFragment();
    final Fragment fragment2 = new SongsFragment();
    final Fragment fragment3 = new MoodFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;
    ProgressBar progressbar;
    String TAG = "FIREBASE MESSAGING";
    public GoogleApiClient apiClient;
    Intent intent;
    PendingIntent pendingIntent;
    private ActivityRecognitionClient activityRecognitionClient;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        fm.beginTransaction().add(R.id.container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.container,fragment1, "1").commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.navigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        sharedpreferences = getSharedPreferences(LoginActivity.my_shared_preferences, Context.MODE_PRIVATE);
        status = getIntent().getStringExtra(STATUS);
        id = getIntent().getStringExtra(TAG_ID);
        username = getIntent().getStringExtra(TAG_USERNAME);
        authToSpotify();
        getAuthToken();
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        getFCMToken();
        apiClient = new GoogleApiClient.Builder(MainActivity.this)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(MainActivity.this)
                .addOnConnectionFailedListener(MainActivity.this)
                .build();
        apiClient.connect();
        Intent intent = new Intent(this, ActivityRecognitionService.class);
        this.startService(intent);
        pendingIntent = PendingIntent.getService(MainActivity.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void getFCMToken (){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.d("FCMTOKEN", "getInstanceId failed", task.getException());
                            return;
                        }
                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d(TAG, msg);
                        Log.d("test", token);
                        Preferences.setFcmToken(getApplicationContext(),token);
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_player:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;
            case R.id.navigation_songs:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;
            case R.id.navigation_mood:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    }

    private void authToSpotify(){
        final AuthenticationRequest request =
                new AuthenticationRequest.Builder(client.getClientId(), AuthenticationResponse.Type.TOKEN, client.getRedirectUri())
                        .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private","user-read-email","user-read-birthdate","streaming"})
                        .build();
        Intent intent = AuthenticationClient.createLoginActivityIntent(this, request);
        startActivityForResult(intent, client.getRequestCode());
    }

    public SpotifyService getAuthToken(){
        SharedPreferences newToken = this.getSharedPreferences("token", Context.MODE_PRIVATE);
        mToken = newToken.getString(KEY_AUTH_TOKEN,null);
        AuthToken authToken = new AuthToken(mToken);
        EventBus.getDefault().postSticky(authToken);
        return authToken.spotifyService();
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == client.getRequestCode()){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()){
                case TOKEN:
                    SharedPreferences newToken = this.getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = newToken.edit();
                    editor.putString(KEY_AUTH_TOKEN, response.getAccessToken());
                    editor.apply();
                    break;
                case ERROR:
                    Toast toast = Toast.makeText(this.getApplicationContext(), response.getError(), Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                default:
                    break;
            }
        }
    }

    void requestActivityTransitionUpdates(final Context context) {
        ActivityTransitionRequest request = buildTransitionRequest();
        // PendingIntent pendingIntent;  // Your pending intent to receive callbacks.
        Task task = ActivityRecognition.getClient(context)
                .requestActivityTransitionUpdates(request, pendingIntent);
        task.addOnSuccessListener(
                result -> {
                    Log.d("test","activity transaition request success");
                });
        task.addOnFailureListener(
                e -> {
                    // Handle failure...
                    Log.d("test","activity transaition request failed");
                });
    }

    ActivityTransitionRequest buildTransitionRequest() {
        List transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("test","Connected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d("ActivityRecognitionServ","error");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d("ActivityRecognitionServ",connectionResult.getErrorMessage());
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }
    @Override
    protected void onStop() {
        super.onStop();;
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventLogout event) {
        if (event.getLogout() == true){
            this.finish();
        }
    }
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventActivityTracking event) {
        if (event.getClick() == true){
            Toast.makeText(this, "Activity Tracking Started", Toast.LENGTH_SHORT).show();
            ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(apiClient, 3000, pendingIntent);
            requestActivityTransitionUpdates(this);
        }
    }

}
