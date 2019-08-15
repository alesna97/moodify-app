package com.alesna.moodify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import com.alesna.moodify.model.Preferences;
import com.github.furkanozalp.introductionslider.WelcomePage;

public class WelcomeActivity extends AppCompatActivity {
    public final static String TAG_ID = "id_user";
    public final static String TAG_USERNAME = "username";
    Boolean session = false;
    String id, username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        session = Preferences.getSession(this);
        id = Preferences.getIdUser(this);
        username = Preferences.getUsername(this);

        if (session){
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }else {
            WelcomePage welcomePage = new WelcomePage(WelcomeActivity.this, MainMenuActivity.class);
            welcomePage.addPage(getString(R.string.slide_1_title), getString(R.string.slide_1_desc), R.drawable.ic_child_care_black_24dp, R.color.bg_screen1);
            welcomePage.addPage(getString(R.string.slide_2_title), getString(R.string.slide_2_desc), R.drawable.ic_mood_history, R.color.bg_screen2);
            welcomePage.addPage(getString(R.string.slide_3_title), getString(R.string.slide_3_desc), R.drawable.ic_directions_run_black_24dp, R.color.bg_screen3);
            welcomePage.addPage(getString(R.string.slide_4_title), getString(R.string.slide_4_desc), R.drawable.ic_watch_black_light, R.color.bg_screen4);
            welcomePage.create();
        }
    }

}



