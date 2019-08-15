package com.alesna.moodify.model;

import com.google.gson.annotations.SerializedName;

public class PostModel {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
