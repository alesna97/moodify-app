package com.alesna.moodify.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class UserMoodifyModel {

        @SerializedName("id_user")
        private String id_user;

        @SerializedName("username")
        private String username;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
        private String email;

        @SerializedName("password")
        private  String password;

        @SerializedName("tgl_lahir")
        private String tgl_lahir;


    public void setTgl_lahir(String tgl_lahir) {
        this.tgl_lahir = tgl_lahir;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getId() {
            return id_user;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        public String getTgl_lahir() {
            return tgl_lahir;
        }

        public String getUsername() {
            return username;
        }
}
