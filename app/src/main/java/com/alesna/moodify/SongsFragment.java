package com.alesna.moodify;


import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alesna.moodify.adapter.playlistAdapter;
import com.alesna.moodify.model.AuthToken;
import com.alesna.moodify.model.PlaylistModel;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.model.UserModel;
import com.alesna.moodify.service.PlaylistIdEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Pager;
import kaaes.spotify.webapi.android.models.Playlist;
import kaaes.spotify.webapi.android.models.PlaylistTrack;
import kaaes.spotify.webapi.android.models.UserPrivate;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    private RecyclerView mRVTracks;
    private TextView txTitle, txDesc;
    private ImageView mImgCover;
    private String userId;
    private playlistAdapter adapter;
    private ArrayList<PlaylistModel> PlaylistTracks;
    private SpotifyService spotifyService;
    private boolean newPlaylist = false;
    private UserModel userModel;
    private LinearLayout bgPlaylistTitle;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);

        txTitle = (TextView) v.findViewById(R.id.playlist_title);
        txDesc = (TextView) v.findViewById(R.id.desc);
        mImgCover = (ImageView) v.findViewById(R.id.imgcover);
        mRVTracks = (RecyclerView) v.findViewById(R.id.rvPlaylistTrack);
        bgPlaylistTitle = (LinearLayout) v.findViewById(R.id.bgPlaylistInfo);
        PlaylistTracks = new ArrayList<>();
        adapter = new playlistAdapter(PlaylistTracks);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager( getContext());
        mRVTracks.setLayoutManager(layoutManager);
        mRVTracks.setAdapter(adapter);
        return v;
    }

    public void checkRecentPlaylist(SpotifyService service){
        if (newPlaylist == false) {
            if(Preferences.getPlaylistId(getContext()) == null){
                txTitle.setText("You're just arrived here, go scan your mood");
            }else{
                txTitle.setText(userModel.getUserId());
                onLoad(service,userModel.getUserId(), Preferences.getPlaylistId(getContext()));
            }
        }
    }

    public void onLoad(SpotifyService service, String userId, String playlistId){
        getPlaylistTracks(service,userId,playlistId);
        getPlaylist(service,userId,playlistId);
    }

    public void setService(SpotifyService spotifyService){
        this.spotifyService = spotifyService;
    }

    public SpotifyService getService(){
        return spotifyService;
    }

    public void getPlaylist(SpotifyService service, String userId, String playlistId){
        service.getPlaylist(userId, playlistId, new Callback<Playlist>() {
            @Override
            public void success(Playlist playlist, Response response) {
                List<Image> img = playlist.images;
                Log.d("TEST", img.get(0).url);
                Picasso.get().load(img.get(0).url).into(mImgCover);
                Picasso.get().load(img.get(0).url).transform(new BlurTransformation(getContext(),25,1))
                        .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        bgPlaylistTitle.setBackground(new BitmapDrawable(bitmap));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        Log.d("test", errorDrawable.toString());
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

                txTitle.setText(playlist.name);
                txDesc.setText(playlist.description);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("TEST", error.toString());
            }
        });
    }

    public void getPlaylistTracks(SpotifyService service,String userId, String playlistId){
        PlaylistTracks.clear();
        service.getPlaylistTracks(userId, playlistId, new Callback<Pager<PlaylistTrack>>() {
            @Override
            public void success(Pager<PlaylistTrack> playlistTrackPager, Response response) {
                List<PlaylistTrack> items = playlistTrackPager.items;
                //PlaylistTracks.clear();
                for(PlaylistTrack playlistTrack : items){
                    Log.d("TEST", playlistTrack.track.album.images.get(0).url
                            + "-" + playlistTrack.track.id
                            + "-" + playlistTrack.track.artists.get(0).name
                            + "-" + playlistTrack.track.name
                            + "-" + playlistTrack.track.duration_ms);

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
    public void onMessageEvent(PlaylistIdEvent event) {
        onLoad(getService(),userModel.getUserId(), event.getPlaylistId());
        newPlaylist = true;
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onEvent(AuthToken event) {
        setService(event.spotifyService());
        userModel = new UserModel();
        userModel.getUserSpotify(event.spotifyService());
        checkRecentPlaylist(getService());
    }
}
