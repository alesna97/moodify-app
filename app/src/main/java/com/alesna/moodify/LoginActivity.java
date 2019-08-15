package com.alesna.moodify;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alesna.moodify.model.Preferences;
import com.alesna.moodify.service.Server;
import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.alesna.moodify.service.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity {

    ProgressDialog mProgressDialog;

    Button mBtnLogin;
    TextView mBtnRegister;
    EditText mTxtUsername, mTxtPassword;
    private static final String TAG = "LoginActivity";
    Intent intent;

    int success;

    ConnectivityManager connectivityManager;

    private String url = Server.URL + "login?key=kmzway87aamoodify";

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    public final static String TAG_USERNAME = "username";
    public final static String TAG_ID = "id_user";

    String tag_json_obj = "json_obj_req";

    SharedPreferences sharedPreferences;
    Boolean session = false;

    String id, username;
    public static final String my_shared_preferences = "my_shared_preferences";
    public static final String session_status = "session_status";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        {
            if (connectivityManager.getActiveNetworkInfo() != null
                    && connectivityManager.getActiveNetworkInfo().isAvailable()
                    && connectivityManager.getActiveNetworkInfo().isConnected()) {
            } else {
                Toast.makeText(getApplicationContext(), "No Internet Connection",
                        Toast.LENGTH_LONG).show();
            }
        }

        mBtnLogin = (Button) findViewById(R.id.btn_login);
        mBtnLogin.setOnClickListener(this::loginButton);
        mBtnRegister = (TextView) findViewById(R.id.btn_txdaftar);
        mBtnRegister.setOnClickListener(this::registerButton);
        mTxtUsername = (EditText) findViewById(R.id.username);
        mTxtPassword = (EditText) findViewById(R.id.password);

        //cek session
        session = Preferences.getSession(this);
        id = Preferences.getIdUser(this);

        if (session){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra(TAG_ID, id);
            intent.putExtra(TAG_USERNAME, username);
            finish();
            startActivity(intent);
        }
    }

    public void loginButton (View v){
        String username = mTxtUsername.getText().toString();
        String password = mTxtPassword.getText().toString();

                if(username.trim().length() > 0 && password.trim().length() > 0){
                        if (connectivityManager.getActiveNetworkInfo() != null
                            && connectivityManager.getActiveNetworkInfo().isAvailable()
                            && connectivityManager.getActiveNetworkInfo().isConnected()) {
                        checkLogin(username, password);
                    }else{
                            SweetAlertDialog newDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                            newDialog.setTitle("Error");
                            newDialog.setContentText("No Internet Connection");
                            newDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    newDialog.dismissWithAnimation();
                                }
                            });
                            newDialog.show();
                    }

                }else{
                    SweetAlertDialog newDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    newDialog.setTitle("Error");
                    newDialog.setContentText("Username and Password Cannot Be Empty !");
                    newDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            newDialog.dismissWithAnimation();
                        }
                    });
                    newDialog.show();
            }
        }


    public void registerButton (View v){
        intent = new Intent(LoginActivity.this, DaftarActivity.class);
        finish();
        startActivity(intent);
    }

    private void checkLogin(final String username, final String password){
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.colorPrimary);
        pDialog.setTitleText("Loging in..");
        pDialog.setCancelable(false);
        pDialog.show();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            Log.e(TAG, "Login Response: " + response.toString()+ url);

            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);

                // Check for error node in json
                if (success == 1) {
                    String username1 = jObj.getString(TAG_USERNAME);
                    String id = jObj.getString(TAG_ID);
                    Log.e("Successfully Login!", jObj.toString());
                    pDialog.dismissWithAnimation();
                    // menyimpan login ke session
                    Preferences.setSession(this,true);
                    Preferences.setIdUser(this, id);
                    Preferences.setUsername(this,username1);

                    // Memanggil main activity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra(TAG_ID, id);
                    intent.putExtra(TAG_USERNAME, username1);
                    finish();
                    startActivity(intent);
                } else {
                    pDialog.dismissWithAnimation();
                    SweetAlertDialog newDialog = new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE);
                    newDialog.setTitle(jObj.getString(TAG_MESSAGE));
                    newDialog.setConfirmButton("OK", new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {
                            newDialog.dismissWithAnimation();
                        }
                    });
                    newDialog.show();
                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

        }, error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    private void showDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

}
