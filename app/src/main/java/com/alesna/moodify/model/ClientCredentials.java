package com.alesna.moodify.model;

public class ClientCredentials {

    private static final String CLIENT_ID = "0b8bdd412f544defb36bb6bb19f5f2a7";
    private static final String REDIRECT_URI = "com.alesna.moodify://callback";
    private static final int REQUEST_CODE = 1337;
    private static final String CLIENT_SECRET = "bf9b291f83f84dc1997011861d44cf61";
    public static int getRequestCode() {
        return REQUEST_CODE;
    }

    public static String getClientId() {
        return CLIENT_ID;
    }

    public static String getRedirectUri() {
        return REDIRECT_URI;
    }

    public static String getClientSecret() {
        return CLIENT_SECRET;
    }
}
