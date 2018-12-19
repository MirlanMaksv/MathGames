package com.example.acer.transitions_everywhere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.acer.transitions_everywhere.R;

import java.util.ArrayList;

/**
 * Created by Mirlan on 15.10.2016.
 */

public class SelectTypeAdapter extends RecyclerView.Adapter<SelectTypeAdapter.ViewHolder> {

    private GameTypeOnClick listener;
    private ArrayList<String> gameTypes;
    private int[] drawableRes;

    public interface GameTypeOnClick {
        void onTypeClicked(int pos);
    }

    public SelectTypeAdapter(GameTypeOnClick listener) {
        this.listener = listener;
        fillArrays();
    }

    private void fillArrays() {
        gameTypes = new ArrayList<>();
        gameTypes.add("На время");
        gameTypes.add("Истина/Ложь");
        gameTypes.add("Свободная игра");

        drawableRes = new int[3];
        drawableRes[0] = R.drawable.ic_clock;
        drawableRes[1] = R.drawable.ic_thumbs_up;
        drawableRes[2] = R.drawable.ic_free_1;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_game_type, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvName.setText(gameTypes.get(position));
        holder.ivIcon.setImageResource(drawableRes[position]);
    }

    @Override
    public int getItemCount() {
        return gameTypes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private ImageView ivIcon;

        public ViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.item_iv);
            tvName = itemView.findViewById(R.id.item_tv);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onTypeClicked(getAdapterPosition());
                }
            });
        }
    }
}
