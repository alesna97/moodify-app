package com.alesna.moodify.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alesna.moodify.R;
import com.alesna.moodify.model.RecommendedModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class recommendedAdapter extends RecyclerView.Adapter<recommendedAdapter.RecommendedViewHolder> {

    private ArrayList<RecommendedModel> dataList;

    public recommendedAdapter (ArrayList<RecommendedModel> dataList){
        this.dataList = dataList;
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
