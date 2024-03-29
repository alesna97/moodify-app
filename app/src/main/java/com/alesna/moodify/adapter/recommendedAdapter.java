package com.alesna.moodify.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alesna.moodify.MainActivity;
import com.alesna.moodify.R;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.model.RecommendedModel;
import com.alesna.moodify.service.PlaylistIdEvent;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import jp.wasabeef.picasso.transformations.BlurTransformation;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Image;
import kaaes.spotify.webapi.android.models.Playlist;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class recommendedAdapter extends RecyclerView.Adapter<recommendedAdapter.RecommendedViewHolder> {

    private ArrayList<RecommendedModel> dataList;
    private Context context;
    LayoutInflater inflater;
    public recommendedAdapter (ArrayList<RecommendedModel> dataList, Context context){
        this.dataList = dataList;
        this.context = context;
    }

    @NonNull
    @Override
    public recommendedAdapter.RecommendedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_recommended, parent, false);
        return new recommendedAdapter.RecommendedViewHolder(view);
    }

    @Override
    public void onBindViewHolder(recommendedAdapter.RecommendedViewHolder holder, int position) {
        holder.title.setText(dataList.get(position).getTitle());
        Picasso.get().load(dataList.get(position).getImgUrl()).into(holder.cover);
        holder.cover.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }case MotionEvent.ACTION_UP: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        View view1 = inflater.inflate(R.layout.list_history_mood, null);
                        SweetAlertDialog alertDialog = new SweetAlertDialog(context, SweetAlertDialog.NORMAL_TYPE);
                        alertDialog.setTitle(dataList.get(position).getTitle());
                        alertDialog.setConfirmButton("Play", new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                EventBus.getDefault().postSticky(new PlaylistIdEvent(dataList.get(position).getPlaylistId()));
                                Preferences.setPlaylistId(context,dataList.get(position).getPlaylistId());
                                alertDialog.dismissWithAnimation();
                            }
                        });
                        alertDialog.show();
                        break;
                    }case MotionEvent.ACTION_OUTSIDE: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class RecommendedViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private ImageView cover;
        public RecommendedViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.playlistTitle);
            cover = (ImageView) itemView.findViewById(R.id.coverImg);
        }
    }

}
