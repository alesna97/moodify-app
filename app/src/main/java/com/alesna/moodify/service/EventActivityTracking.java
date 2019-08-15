package com.alesna.moodify.service;

public class EventActivityTracking {
    private boolean click;

    public EventActivityTracking(Boolean click){
        this.click = click;
    }
    public Boolean getClick(){
        return click;
    }
}
