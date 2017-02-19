package com.example.acer.transitions_everywhere.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.ActivityPlay;
import com.example.acer.transitions_everywhere.Utils;
import com.example.acer.transitions_everywhere.adapters.CustomAdapter;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.example.fablib.OnDragStartListener;

import java.util.ArrayList;

/**
 * Created by Mirlan on 19.10.2016.
 */

public class KeyboardRecycler extends Fragment implements OnDragStartListener {

    private ActivityPlay upperAct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.keyboard_nums, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> buttons = new ArrayList<>();
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyKB)))
            buttons = Utils.getButtons();
        CustomAdapter adapter = new CustomAdapter(buttons, this, false);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
    }

    @Override
    public void onClick(String s, int pos) {
        upperAct.onClick(s);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {

    }
}
