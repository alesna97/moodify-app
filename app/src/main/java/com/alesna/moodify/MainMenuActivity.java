package com.alesna.moodify;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import static com.alesna.moodify.LoginActivity.my_shared_preferences;
import static com.alesna.moodify.LoginActivity.session_status;

public class MainMenuActivity extends AppCompatActivity {
    Button login,register;
    public final static String TAG_ID = "id_user";
    public final static String TAG_USERNAME = "username";

    SharedPreferences sharedPreferences;
    Boolean session = false;

    String id, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        getSupportActionBar().hide();

        login = (Button) findViewById(R.id.btn_login_main);
        register = (Button) findViewById(R.id.btn_daftar_main);

        login.setOnClickListener(this::onLoginCick);
        register.setOnClickListener(this::onRegisterCick);
        //cek session
        sharedPreferences = getSharedPreferences(my_shared_preferences, Context.MODE_PRIVATE);
        session = sharedPreferences.getBoolean(session_status, false);
        id = sharedPreferences.getString(TAG_ID, null);

        if (session){
            Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }
    }
    public void onLoginCick (View v){
        startActivity(new Intent(MainMenuActivity.this, LoginActivity.class));
        finish();
    }

    public void onRegisterCick (View v){
        startActivity(new Intent(MainMenuActivity.this, DaftarActivity.class));
        finish();
    }
}
