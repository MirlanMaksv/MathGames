package com.example.acer.transitions_everywhere;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.adapters.CustomListAdapter;

import java.util.ArrayList;

/**
 * Created by Mirlan on 24.09.2016.
 */

public class ResultListDialog extends DialogFragment {

    public static DialogFragment newInstance(ArrayList<String> resList) {
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("list", resList);
        DialogFragment fragment = new ResultListDialog();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_res_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayList<String> list = getArguments().getStringArrayList("list");

        RecyclerView rv = (RecyclerView) view.findViewById(R.id.rv);
        CustomListAdapter adapterCorrect = new CustomListAdapter(list);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapterCorrect);
        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
