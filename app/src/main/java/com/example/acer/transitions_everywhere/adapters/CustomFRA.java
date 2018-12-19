package com.example.acer.transitions_everywhere.adapters;

import android.view.View;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.acer.transitions_everywhere.MessageHolder;
import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.models.MessageModel;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by Mirlan on 05.10.2016.
 */

public class CustomFRA extends FirebaseRecyclerAdapter<MessageModel, MessageHolder> {

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd, HH:mm", Locale.US);
    private ProgressBar pb;

    public CustomFRA(Class<MessageModel> modelClass, int modelLayout, Class<MessageHolder> viewHolderClass,
                     DatabaseReference ref, ProgressBar pb) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        this.pb = pb;
    }

    @Override
    protected void populateViewHolder(MessageHolder viewHolder, MessageModel model, int position) {
        pb.setVisibility(ProgressBar.INVISIBLE);
        String name = model.getName();
        viewHolder.tvMessage.setText(model.getText());
        viewHolder.tvName.setText(name);
        viewHolder.tvTime.setText(dateFormat.format(model.getTimeInMillis()));
        if (model.getPhotoUrl() == null) {
            if (name == null || name.length() == 0)
                name = "A";
            else name = name.charAt(0) + "";
            viewHolder.nameOnImage.setVisibility(View.VISIBLE);
            viewHolder.nameOnImage.setText(name);
            viewHolder.image.setBackgroundResource(R.color.colorPrimaryDark);
        } else if (model.getPhotoUrl() != null) {
            viewHolder.image.setBackgroundResource(0);
            Glide.with(viewHolder.image.getContext())
                    .load(model.getPhotoUrl())
                    .into(viewHolder.image);
        }
    }

    @Override
    public void onViewRecycled(MessageHolder holder) {
        super.onViewRecycled(holder);
        holder.image.setBackgroundResource(0);
        holder.nameOnImage.setVisibility(View.GONE);
        Glide.clear(holder.image);
    }
}
