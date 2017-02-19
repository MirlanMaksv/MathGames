package com.example.fablib;

import android.support.v7.widget.RecyclerView;

/**
 * Created by Mirlan on 22.09.2016.
 */
public interface OnDragStartListener {

    void onStartDrag(RecyclerView.ViewHolder holder);

    void onClick(String s, int pos);
}
