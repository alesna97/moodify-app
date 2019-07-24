package com.alesna.moodify.model;

import com.google.gson.annotations.SerializedName;

public class FcmModel {

    @SerializedName("success")
    private String success;

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getSuccess() {
        return success;
    }
}

