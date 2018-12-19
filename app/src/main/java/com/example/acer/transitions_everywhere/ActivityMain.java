package com.example.acer.transitions_everywhere;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.acer.transitions_everywhere.adapters.CustomFRA;
import com.example.acer.transitions_everywhere.databinding.ActivityMainBinding;
import com.example.acer.transitions_everywhere.models.MessageModel;
import com.example.acer.transitions_everywhere.models.UserModel;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.example.menubuttonlib.MenuButton;
import com.example.menubuttonlib.OnMenuItemClick;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ActivityMain extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnMenuItemClick {

    public static final String MESSAGES_CHILD = "messages";
    public static final String ANONYMOUS = "anonymous";
    private static final String TAG = "ActivityMain";
    public static final int DEFAULT_MSG_LENGTH_LIMIT = 100;
    private static final int REQCODE = 101;
    private static final int SignInREQCODE = 102;
    private ActivityMainBinding binding;
    private AnimatorSet impulse;
    private PrefsHelper preferences;
    private boolean themeWas;
    private boolean isOnTop, isMorphedUp = true;
    private BottomSheetBehavior behavior;
    //    private static final int REQUEST_INVITE = 1;
//    private static final String MESSAGE_SENT_EVENT = "message_sent";
    private String mUsername;
    private String mPhotoUrl;
    private GoogleApiClient mGoogleApiClient;

    private LinearLayoutManager mLinearLayoutManager;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;

    private DatabaseReference reference;
    private FirebaseRecyclerAdapter<MessageModel, MessageHolder>
            mFirebaseAdapter;

    private RecyclerView.AdapterDataObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        preferences = new PrefsHelper(this);
        if (preferences.getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setHandlers(this);

        if (!FacebookSdk.isInitialized())
            FacebookSdk.sdkInitialize(getApplicationContext());

        impulse = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.play_anims);
        impulse.setTarget(binding.startPlay);
        impulse.setStartDelay(getResources().getInteger(R.integer.animImpulse));
        impulse.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animator) {
                if (isOnTop && !impulse.isStarted())
                    impulse.start();
            }

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {
            }
        });
        behavior = BottomSheetBehavior.from(binding.bottomSheet);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {

            private int offSet = Utils.convertDpToPixels(35);

            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState != BottomSheetBehavior.STATE_EXPANDED) {
                    binding.messageEditText.setFocusable(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                binding.mainScreen.setTranslationY(-offSet * slideOffset);
                if (slideOffset == 0.0) {
                    upDown(true);
                    isOnTop = true;
                    if (!ActivityMain.this.impulse.isStarted() && !ActivityMain.this.impulse.isRunning())
                        ActivityMain.this.impulse.start();
                } else if (slideOffset == 1.0) {
                    upDown(false);
                    isOnTop = false;
                    if (ActivityMain.this.impulse.isStarted() || ActivityMain.this.impulse.isRunning())
                        ActivityMain.this.impulse.end();
                    binding.messageEditText.setFocusableInTouchMode(true);
                }
            }
        });
        binding.chatSignIn.post(new Runnable() {
            @Override
            public void run() {
                behavior.setPeekHeight(binding.chatSignIn.getHeight());
            }
        });

        ((MenuButton) findViewById(R.id.menuButton)).setOnMenuItemClick(this);
        setupChat();
    }

    private void upDown(boolean expand) {
        if (!expand && isMorphedUp) {
            binding.upDown.morphDown();
            isMorphedUp = false;
        } else if (expand && !isMorphedUp) {
            binding.upDown.morphUp();
            isMorphedUp = true;
        }
    }

    private void setupChat() {
        mFirebaseAuth = FirebaseAuth.getInstance();
        assignUser();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        // Initialize ProgressBar and RecyclerView.
        mLinearLayoutManager = new LinearLayoutManager(this);
//        mLinearLayoutManager.setStackFromEnd(true);

        // New child entries
        reference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new CustomFRA(
                MessageModel.class,
                R.layout.item_message,
                MessageHolder.class,
                reference.child(MESSAGES_CHILD), binding.progressBar);

        binding.bsRecyclerView.setLayoutManager(mLinearLayoutManager);
        binding.bsRecyclerView.setAdapter(mFirebaseAdapter);
        observer = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (positionStart >= (friendlyMessageCount - 1) &&
                        lastVisiblePosition <= (positionStart - 1)) {
                    binding.bsRecyclerView.scrollToPosition(positionStart);
                }
            }
        };
        mFirebaseAdapter.registerAdapterDataObserver(observer);

        binding.messageEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(DEFAULT_MSG_LENGTH_LIMIT)});
        binding.messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    binding.sendButton.setEnabled(true);
                } else {
                    binding.sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void assignUser() {
        mUsername = ANONYMOUS;
        mPhotoUrl = null;
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        int stringId = (mFirebaseUser == null) ?
                R.string.sign_in : R.string.sign_out;
        binding.chatSignIn.setText(getText(stringId));
        if (mFirebaseUser != null) {
            String name = mFirebaseUser.getDisplayName();
            if (name == null) {
                ValueEventListener listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        UserModel model = dataSnapshot.getValue(UserModel.class);
                        if (model != null) {
                            Log.e("ValueEvent", "onDataChange\nemail: " + model.getEmail() + "\nusername: " + model.getUserName());
                            mUsername = model.getUserName();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("ValueEvent", "onCancelled");
                    }
                };

                reference.child("users").child(mFirebaseUser.getUid()).addListenerForSingleValueEvent(listener);
            } else if (name.length() > 0)
                mUsername = name;
            else mUsername = ANONYMOUS;

            if (mFirebaseUser.getPhotoUrl() != null) {
                mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
            }
        }
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.start_play) {
            startActivity(new Intent(ActivityMain.this, ActivitySelectType.class));
        }
        if (id == R.id.chat_signIn) {
            if (mFirebaseAuth.getCurrentUser() != null) {
                mFirebaseAuth.signOut();
                LoginManager.getInstance().logOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                assignUser();
            } else
                startActivityForResult(new Intent(this, ActivitySignIn.class), SignInREQCODE);
        } else if (id == R.id.sendButton) {
            MessageModel messageModel = new
                    MessageModel(binding.messageEditText.getText().toString(),
                    mUsername,
                    mPhotoUrl,
                    Calendar.getInstance().getTimeZone().getDisplayName(),
                    System.currentTimeMillis());
            reference.child(MESSAGES_CHILD).push().setValue(messageModel);
            binding.messageEditText.setText("");
        } else if (id == R.id.upDown) {
            if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        } else if (id == R.id.test) {
            startActivity(new Intent(this, TestActivity.class));
        }
    }

    @Override
    public void onMenuItemClick(int id) {
        if (id == R.id.ivSettings || id == R.id.ivStat) {
            Intent intent = new Intent(this, ActivityFAB.class);
            intent.putExtra("id", id);
            startActivityForResult(intent, REQCODE);
            themeWas = preferences.getBoolean(getString(R.string.pref_keyTheme));
        } else if (id == R.id.ivLike || id == R.id.ivLeaderBoard) {
            Snackbar.make(binding.mainContainer, R.string.notReady, Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQCODE && themeWas != preferences.getBoolean(getString(R.string.pref_keyTheme))) {
            recreate();
        } else if (requestCode == SignInREQCODE && resultCode == RESULT_OK) {
            assignUser();
        }
    }

    @Override
    public void onBackPressed() {
        if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else
            super.onBackPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (behavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
            isOnTop = true;
            impulse.start();
        }
    }

    @Override
    protected void onStop() {
        isOnTop = false;
        impulse.end();
        binding.messageEditText.setFocusable(false);
        binding.messageEditText.setFocusableInTouchMode(true);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mFirebaseAdapter.unregisterAdapterDataObserver(observer);
        mGoogleApiClient.stopAutoManage(this);
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.e(TAG, "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
    }
}
