package com.example.acer.transitions_everywhere;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Mirlan on 05.10.2016.
 */

public class MessageHolder extends RecyclerView.ViewHolder {

    public TextView tvMessage, tvName, tvTime, nameOnImage;
    public ImageView image;

    public MessageHolder(View v) {
        super(v);
        tvMessage = itemView.findViewById(R.id.tv_message);
        tvName = itemView.findViewById(R.id.tv_name);
        image = itemView.findViewById(R.id.image);
        tvTime = itemView.findViewById(R.id.tv_time);
        nameOnImage = itemView.findViewById(R.id.nameOnImage);
    }
}
