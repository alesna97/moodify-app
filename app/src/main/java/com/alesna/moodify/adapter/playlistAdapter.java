package com.alesna.moodify.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alesna.moodify.R;
import com.alesna.moodify.model.PlaylistModel;
import com.squareup.picasso.Picasso;
import com.yinglan.shadowimageview.ShadowImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class playlistAdapter extends RecyclerView.Adapter<playlistAdapter.PlaylistViewHolder> {

    private ArrayList<PlaylistModel> dataList;

    public playlistAdapter(ArrayList<PlaylistModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_playlist_tracks, parent, false);
        return new PlaylistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        holder.trackName.setText(dataList.get(position).getTrack());
        holder.trackDuration.setText(dataList.get(position).getDuration().toString());
        holder.trackArtist.setText(dataList.get(position).getArtist());
        //holder.album.setImageBitmap(dataList.get(position).getAlbum_url());
        Picasso.get().load(dataList.get(position).getAlbum_url()).into(holder.album);

    }

    @Override
    public int getItemCount() {
        return (dataList != null) ? dataList.size() : 0;
    }

    public class PlaylistViewHolder extends RecyclerView.ViewHolder {

        private TextView trackName, trackDuration, trackArtist;
        private ImageView album;

        public PlaylistViewHolder(View itemView) {
            super(itemView);

            trackName = (TextView) itemView.findViewById(R.id.trackname);
            trackArtist = (TextView) itemView.findViewById(R.id.artist);
            trackDuration = (TextView) itemView.findViewById(R.id.trackduration);
            album = (ImageView) itemView.findViewById(R.id.album);

        }
    }

}
