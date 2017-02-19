package com.example.acer.transitions_everywhere.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.acer.transitions_everywhere.ActivityPlay;
import com.example.acer.transitions_everywhere.R;

/**
 * Created by Mirlan on 16.10.2016.
 */

public class TrueFalse extends Fragment implements View.OnClickListener {

    private ActivityPlay upperAct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.keyboard_true_false, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.thumbUp).setOnClickListener(this);
        view.findViewById(R.id.thumbDown).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // send 1 if thumbUp clicked, 0 otherwise
        upperAct.onClick(view.getId() == R.id.thumbUp ? "1" : "0");
    }

    @Override
    public void onStart() {
        super.onStart();
        upperAct = (ActivityPlay) getActivity();
    }

    @Override
    public void onStop() {
        upperAct = null;
        super.onStop();
    }
}
