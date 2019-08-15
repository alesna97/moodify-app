package com.alesna.moodify;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.alesna.moodify.adapter.historyMoodAdapter;
import com.alesna.moodify.model.FcmModel;
import com.alesna.moodify.model.HistoryMoodModel;
import com.alesna.moodify.model.MoodMoodifyModel;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.service.ApiClient;
import com.alesna.moodify.service.ConnWearableEvent;
import com.alesna.moodify.service.EventActivityTracking;
import com.alesna.moodify.service.EventActivityType;
import com.alesna.moodify.service.MoodifyService;
import com.alesna.moodify.service.PlaylistIdEvent;
import com.alesna.moodify.service.Mood;
import com.github.ybq.android.spinkit.SpinKitView;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;
import com.spotify.android.appremote.api.error.AuthenticationFailedException;
import com.spotify.android.appremote.api.error.CouldNotFindSpotifyApp;
import com.spotify.android.appremote.api.error.LoggedOutException;
import com.spotify.android.appremote.api.error.NotLoggedInException;
import com.spotify.android.appremote.api.error.OfflineModeException;
import com.spotify.android.appremote.api.error.SpotifyConnectionTerminatedException;
import com.spotify.android.appremote.api.error.SpotifyDisconnectedException;
import com.spotify.android.appremote.api.error.SpotifyRemoteServiceException;
import com.spotify.android.appremote.api.error.UnsupportedFeatureVersionException;
import com.spotify.android.appremote.api.error.UserNotAuthorizedException;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.yinglan.shadowimageview.ShadowImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private static final String CLIENT_ID = "0b8bdd412f544defb36bb6bb19f5f2a7";
    private static final String REDIRECT_URI = "com.alesna.moodify://callback";

    private SpotifyAppRemote mSpotifyAppRemote;
    ImageButton mConnectButton, mPlaybutton, mPauseButton, mNextButton, mPrevButton,
            mRepeatButton, mShuffleButton, mTrackButton;
    SeekBar mSeekBar;
    TextView nowPlaying, mArtist, mTitle, mStatus, mStartDuration, mEndDuration, mActivity;
    ShadowImageView mCoverImage;
    TrackProgressBar mTrackProgressBar;
    SpinKitView mSpinkitView;
    Boolean device = false;
    int mHeartRate;
    Boolean isListeningHeartRate = false;
    private SweetAlertDialog sweetAlertDialog;

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    public static final String STATUS = "STATUS_CONNECT";
    BluetoothAdapter bluetoothAdapter;
    BluetoothGatt bluetoothGatt;
    BluetoothDevice bluetoothDevice;
    TextView DeviceName;
    TextView txtState, txtByte;
    private String mDeviceName;
    private String mDeviceAddress;
    String status;
    String device_name;
    String device_address;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        initializeObjects();
        //status = getArguments().getString("STATUS_CONNECT");
        if(status == STATUS){
            mDeviceName = getArguments().getString("name");
            mDeviceAddress = getArguments().getString("add");
            DeviceName.setText(mDeviceAddress);
        }
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player, container, false);

        mActivity = (TextView) v.findViewById(R.id.txactivity);
        mTrackButton = (ImageButton) v.findViewById(R.id.btn_track);
        mTrackButton.setOnClickListener(view -> onTrackClick(view));
        DeviceName = (TextView) v.findViewById(R.id.txtPhysicalAddress);
        txtByte = (TextView) v.findViewById(R.id.txtByte);
        //txtState = (TextView) v.findViewById(R.id.txtState);
        mConnectButton = (ImageButton) v.findViewById(R.id.ConnectButton);
        mConnectButton.setOnClickListener(this::onConnectButton);
        mPlaybutton = (ImageButton) v.findViewById(R.id.PlayButton);
        mPlaybutton.setOnClickListener(this::onPlayPause);
        mPauseButton = (ImageButton) v.findViewById(R.id.PauseButton);
        mPauseButton.setOnClickListener(this::onGetCurrentMood);
        mNextButton = (ImageButton) v.findViewById(R.id.NextButton);
        mNextButton.setOnClickListener(this::onSkipNext);
        mPrevButton = (ImageButton) v.findViewById(R.id.PrevButton);
        mPrevButton.setOnClickListener(this::onSkipPrev);
        mRepeatButton = (ImageButton) v.findViewById(R.id.RepeatButton);
        mRepeatButton.setOnClickListener(this::onSetRepeat);
        mShuffleButton = (ImageButton) v.findViewById(R.id.ShuffleButton);
        mShuffleButton.setOnClickListener(this::onSetShuffle);
        nowPlaying = (TextView) v.findViewById(R.id.NowPlaying);
        mArtist = (TextView) v.findViewById(R.id.Artist);
        mTitle = (TextView) v.findViewById(R.id.Title);
        mTitle.setSelected(true);
        mStatus = (TextView) v.findViewById(R.id.Status);
        mStartDuration = (TextView) v.findViewById(R.id.startDuration);
        mEndDuration = (TextView) v.findViewById(R.id.endDuration);
        mSpinkitView = (SpinKitView) v.findViewById(R.id.spin_kit);
        mCoverImage = (ShadowImageView) v.findViewById(R.id.CoverImage);
        mSeekBar = (SeekBar) v.findViewById(R.id.seekbar);
        mTrackProgressBar = new TrackProgressBar(mSeekBar);
        sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        return v;
    }

    public void onTrackClick(View v){
        EventBus.getDefault().postSticky(new EventActivityTracking(true));
    }
    public void showLoading (SweetAlertDialog sweetAlertDialog){
        sweetAlertDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        sweetAlertDialog.setContentText("Getting your mood please wait :)");
        sweetAlertDialog.setTitleText("Scanning");
        sweetAlertDialog.show();
    }

    public void dismissLoading (SweetAlertDialog sweetAlertDialog){
        if(sweetAlertDialog.isShowing()){
            sweetAlertDialog.dismissWithAnimation();
        }
    }

    //WEARABLE DEVICE
    void startConnecting(String device_name,String device_address) {
        //if(device_name == "MI Band 3") {
            device = true;
            bluetoothDevice = bluetoothAdapter.getRemoteDevice(device_address);
            Log.v("test", "Connecting to " + device_name);
            Log.v("test", "Device name " + bluetoothDevice.getName());
            DeviceName.setText(bluetoothDevice.getName());
            bluetoothGatt = bluetoothDevice.connectGatt(getActivity().getApplicationContext(), true, bluetoothGattCallback);
        //}else{
          //  Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Wrong Device !", Toast.LENGTH_SHORT);
          //  toast.show();
        //}
    }

    void initializeObjects() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    protected void connect(){
        ConnectionParams connectionParams = new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();
        mSpinkitView.setVisibility(View.VISIBLE);
        mStatus.setText("Connecting to Spotify");
            SpotifyAppRemote.connect(getActivity().getApplicationContext(), connectionParams,
                new Connector.ConnectionListener() {

                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        PlayerFragment.this.onConnect();
                        // Subscribe to PlayerState*
                        mSpotifyAppRemote.getPlayerApi()
                                .subscribeToPlayerState()
                                .setEventCallback(mPlayerStateEventCallback);
                        mStatus.setText("Spotify Connected, Yeay!");
                        mSpinkitView.setVisibility(View.GONE);
                    }

                    // Error Handling
                    @Override
                    public void onFailure(Throwable error) {

                        if (error instanceof SpotifyRemoteServiceException) {
                            if (error.getCause() instanceof SecurityException) {
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Error, Security Exception!", Toast.LENGTH_SHORT);
                                toast.show();
                            } else if (error.getCause() instanceof IllegalStateException) {
                                Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Error, Illegal Exception!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        } else if (error instanceof NotLoggedInException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"User Not Login !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof AuthenticationFailedException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Authentication Failed !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof CouldNotFindSpotifyApp) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Spotify App Not Installed !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof LoggedOutException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"User Logged Out !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof OfflineModeException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"No Connection Available !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof UserNotAuthorizedException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"User Not Authorized !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof UnsupportedFeatureVersionException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Error, Unsupported Version !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof SpotifyDisconnectedException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Spotify Disconnected !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else if (error instanceof SpotifyConnectionTerminatedException) {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Error, Connection Timed Out !", Toast.LENGTH_SHORT);
                            toast.show();
                        } else {
                            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Connection Failed !", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        mSpinkitView.setVisibility(View.GONE);
                        PlayerFragment.this.onDisconnected();
                    }
                });
    }

    // get recent activity from spotify
    @SuppressLint("SetTextI18n")
    private final Subscription.EventCallback<PlayerState> mPlayerStateEventCallback = new Subscription.EventCallback<PlayerState>() {
        @Override
        public void onEvent(PlayerState playerState) {
            //set repeat mode
            if (playerState.playbackOptions.repeatMode == Repeat.ALL) {
                mRepeatButton.setImageResource(R.drawable.btn_repeat_all);
            } else if (playerState.playbackOptions.repeatMode == Repeat.ONE) {
                mRepeatButton.setImageResource(R.drawable.btn_repeat_one);
            } else {
                mRepeatButton.setImageResource(R.drawable.btn_repeat);
            }

            //set shuffle mode
            if(playerState.playbackOptions.isShuffling){
                mShuffleButton.setImageResource(R.drawable.btn_shuffle_on);
            }else{
                mShuffleButton.setImageResource(R.drawable.btn_shuffle);
            }

            //set progressbar
            mSeekBar.setMax((int) playerState.track.duration);
            mTrackProgressBar.setDuration(playerState.track.duration);
            mTrackProgressBar.update(playerState.playbackPosition);
            mSeekBar.setEnabled(true);

            //set image & title
            mSpotifyAppRemote.getImagesApi()
                    .getImage(playerState.track.imageUri, Image.Dimension.LARGE)
                    .setResultCallback(bitmap -> {
                        mCoverImage.setImageBitmap(bitmap);
                    });
            mArtist.setText(playerState.track.artist.name);
            mTitle.setText(playerState.track.name);

            //set update progressbar
            if (playerState.playbackSpeed > 0) {
                mTrackProgressBar.unpause();
            } else {
                mTrackProgressBar.pause();
            }

            // set button play / pause
            if (playerState.isPaused) {
                mPlaybutton.setImageResource(R.drawable.btn_play);
            } else {
                mPlaybutton.setImageResource(R.drawable.btn_pause);
            }

        }
    };

    //scan current mood
    public void onGetCurrentMood(View v){
        // Play a playlist
        //getBoundedDevice();
        if (!device){
            Toast toast = Toast.makeText(getActivity().getApplicationContext(),"Connect Device First !", Toast.LENGTH_SHORT);
            toast.show();
        }else{
            showLoading(sweetAlertDialog);
            startScanHeartRate();
            startScanHeartRate();
            startScanHeartRate();
        }
    }

    //connect wearable device
    public void onConnectButton(View v){
        //startConnecting();
        //device = true;
        if(this.device_name == null){
            Intent intent = new Intent(getActivity(), ScanDeviceActivity.class);
            getActivity().finish();
            startActivity(intent);
        }else{
            stateDisconnected();
        }
    }
    void startScanHeartRate() {

        txtByte.setText("Scanning");
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.controlCharacteristic);
        bchar.setValue(new byte[]{21, 2, 1});
        bluetoothGatt.writeCharacteristic(bchar);
    }

    void listenHeartRate() {
        BluetoothGattCharacteristic bchar = bluetoothGatt.getService(CustomBluetoothProfile.HeartRate.service)
                .getCharacteristic(CustomBluetoothProfile.HeartRate.measurementCharacteristic);
        bluetoothGatt.setCharacteristicNotification(bchar, true);
        BluetoothGattDescriptor descriptor = bchar.getDescriptor(CustomBluetoothProfile.HeartRate.descriptor);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        bluetoothGatt.writeDescriptor(descriptor);
        isListeningHeartRate = true;
    }


    public void onPlayPause(View v){
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.isPaused) {
                mSpotifyAppRemote.getPlayerApi()
                        .resume();
                mPlaybutton.setImageResource(R.drawable.btn_pause);
            } else {
                mSpotifyAppRemote.getPlayerApi()
                        .pause();
                mPlaybutton.setImageResource(R.drawable.btn_play);
            }
        });
    }

    public void onSkipNext(View v){
        mSpotifyAppRemote.getPlayerApi()
                .skipNext();
    }

    public void onSkipPrev(View v){
        mSpotifyAppRemote.getPlayerApi()
                .skipPrevious();
    }

    public void onSetRepeat(View v){
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if(playerState.playbackOptions.repeatMode == Repeat.ALL){
                mSpotifyAppRemote.getPlayerApi()
                        .setRepeat(Repeat.ONE);
                mRepeatButton.setImageResource(R.drawable.btn_repeat_one);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Repeat One", Toast.LENGTH_SHORT);
                toast.show();
            }else if(playerState.playbackOptions.repeatMode == Repeat.OFF){
                mSpotifyAppRemote.getPlayerApi()
                        .setRepeat(Repeat.ALL);
                mRepeatButton.setImageResource(R.drawable.btn_repeat_all);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Repeat All", Toast.LENGTH_SHORT);
                toast.show();
            }else if(playerState.playbackOptions.repeatMode == Repeat.ONE){
                mSpotifyAppRemote.getPlayerApi()
                        .setRepeat(Repeat.OFF);
                mRepeatButton.setImageResource(R.drawable.btn_repeat);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Repeat Off", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void onSetShuffle(View v) {
        mSpotifyAppRemote.getPlayerApi().getPlayerState().setResultCallback(playerState -> {
            if (playerState.playbackOptions.isShuffling) {
                mSpotifyAppRemote.getPlayerApi()
                        .setShuffle(false);
                mShuffleButton.setImageResource(R.drawable.btn_shuffle);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Shuffle Off", Toast.LENGTH_SHORT);
                toast.show();
            } else {
                mSpotifyAppRemote.getPlayerApi()
                        .setShuffle(true);
                mShuffleButton.setImageResource(R.drawable.btn_shuffle_on);
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), "Shuffle On", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

    }

    public void onConnect(){
        mPlaybutton.setPressed(true);
        mPauseButton.setPressed(true);
        mPrevButton.setPressed(true);
        mNextButton.setPressed(true);
    }

    public void onDisconnected(){
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
        mPlaybutton.setPressed(false);
        mPauseButton.setPressed(false);
        mPrevButton.setPressed(false);
        mNextButton.setPressed(false);

    }

    // Progress Bar
    private class TrackProgressBar {

        private static final int LOOP_DURATION = 500;
        private final SeekBar mSeekBar;
        private final Handler mHandler;


        private final SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mSpotifyAppRemote.getPlayerApi().seekTo(seekBar.getProgress());
            }
        };

        private final Runnable mSeekRunnable = new Runnable() {
            @Override
            public void run() {
                int progress = mSeekBar.getProgress();
                mSeekBar.setProgress(progress + LOOP_DURATION);
                mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
            }
        };

        private TrackProgressBar(SeekBar seekBar) {
            mSeekBar = seekBar;
            mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
            mHandler = new Handler();
        }


        private void setDuration(long duration) {
            mSeekBar.setMax((int) duration);
        }

        private void update(long progress) {
            mSeekBar.setProgress((int) progress);
        }

        private void pause() {
            mHandler.removeCallbacks(mSeekRunnable);
        }

        private void unpause() {
            mHandler.removeCallbacks(mSeekRunnable);
            mHandler.postDelayed(mSeekRunnable, LOOP_DURATION);
        }
    }

    void stateConnected() {
        bluetoothGatt.discoverServices();
        //txtState.setText("Connected");
    }

    public void postMood(String id_user, String id_mood, String heart_rate){
        MoodifyService service = ApiClient.retrofitClient().create(MoodifyService.class);
        Call<MoodMoodifyModel> call = service.PostMood(id_user,id_mood,heart_rate);
        call.enqueue(new Callback<MoodMoodifyModel>() {
            @Override
            public void onResponse(Call<MoodMoodifyModel> call, Response<MoodMoodifyModel> response) {
                MoodMoodifyModel res = response.body();
                if(response.isSuccessful()){
                    dismissLoading(sweetAlertDialog);
                    Log.d("test", res.getMessage());
                    SweetAlertDialog sweetAlertDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
                    sweetAlertDialog.setTitleText("Mood Detected");
                    sweetAlertDialog.setContentText("Are you Feeling "+ id_mood + " \n Heart Rate "+heart_rate +" Bpm");
                    sweetAlertDialog.setConfirmButton("Ok", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            sweetAlertDialog.dismissWithAnimation();
                        }
                    });
                    sweetAlertDialog.show();
                    txtByte.setText(heart_rate+" Bpm");
                }else{
                    Log.d("test", res.getMessage());
                }
            }

            @Override
            public void onFailure(Call<MoodMoodifyModel> call, Throwable t) {
                Log.d("test", t.getMessage());
            }
        });
    }

    public void sendNotification(String heart_rate,String id_mood){
        MoodifyService service = ApiClient.retrofitClient().create(MoodifyService.class);
        Call<FcmModel> call = service.sendFcmNotification(Preferences.getFcmToken(getContext()),heart_rate,id_mood);
        call.enqueue(new Callback<FcmModel>() {
            @Override
            public void onResponse(Call<FcmModel> call, Response<FcmModel> response) {
                if(response.isSuccessful()){
                    Log.d("test",response.body().getSuccess());
                }
            }

            @Override
            public void onFailure(Call<FcmModel> call, Throwable t) {

            }
        });
    }
    void stateDisconnected() {
        bluetoothGatt.disconnect();
    }

    final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            Log.v("test", "onConnectionStateChange");

            if (newState == android.bluetooth.BluetoothProfile.STATE_CONNECTED) {
                stateConnected();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                stateDisconnected();
            }

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            Log.v("test", "onServicesDiscovered");
            listenHeartRate();
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            byte[] data = characteristic.getValue();
            byte[] slice = Arrays.copyOfRange(data, 1,2);
            mHeartRate = slice[0];
            Mood mood = new Mood();
            mood.getMoodResult(mHeartRate);
            mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:"+mood.getPlaylist_uri());
            Preferences.clearPlaylistID(getContext());
            Preferences.setPlaylistId(getContext(), mood.getPlaylist_uri());
            EventBus.getDefault().post(new PlaylistIdEvent(mood.getPlaylist_uri()));
            postMood(Preferences.getIdUser(getContext()),mood.getMood(),String.valueOf(mHeartRate));
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            Log.v("test", "onCharacteristicWrite");
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            Log.v("test", "onCharacteristicChanged");
            byte[] data = characteristic.getValue();
            byte[] slice = Arrays.copyOfRange(data, 1,2);
            mHeartRate = slice[0];
            Mood mood = new Mood();
            mood.getMoodResult(mHeartRate);
            mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:"+mood.getPlaylist_uri());
            sendNotification(String.valueOf(mHeartRate),mood.getMood());
            Preferences.clearPlaylistID(getContext());
            Preferences.setPlaylistId(getContext(), mood.getPlaylist_uri());
            EventBus.getDefault().postSticky(new PlaylistIdEvent(mood.getPlaylist_uri()));
            postMood(Preferences.getIdUser(getContext()),mood.getMood(),String.valueOf(mHeartRate));
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
            Log.v("test", "onDescriptorRead");
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            Log.v("test", "onDescriptorWrite");
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
            Log.v("test", "onReliableWriteCompleted");
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
            Log.v("test", "onReadRemoteRssi");
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
            Log.v("test", "onMtuChanged");
        }

    };

    @Override
    public void onStart() {
        super.onStart();
        connect();
        EventBus.getDefault().register(this);
    }

    // UI updates must run on MainThread
    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(ConnWearableEvent event) {
        this.device_address = event.getMac_address();
        this.device_name = event.getDevice_name();
        txtByte.setText(this.device_name);
        startConnecting(event.getDevice_name(),event.getMac_address());
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(PlaylistIdEvent event) {
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:"+event.getPlaylistId());
    }
    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(EventActivityType event) {
      mActivity.setText(event.getActivityType());
    }
}
