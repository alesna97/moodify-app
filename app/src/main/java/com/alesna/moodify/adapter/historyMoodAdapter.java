package com.alesna.moodify.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alesna.moodify.R;
import com.alesna.moodify.model.HistoryMoodModel;
import com.squareup.picasso.Picasso;
import com.yinglan.shadowimageview.ShadowImageView;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class historyMoodAdapter extends RecyclerView.Adapter<historyMoodAdapter.MoodViewHolder> {

    private ArrayList<HistoryMoodModel> dataList;

    public historyMoodAdapter(ArrayList<HistoryMoodModel> dataList) {
        this.dataList = dataList;
    }

    @Override
    public MoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.list_history_mood, parent, false);
        return new MoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoodViewHolder holder, int position) {
        holder.mood.setText(dataList.get(position).getIdMood());
        holder.date.setText(dataList.get(position).getDate());
        holder.time.setText(dataList.get(position).getTime());
        holder.heartRate.setText(dataList.get(position).getHeartRate());
        if (dataList.get(position).getIdMood() == "happy"){
            holder.ic_mood.setImageResource(R.drawable.ic_mood_history);
            holder.bg_img.setBackgroundResource(R.drawable.bg_happy);
            holder.bg_overlay.setBackgroundResource(R.drawable.overlay_pink_history);
        } else if(dataList.get(position).getIdMood() == "sad"){
            holder.ic_mood.setImageResource(R.drawable.ic_mood_bad_black_24dp);
            holder.bg_img.setBackgroundResource(R.drawable.bg_sad);
            holder.bg_overlay.setBackgroundResource(R.drawable.overlay_cyan_history);
        } else if(dataList.get(position).getIdMood() == "angry"){
            holder.ic_mood.setImageResource(R.drawable.ic_mood_bad_black_24dp);
            holder.bg_img.setBackgroundResource(R.drawable.bg_angry);
            holder.bg_overlay.setBackgroundResource(R.drawable.overlay_green_history);
        } else if(dataList.get(position).getIdMood() == "relax"){
            holder.ic_mood.setImageResource(R.drawable.ic_mood_bad_black_24dp);
            holder.bg_img.setBackgroundResource(R.drawable.bg_relax);
            holder.bg_overlay.setBackgroundResource(R.drawable.overlay_orange_history);
        }
    }

    @Override
    public int getItemCount() {

        return (dataList != null) ? dataList.size() : 0;
    }

    public class MoodViewHolder extends RecyclerView.ViewHolder {

        private TextView mood, date, time, heartRate;
        private ImageView ic_mood;
        private LinearLayout bg_overlay, bg_img;

        public MoodViewHolder(View itemView) {
            super(itemView);


            heartRate = (TextView) itemView.findViewById(R.id.heartRate);
            mood = (TextView) itemView.findViewById(R.id.mood);
            date = (TextView) itemView.findViewById(R.id.date);
            time = (TextView) itemView.findViewById(R.id.time);
            ic_mood = (ImageView) itemView.findViewById(R.id.ic_mood);
            bg_overlay = (LinearLayout) itemView.findViewById(R.id.bg_overlay);
            bg_img = (LinearLayout) itemView.findViewById(R.id.bg_img);
        }
    }

}
