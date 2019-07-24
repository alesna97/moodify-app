package com.alesna.moodify.model;

public class RecommendedModel {
    private String playlistId, title, imgUrl;

    public RecommendedModel(String playlistId, String title, String imgUrl){
        this.playlistId = playlistId;
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public String getTitle() {
        return title;
    }

    public String getImgUrl() {
        return imgUrl;
    }
}
