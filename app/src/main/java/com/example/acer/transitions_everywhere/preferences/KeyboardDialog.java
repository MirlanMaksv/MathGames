package com.example.acer.transitions_everywhere.preferences;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.acer.transitions_everywhere.R;
import com.example.acer.transitions_everywhere.Utils;
import com.example.acer.transitions_everywhere.adapters.CustomAdapter;
import com.example.acer.transitions_everywhere.databinding.DialogKeyboardBinding;
import com.example.fablib.OnDragStartListener;
import com.example.fablib.SimpleItemTouchHelperCallback;

import java.util.ArrayList;

/**
 * Created by Mirlan on 23.09.2016.
 */
public class KeyboardDialog extends DialogFragment implements OnDragStartListener {

    private DialogKeyboardBinding binding;
    private ItemTouchHelper touchHelper;
    private CustomAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_keyboard, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.setHandlers(this);
        ArrayList<String> buttons = new ArrayList<>();
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(getContext());
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyKB)))
            buttons = Utils.getButtons();
        adapter = new CustomAdapter(buttons, this, true);
        binding.prefRv.setLayoutManager(new GridLayoutManager(getContext(), 3));
        binding.prefRv.setAdapter(adapter);

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        touchHelper = null;
        touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(binding.prefRv);

    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder holder) {
        touchHelper.startDrag(holder);
    }

    @Override
    public void onClick(String s, int pos) {

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.pref_save) {
            saveButtons(adapter.getButtons());
            View parentView = getParentFragment().getView();
            if (parentView != null)
                Snackbar.make(parentView, getString(R.string.SnackBarSaved), Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.pref_reset) {
            saveButtons(adapter.fillArray(new ArrayList<String>()));
            dismiss();
            show(getFragmentManager(), null);
            return;
        }
        dismiss();
    }

    private void saveButtons(ArrayList<String> buttons) {
        String btns = "";
        for (String i : buttons)
            btns += i + ",";
        PrefsHelper.getPrefsHelper().savePref(PrefsHelper.BUTTONS, btns);
    }
}
