package com.alesna.moodify;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.ybq.android.spinkit.SpinKitView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SongsFragment extends Fragment {

    SpinKitView mSpinkitView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_songs, container, false);
        mSpinkitView = (SpinKitView) v.findViewById(R.id.spin_kit_song);
        mSpinkitView.setVisibility(View.VISIBLE);
        return v;
    }

}
