package com.example.acer.transitions_everywhere;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Created by Mirlan on 07.11.2016.
 */

public class PasswordDialog extends DialogFragment implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private ProgressBar bar;
    private EditText etEmail;
    private Button reset;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.DialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_password, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        firebaseAuth = FirebaseAuth.getInstance();

        reset = (Button) view.findViewById(R.id.btnReset);
        reset.setOnClickListener(this);
        reset.setEnabled(false);
        etEmail = ((EditText) view.findViewById(R.id.etEmail));
        etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                reset.setEnabled(charSequence.length() > 0);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        String email = etEmail.getText().toString();
        if (email.length() > 0 && email.contains("@")) {
            bar = (ProgressBar) getView().findViewById(R.id.progressBar);
            bar.setVisibility(View.VISIBLE);
            reset.setClickable(false);
            firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    bar.setVisibility(View.GONE);
                    reset.setClickable(true);
                    if (task.isSuccessful()) {
                        PasswordDialog.this.dismiss();
                        ((ActivitySignIn) getActivity()).showSnackBar(R.string.resetSentSuccessful, Snackbar.LENGTH_SHORT);
                    } else
                        Toast.makeText(getContext(), R.string.resetSentUnsuccessful, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
