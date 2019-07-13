package com.alesna.moodify;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alesna.moodify.adapter.playlistAdapter;
import com.alesna.moodify.model.ClientCredentials;
import com.alesna.moodify.model.PlaylistModel;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.service.MessageEvent;
import com.github.ybq.android.spinkit.SpinKitView;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.squareup.picasso.Picasso;
import com.yinglan.shadowimageview.ShadowImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.ArtistSimple;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.Track;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    SpinKitView mSpinkitView;
    ClientCredentials client = new ClientCredentials();

    Button mtest;
    private RecyclerView mRVTracks;
    static final String KEY_AUTH_TOKEN = "new_token";

    String mToken;
    String playlistID;

    private playlistAdapter adapter;
    private ArrayList<PlaylistModel> PlaylistTracks;

    TextView txTest;
    ImageView mImgCover;
    //private String url = Server.SPOTIFY_URL + "playlists/";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);

        txTest = (TextView) v.findViewById(R.id.text);
        //mtest = (Button) v.findViewById(R.id.test);
        mImgCover = (ImageView) v.findViewById(R.id.imgcover);
        //mtest.setOnClickListener(this::ontest);
        authToSpotify();
        if (this.playlistID == null){
            txTest.setText("Playlist Not Ready");
        }else{
            onLoad("9faf9gb07ohknuy700tc8vkh6", this.playlistID);
        }
        PlaylistTracks = new ArrayList<>();
        mRVTracks = (RecyclerView) v.findViewById(R.id.rvPlaylistTrack);
        adapter = new playlistAdapter(PlaylistTracks);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext());
        mRVTracks.setLayoutManager(layoutManager);
        mRVTracks.setAdapter(adapter);
        return v;
    }
    public void onLoad(String userId, String playlistId){
        getPlaylistTracks(userId,playlistId);
        getPlaylist(userId,playlistId);
    }

    private void authToSpotify(){
        final AuthenticationRequest request =
                new AuthenticationRequest.Builder(client.getClientId(), AuthenticationResponse.Type.TOKEN, client.getRedirectUri())
                        .setScopes(new String[]{"user-read-private", "playlist-read", "playlist-read-private", "streaming"})
                        .build();

        //if (getAuthToken() == null){
            Intent intent = AuthenticationClient.createLoginActivityIntent(getActivity(), request);
            startActivityForResult(intent, client.getRequestCode());
        //}else{
            txTest.setText(mToken);
        //}

    }

    public SpotifyApi getAuthToken(){
        SharedPreferences newToken = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
        //SpotifyService spotify = getAuthToken().getService();
        mToken = newToken.getString(KEY_AUTH_TOKEN,null);
        return new SpotifyApi().setAccessToken(mToken);
    }

    public SpotifyService spotify(){
        return getAuthToken().getService();
    }

    /*public void ontest(View v) {
        String playlist = RequestSpotify.getData(getAuthToken()).toString();
        txTest.setText(playlist);
    }*/

    public void getPlaylist(String userId, String playlistId){
        spotify().getPlaylist(userId, playlistId, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                List<Image> img = playlist.images;
                Log.d("TEST", img.get(0).url);
                Picasso.get().load(img.get(0).url).into(mImgCover);
                txTest.setText(playlist.name);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TEST", "Error");
            }
        });
    }

    public void getPlaylistTracks(String userId, String playlistId){

        spotify().getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<PlaylistTrack> items = playlistTrackPager.items;
                //PlaylistTracks.clear();
                for(PlaylistTrack playlistTrack : items){
                    Log.d("TEST", playlistTrack.track.album.images.get(0).url + "," + playlistTrack.track.id + "," + playlistTrack.track.artists.get(0).name + " - " + playlistTrack.track.name + " , "+ playlistTrack.track.duration_ms);
                    String trackName = playlistTrack.track.name;
                    String artistName = playlistTrack.track.artists.get(0).name;
                    String duration = convertMilliSeconds(playlistTrack.track.duration_ms);
                    String album_url = playlistTrack.track.album.images.get(0).url;
                    //PlaylistTracks.add(new PlaylistModel(trackName,artistName,duration));
                    PlaylistTracks.add(new PlaylistModel( trackName,artistName,duration,album_url));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("TAG", error.toString());
            }
        });
    }

    public String convertMilliSeconds (long ms){
        long min = (ms / 1000 ) /60;
        long sec = (ms / 1000 ) % 60;
        return min + " : " + sec + " min";
    }

    public  void onActivityResult(int requestCode, int resultCode, Intent intent){
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == client.getRequestCode()){
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            switch (response.getType()){
                case TOKEN:
                    SharedPreferences newToken = getActivity().getSharedPreferences("token", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = newToken.edit();
                    //authTokenModel.setToken(response.getAccessToken());
                    editor.putString(KEY_AUTH_TOKEN, response.getAccessToken());
                    editor.apply();
                    //txTest.setText(response.getAccessToken());
                    break;
                case ERROR:
                    Toast toast = Toast.makeText(getActivity().getApplicationContext(), response.getError(), Toast.LENGTH_SHORT);
                    toast.show();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(SongsFragment.this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(SongsFragment.this);
        super.onStop();
    }

    @Subscribe
    public void onMessageEvent(MessageEvent event) {
        this.playlistID = event.getMessage();
        onLoad("9faf9gb07ohknuy700tc8vkh6", this.playlistID);
    }
}
