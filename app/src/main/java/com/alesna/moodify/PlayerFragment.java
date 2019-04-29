package com.alesna.moodify;


import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.spotify.protocol.client.ErrorCallback;
import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.Image;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Repeat;
import com.yinglan.shadowimageview.ShadowImageView;

import static android.content.ContentValues.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerFragment extends Fragment {

    private static final String CLIENT_ID = "0b8bdd412f544defb36bb6bb19f5f2a7";
    private static final String REDIRECT_URI = "com.alesna.moodify://callback";

    private SpotifyAppRemote mSpotifyAppRemote;
    ImageButton mConnectButton, mPlaybutton, mPauseButton, mNextButton, mPrevButton,
            mRepeatButton, mShuffleButton;
    SeekBar mSeekBar;
    TextView nowPlaying, mArtist, mTitle, mStatus, mStartDuration, mEndDuration;
    ShadowImageView mCoverImage;
    TrackProgressBar mTrackProgressBar;
    SpinKitView mSpinkitView;

    private final ErrorCallback mErrorCallback = throwable -> Toast.makeText(getActivity().getApplicationContext(), "error",Toast.LENGTH_SHORT).show();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player, container, false);

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
        return v;
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
        mSpotifyAppRemote.getPlayerApi().play("spotify:user:spotify:playlist:37i9dQZF1DX86qIyFMUwi4");
    }

    //connect wearable device
    public void onConnectButton(View v){
        connect();
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

    @Override
    public void onStart() {
        super.onStart();
        connect();
    }
}
