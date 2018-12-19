package com.example.acer.transitions_everywhere.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.ActivityPlay;
import com.example.acer.transitions_everywhere.ActivitySelectType;
import com.example.acer.transitions_everywhere.Const;
import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.adapters.SelectTypeAdapter;

/**
 * Created by Mirlan on 16.11.2016.
 */

public class SelectType extends Fragment implements SelectTypeAdapter.GameTypeOnClick {

    private ActivitySelectType upperAct;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_select_game, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new SelectTypeAdapter(this));
    }

    @Override
    public void onTypeClicked(int position) {
        if (position != 2) {
            Intent intent = new Intent(getContext(), ActivityPlay.class);
            Bundle bundle = new Bundle();
            bundle.putInt(Const.GAME_TYPE, position);
            intent.putExtra(Const.BUNDLE, bundle);
            upperAct.startActivityForResult(intent, ActivitySelectType.REQCODE);
        } else {
            upperAct.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new GameSetup())
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (upperAct == null)
            upperAct = (ActivitySelectType) getActivity();
    }

    @Override
    public void onStop() {
        upperAct = null;
        super.onStop();
    }
}
