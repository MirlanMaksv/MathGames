package com.example.acer.transitions_everywhere.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.acer.transitions_everywhere.R;
import com.example.fablib.ItemTouchHelperAdapter;
import com.example.fablib.OnDragStartListener;

import java.util.ArrayList;
import java.util.Collections;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
        implements ItemTouchHelperAdapter {

    private ArrayList<String> buttons = new ArrayList<>();
    private OnDragStartListener onDragStart;
    private boolean setDragAndDrop;

    public CustomAdapter(ArrayList<String> buttons,
                         OnDragStartListener listener, boolean setDragAndDrop) {
        this.onDragStart = listener;
        this.setDragAndDrop = setDragAndDrop;
        if (buttons == null || buttons.size() == 0)
            fillArray(this.buttons);
        else this.buttons = buttons;
    }

    public ArrayList<String> fillArray(ArrayList<String> buttons) {
        buttons.add("7");
        buttons.add("8");
        buttons.add("9");
        buttons.add("4");
        buttons.add("5");
        buttons.add("6");
        buttons.add("1");
        buttons.add("2");
        buttons.add("3");
        buttons.add("");
        buttons.add("0");
        buttons.add("D");
        return buttons;
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++)
                Collections.swap(buttons, i, i + 1);
        } else {
            for (int i = fromPosition; i > toPosition; i--)
                Collections.swap(buttons, i, i - 1);
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grid_btn, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        String buttonSign = buttons.get(position);
        if (!buttonSign.equals("D")) {      // IF NOT A DELETE button
            holder.ib.setVisibility(View.GONE);
            holder.btn.setText(buttonSign);
            holder.btn.setTextSize(25);
        } else {                            // IF DELETE button
            holder.btn.setVisibility(View.GONE);
            holder.ib.setImageResource(R.drawable.icon_backspace);
        }
        setListener(holder, !buttonSign.equals("D") ? holder.btn : holder.ib, position);
    }

    private void setListener(final RecyclerView.ViewHolder holder, View view, final int position) {
        if (setDragAndDrop) view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    onDragStart.onStartDrag(holder);
                return false;
            }
        });
        else view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = buttons.get(position);
                onDragStart.onClick(s.equals("00") ? "" : s, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return buttons.size();
    }

    public ArrayList<String> getButtons() {
        return buttons;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private Button btn;
        private ImageButton ib;

        public ViewHolder(View itemView) {
            super(itemView);
            btn = (Button) itemView.findViewById(R.id.item_btn);
            ib = (ImageButton) itemView.findViewById(R.id.item_ib);
        }
    }
}
