package com.alesna.moodify;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.github.ybq.android.spinkit.SpinKitView;

import static com.alesna.moodify.MainActivity.TAG_ID;
import static com.alesna.moodify.MainActivity.TAG_USERNAME;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoodFragment extends Fragment {
    Button mBtnLogout;
    SpinKitView mSpinkitView;
    SharedPreferences sharedpreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_mood, container, false);

        mBtnLogout = (Button) v.findViewById(R.id.logout);
        mBtnLogout.setOnClickListener(this::logout);
        mSpinkitView = (SpinKitView) v.findViewById(R.id.spin_kit_mood);
        mSpinkitView.setVisibility(View.VISIBLE);
        return v;
    }

    public void logout(View v){
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean(LoginActivity.session_status, false);
        editor.remove(TAG_ID);
        editor.remove(TAG_USERNAME);
        editor.apply();

        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
    }

}
