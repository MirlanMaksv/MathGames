package com.example.acer.transitions_everywhere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.acer.transitions_everywhere.R;

import java.util.ArrayList;

/**
 * Created by Mirlan on 24.09.2016.
 */
public class CustomListAdapter extends RecyclerView.Adapter<CustomListAdapter.ViewHolder> {

    private ArrayList<String> arrayList;

    public CustomListAdapter(ArrayList<String> list) {
        arrayList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvPos.setText((position + 1) + ".");
        holder.tvValue.setText(arrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvPos, tvValue;

        public ViewHolder(View itemView) {
            super(itemView);
            tvValue = itemView.findViewById(R.id.listItem_tvValue);
            tvPos = itemView.findViewById(R.id.listItem_tvPos);
        }
    }
}
