package com.alesna.moodify.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MoodMoodifyModel {

    @SerializedName("message")
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    private List<list_history_mood> historylist;

    public List<list_history_mood> getHistorylist() {
        return historylist;
    }

    public void setHistorylist(List<list_history_mood> historylist) {
        this.historylist = historylist;
    }

    public class list_history_mood{
        @SerializedName("id_history_mood")
        private String id_history_mood;

        @SerializedName("id_user")
        private String id_user;

        @SerializedName("id_mood")
        private String id_mood;

        @SerializedName("heart_rate")
        private String heart_rate;

        @SerializedName("tanggal_mood")
        private String tanggal_mood;

        @SerializedName("waktu_mood")
        private String waktu_mood;


        public void setId_user(String id_user) {
            this.id_user = id_user;
        }

        public void setId_history_mood(String id_history_mood) {
            this.id_history_mood = id_history_mood;
        }

        public void setHeart_rate(String heart_rate) {
            this.heart_rate = heart_rate;
        }

        public void setId_mood(String id_mood) {
            this.id_mood = id_mood;
        }

        public void setTanggal_mood(String tanggal_mood) {
            this.tanggal_mood = tanggal_mood;
        }

        public void setWaktu_mood(String waktu_mood) {
            this.waktu_mood = waktu_mood;
        }

        public String getHeart_rate() {
            return heart_rate;
        }

        public String getId_history_mood() {
            return id_history_mood;
        }

        public String getId_user() {
            return id_user;
        }

        public String getId_mood() {
            return id_mood;
        }

        public String getTanggal_mood() {
            return tanggal_mood;
        }

        public String getWaktu_mood() {
            return waktu_mood;
        }
    }

}
