package com.alesna.moodify;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alesna.moodify.app.Server;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.alesna.moodify.app.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DaftarActivity extends AppCompatActivity {
    ProgressDialog mProgressDialog;
    Button mBtnRegister;
    TextView  mBtnLogin;
    EditText txt_username, txt_password, txt_confirm_password, txt_email, txt_tgl_lahir;
    Intent intent;

    int success;
    ConnectivityManager connectivityManager;

    private String url = Server.URL + "register?key=kmzway87aamoodify";

    private static final String TAG = DaftarActivity.class.getSimpleName();

    private static final String TAG_SUCCESS = "success";
    private static final String TAG_MESSAGE = "message";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar);
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

        mBtnLogin = (TextView) findViewById(R.id.back_login);
        mBtnRegister = (Button) findViewById(R.id.btn_daftar);
        mBtnRegister.setOnClickListener(this::registerButton);
        mBtnLogin.setOnClickListener(this::loginback);

        txt_confirm_password = (EditText) findViewById(R.id.confirm_password);
        txt_password = (EditText) findViewById(R.id.password);
        txt_email = (EditText) findViewById(R.id.email);
        txt_username = (EditText) findViewById(R.id.username);
        txt_tgl_lahir = (EditText) findViewById(R.id.tgl_lahir);

    }

    public void registerButton (View v){
        String username = txt_username.getText().toString();
        String password = txt_password.getText().toString();
        String confirm_password = txt_confirm_password.getText().toString();
        String email = txt_email.getText().toString();
        String tgl_lahir = txt_tgl_lahir.getText().toString();
        if (connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isAvailable()
                && connectivityManager.getActiveNetworkInfo().isConnected()) {
            checkRegister(username, password, confirm_password,email,tgl_lahir);
        } else {
            Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkRegister(final String username, final String password, final String confirm_password, final String email,final String tgl_lahir) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Register ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST, url, response -> {
            Log.e(TAG, "Register Response: " + response.toString());
            hideDialog();

            try {
                JSONObject jObj = new JSONObject(response);
                success = jObj.getInt(TAG_SUCCESS);

                // Check for error node in json
                if (success == 1) {

                    Log.e("Successfully Register!", jObj.toString());

                    Toast.makeText(getApplicationContext(),
                            jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                    txt_username.setText("");
                    txt_password.setText("");
                    txt_confirm_password.setText("");
                    txt_email.setText("");
                    txt_tgl_lahir.setText("");

                } else {
                    Toast.makeText(getApplicationContext(),
                            jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                }
            } catch (JSONException e) {
                // JSON error
                e.printStackTrace();
            }

        }, error -> {
            Log.e(TAG, "Login Error: " + error.getMessage());
            Toast.makeText(getApplicationContext(),
                    error.getMessage(), Toast.LENGTH_LONG).show();

            hideDialog();

        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("password", password);
                params.put("confirm_password", confirm_password);
                params.put("email", email);
                params.put("tgl_lahir", tgl_lahir);

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

    public void loginback(View v){
        intent = new Intent(DaftarActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        intent = new Intent(DaftarActivity.this, LoginActivity.class);
        finish();
        startActivity(intent);
    }


}
