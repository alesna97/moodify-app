package com.alesna.moodify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.alesna.moodify.model.Preferences;


public class MainMenuActivity extends AppCompatActivity {
    Button login,register;
    public final static String TAG_ID = "id_user";
    public final static String TAG_USERNAME = "username";

    Boolean session = false;
    String id, username;
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
        session = Preferences.getSession(this);
        id = Preferences.getIdUser(this);
        username = Preferences.getUsername(this);

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
