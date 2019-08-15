package com.alesna.moodify;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alesna.moodify.model.PostModel;
import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.model.UserMoodifyModel;
import com.alesna.moodify.service.ApiClient;
import com.alesna.moodify.service.EventLogout;
import com.alesna.moodify.service.MoodifyService;

import org.greenrobot.eventbus.EventBus;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {

    Button logout,save,password;
    EditText username,name,email,birthdate;
    MoodifyService service;
    ProgressDialog progressDialog;
    SweetAlertDialog sweetAlertDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        logout = (Button) findViewById(R.id.btn_logout);
        logout.setOnClickListener(v -> logout(v));
        save = (Button) findViewById(R.id.btn_save);
        save.setOnClickListener(v -> editProfile(v));
        username = (EditText) findViewById(R.id.tx_user);
        name = (EditText) findViewById(R.id.tx_name);
        email = (EditText) findViewById(R.id.tx_email);
        birthdate = (EditText) findViewById(R.id.tx_birth);
        password = (Button) findViewById(R.id.btn_password);
        service = ApiClient.retrofitClient().create(MoodifyService.class);
        getUserData();
    }

    public void logout (View v){
        Preferences.clearPlaylistID(this);
        Preferences.clearFcmToken(this);
        Preferences.clearIdUser(this);
        Preferences.clearSpotifyToken(this);
        Preferences.clearUsername(this);
        Preferences.clearSession(this);
        Intent intent = new Intent(ProfileActivity.this, MainMenuActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        EventBus.getDefault().postSticky(new EventLogout(true));
        finish();
        startActivity(intent);
    }
    public void getUserData(){
        Call<UserMoodifyModel> call = service.getUserById(Preferences.getIdUser(this));
        call.enqueue(new Callback<UserMoodifyModel>() {
            @Override
            public void onResponse(Call<UserMoodifyModel> call, Response<UserMoodifyModel> response) {
                if(response.isSuccessful()){
                    UserMoodifyModel res = response.body();
                    username.setText(res.getUsername());
                    name.setText(res.getName());
                    email.setText(res.getEmail());
                    birthdate.setText(res.getTgl_lahir());
                }else{
                    Toast.makeText(ProfileActivity.this,"error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserMoodifyModel> call, Throwable t) {
                Toast.makeText(ProfileActivity.this,t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void editProfile(View v){
        String id = Preferences.getIdUser(this);
        String mUsername = username.getText().toString();
        String mName = name.getText().toString();
        String mBirth = birthdate.getText().toString();
        String mEmail = email.getText().toString();


        if(mUsername.trim().length() > 0 ) {
            setToast("Username cannot be empty !");
            username.setFocusable(true);
        }else if(mName.trim().length() > 0 ) {
            setToast("Name cannot be empty !");
            name.setFocusable(true);
        }else if(mBirth.trim().length() > 0 ) {
            setToast("Birthdate cannot be empty !");
            birthdate.setFocusable(true);
        }else if(mEmail.trim().length() > 0 ) {
            setToast("Email cannot be empty !");
            email.setFocusable(true);
        }

        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Updating Data...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Call<PostModel> call = service.editProfile(id,mUsername,mName,mEmail,mBirth);
        call.enqueue(new Callback<PostModel>() {
            @Override
            public void onResponse(Call<PostModel> call, Response<PostModel> response) {
                if(response.isSuccessful()){
                    progressDialog.dismiss();
                    getUserData();
                    Toast.makeText(ProfileActivity.this,response.body().getMessage() ,Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.dismiss();
                    Toast.makeText(ProfileActivity.this,response.body().getMessage() ,Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostModel> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(ProfileActivity.this,t.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void setToast(String msg){
        Toast.makeText(ProfileActivity.this,msg ,Toast.LENGTH_SHORT).show();
    }
}
