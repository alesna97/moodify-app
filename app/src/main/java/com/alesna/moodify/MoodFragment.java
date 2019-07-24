package com.alesna.moodify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.alesna.moodify.adapter.historyMoodAdapter;
import com.alesna.moodify.model.AuthToken;
import com.alesna.moodify.model.HistoryMoodModel;
import com.alesna.moodify.model.MoodMoodifyModel;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.model.RecommendedModel;
import com.alesna.moodify.adapter.recommendedAdapter;
import com.alesna.moodify.model.UserMoodifyModel;
import com.alesna.moodify.service.ApiClient;
import com.alesna.moodify.service.Mood;
import com.alesna.moodify.service.MoodifyService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.PlaylistSimple;
import kaaes.spotify.webapi.android.models.PlaylistsPager;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit2.Call;

/**
 * A simple {@link Fragment} subclass.
 */
public class MoodFragment extends Fragment {
    TextView recommended, txUsername;
    RecyclerView mRvRecommended, mRvHistoryMood;
    private recommendedAdapter adapterRecomm;
    private historyMoodAdapter adapterMood;
    private ArrayList<RecommendedModel> RecommendedList;
    private ArrayList<HistoryMoodModel> HistoryMoodList;
    MoodifyService service = ApiClient.retrofitClient().create(MoodifyService.class);
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_mood, container, false);
        txUsername = (TextView) v.findViewById(R.id.username);
        recommended = (TextView) v.findViewById(R.id.recomemnded);
        onLoad();
        HistoryMoodList = new ArrayList<>();
        RecommendedList = new ArrayList<>();
        mRvHistoryMood = (RecyclerView) v.findViewById(R.id.rvHistoryMood);
        mRvRecommended = (RecyclerView) v.findViewById(R.id.rvRecommended);
        adapterRecomm = new recommendedAdapter(RecommendedList);
        adapterMood = new historyMoodAdapter(HistoryMoodList);
        RecyclerView.LayoutManager lmHistoryMood = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL,false);
        RecyclerView.LayoutManager layoutManagerRvRecomm = new LinearLayoutManager( getContext(), LinearLayoutManager.HORIZONTAL,false);
        mRvRecommended.setLayoutManager(layoutManagerRvRecomm);
        mRvRecommended.setAdapter(adapterRecomm);
        mRvHistoryMood.setLayoutManager(lmHistoryMood);
        mRvHistoryMood.setAdapter(adapterMood);

        return v;
    }

    public void onLoad(){
        getUserMoodify(Preferences.getIdUser(getContext()));
        getMoodHistory(Preferences.getIdUser(getContext()));
    }

    public void getRecommended(SpotifyService service){
        RecommendedList.clear();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("country" , "id");
        map.put("limit" , "20");
        map.put("offset","5");

        service.getPlaylistsForCategory("mood", map, new Callback<PlaylistsPager>() {
            @Override
            public void success(PlaylistsPager playlistsPager, Response response) {
                List<PlaylistSimple> items = playlistsPager.playlists.items;
                for(PlaylistSimple playlistsPager1 : items){
                    Log.d("test",playlistsPager1.id + ' ' +  playlistsPager1.name + ' ' + playlistsPager1.images.get(0).url);
                    String id = playlistsPager1.id;
                    String name = playlistsPager1.name;
                    String uriImg = playlistsPager1.images.get(0).url;
                    RecommendedList.add(new RecommendedModel(id,name,uriImg));
                    adapterRecomm.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("test",error.toString());
                Toast toast = Toast.makeText(getActivity().getApplicationContext(), error.toString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
    }

    public void getUserMoodify(String id){
        Call<UserMoodifyModel> call = service.getUserById(id);
        call.enqueue(new retrofit2.Callback<UserMoodifyModel>() {
            @Override
            public void onResponse(Call<UserMoodifyModel> call, retrofit2.Response<UserMoodifyModel> response) {
                UserMoodifyModel res = response.body();
                String username = res.getUsername();
                txUsername.setText(username);
                Log.d("test", response.body().getEmail() + " - " + response.body().getTgl_lahir() + " - " + username);
            }

            @Override
            public void onFailure(Call<UserMoodifyModel> call, Throwable t) {
                Log.d("test", t.getMessage());
            }
        });
    }

    public void getMoodHistory(String id){
        Call<MoodMoodifyModel> call = service.getMoodById(id);
        call.enqueue(new retrofit2.Callback<MoodMoodifyModel>() {
            @Override
            public void onResponse(Call<MoodMoodifyModel> call, retrofit2.Response<MoodMoodifyModel> response) {
                if (response.isSuccessful()) {
                        List<MoodMoodifyModel.list_history_mood> data = response.body().getHistorylist();
                        if (data != null){
                            for (MoodMoodifyModel.list_history_mood res : data) {
                                Log.d("test", res.getId_mood());
                                HistoryMoodList.add(new HistoryMoodModel(res.getId_user(), res.getId_mood(), res.getHeart_rate(), res.getTanggal_mood(), res.getWaktu_mood()));
                                adapterMood.notifyDataSetChanged();
                            }
                        }
                    } else if (response.isSuccessful() && response.body().getMessage() == "not found") {
                        Log.d("test", response.body().getMessage());
                    }
                }
            @Override
            public void onFailure(Call<MoodMoodifyModel> call, Throwable t) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(MoodFragment.this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(MoodFragment.this);
        super.onStop();
    }

    @Subscribe(sticky = true, threadMode = ThreadMode.MAIN)
    public void onMessageEvent(AuthToken event) {
        getRecommended(event.spotifyService());
    }

}
