package com.alesna.moodify.service;

public class ConnWearableEvent {

    public final String mac_address,device_name;

    public ConnWearableEvent(String mac_address, String device_name){
        this.mac_address = mac_address;
        this.device_name = device_name;
    }

    public String getDevice_name() {
        return device_name;
    }

    public String getMac_address() {
        return mac_address;
    }
}
