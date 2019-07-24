package com.alesna.moodify.model;

public class HistoryMoodModel {
    String idUser, idMood, heartRate, date, time;

    public HistoryMoodModel(String idUser, String idMood, String heartRate,String date, String time){
        this.idUser = idUser;
        this.idMood = idMood;
        this.date = date;
        this.time = time;
        this.heartRate = heartRate;
    }

    public String getDate() {
        return date;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public String getIdMood() {
        return idMood;
    }

    public String getIdUser() {
        return idUser;
    }

    public String getTime() {
        return time;
    }
}
