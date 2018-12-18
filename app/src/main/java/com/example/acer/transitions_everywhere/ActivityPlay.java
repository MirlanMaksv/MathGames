package com.example.acer.transitions_everywhere;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.example.acer.transitions_everywhere.databinding.ActivityPlayBinding;
import com.example.acer.transitions_everywhere.fragments.KeyboardRecycler;
import com.example.acer.transitions_everywhere.fragments.TimeOut;
import com.example.acer.transitions_everywhere.fragments.TrueFalse;
import com.example.acer.transitions_everywhere.preferences.PrefsHelper;
import com.example.acer.transitions_everywhere.taskgenerators.GeneratorTrueFalse;
import com.example.acer.transitions_everywhere.taskgenerators.GeneratorZeroToNine;
import com.example.acer.transitions_everywhere.taskgenerators.TaskGenerator;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by Mirlan on 25.09.2016.
 */

public class ActivityPlay extends AppCompatActivity {

    private ActivityPlayBinding binding;
    private CountDownTimer timer;
    private TaskGenerator task;
    private Animation errorAnim;
    private AnimatorSet animCorrect, animIncorrect, hideTimer, showTimer;
    private ScaleAnimation scaleAnimation;
    private ArrayList<String> correctList, incorrectList;

    private Bundle bundleExtra;
    private String curTask, result, answer;
    private int correctAns, incorrectAns, gameType, time;
    private boolean isOnTop, isCountingDown = true, isKBClickable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if (PrefsHelper.getPrefsHelper() == null)
            new PrefsHelper(this);
        if (PrefsHelper.getPrefsHelper().getBoolean(getString(R.string.pref_keyTheme)))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);

        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_play);

        correctList = new ArrayList<>();
        incorrectList = new ArrayList<>();

        if (savedInstanceState == null) {
            setFragment();
            if (time != 0)
                timer = new CountDownTimer(time, 60) {
                    @Override
                    public void onTick(long l) {
                        int sec = (int) (l / 1000);
                        if (sec <= 10 || sec >= time / 1000 - 6) {
                            if (sec > 5)
                                binding.tvTimer.setText(String.valueOf(sec));
                            else if (sec <= 5) {
                                int millis = (int) (l % 1000) / 100;
                                binding.tvTimer.setText(String.format(Locale.US, "%d.%d", sec, millis));
                            }
                            if (sec == time / 1000 - 6 && time / 1000 - 6 >= 20)
                                hideTimer.start();
                            else if (sec <= 10)
                                showTimer.start();

                        }
                    }

                    @Override
                    public void onFinish() {
                        isCountingDown = false;
                        showEndDialog();
                    }
                };
            setupAnimations();
        } else {
            Intent intent = new Intent(new Intent(this, ActivityPlay.class));
            intent.putExtra(Const.BUNDLE, savedInstanceState.getBundle(Const.BUNDLE));
            startActivity(intent);
            finish();
        }
    }

    public void onClick(String s) {
        if (!animIncorrect.isRunning() && isKBClickable) {
            if (s.equals("D")) {    // button delete
                if (answer.length() - 1 >= 0)
                    answer = answer.substring(0, answer.length() - 1);
            } else answer += s;     // number buttons
            handleInput();
        }
    }

    private void handleInput() {
        if (gameType != 1)
            binding.tvAnswer.setText(answer);
        if (answer.length() >= result.length()) {
            if (answer.equals(result)) {    // answer is CORRECT
                animCorrect.start();
                ++correctAns;
                correctList.add(curTask.replace("?", answer));
                setTask(true);
            } else {                        // WRONG answer
                animIncorrect.start();
                binding.inputContainer.startAnimation(errorAnim);
                int color = ContextCompat.getColor(this, R.color.colorIncorrect);
                binding.tvTask.setTextColor(color);
                binding.tvAnswer.setTextColor(color);
                ++incorrectAns;
                incorrectList.add(curTask.replace("?", answer));
            }
        }
    }

    private void setTask(boolean isCorrect) {
        curTask = task.newTask(isCorrect);
        result = task.getRes();
        answer = "";

        String corAns = String.valueOf(correctAns);
        String incorAns = String.valueOf(incorrectAns);
        binding.tvAnswer.setText(answer);
        binding.tvTask.setText(curTask);
        binding.tvSolved.setText(corAns);
        binding.tvIncorrect.setText(incorAns);
    }

    private void showEndDialog() {
        if (isOnTop)
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.keyboardContainer, TimeOut.newInstance
                            (correctList, incorrectList, bundleExtra))
                    .commit();
    }

    private void setFragment() {
        bundleExtra = getIntent().getBundleExtra(Const.BUNDLE);
        gameType = bundleExtra.getInt(Const.GAME_TYPE, 0);
        time = getResources().getInteger(R.integer.timer);
        Fragment fragment = null;
        if (gameType == 0) {
            fragment = new KeyboardRecycler();
            task = new GeneratorZeroToNine();
        } else if (gameType == 1) {
            fragment = new TrueFalse();
            task = new GeneratorTrueFalse();
        } else if (gameType == 2) {
            ArrayList<Integer> ops = bundleExtra.getIntegerArrayList(Const.OPERATORS);
            int gameChosen = bundleExtra.getInt(Const.GAME_CHOSEN);
            int from = bundleExtra.getInt(Const.FROM, 0);
            int to = bundleExtra.getInt(Const.TO, 0);
            time = bundleExtra.getBoolean(Const.NOTIME, false) ? 0 : bundleExtra.getInt(Const.TIME, 0);
            fragment = gameChosen == 0 ? new KeyboardRecycler() : new TrueFalse();
            task = gameChosen == 0 ? new GeneratorZeroToNine(from, to, ops) : new GeneratorTrueFalse(from, to, ops);
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.keyboardContainer, fragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBundle(Const.BUNDLE, bundleExtra);
        super.onSaveInstanceState(outState);
    }

    private void setupAnimations() {
        binding.tvTask.setTextSize(55);
        binding.tvTask.post(new Runnable() {
            @Override
            public void run() {
                final ScaleAnimation animUpperContainer = new ScaleAnimation(0, 1, 0, 1, binding.contentContainer.getWidth() / 2, 0);
                animUpperContainer.setDuration(500);
                animUpperContainer.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        if (timer != null)
                            timer.start();
                        isKBClickable = true;
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });

                final AlphaAnimation animKeyboard = new AlphaAnimation(0, 1);
                animKeyboard.setDuration(500);

                int centerX = binding.tvTask.getWidth() / 2;
                int centerY = binding.tvTask.getHeight() / 2;
                scaleAnimation = new ScaleAnimation(1, 0, 1, 0, centerX, centerY);
                scaleAnimation.setDuration(1000);
                scaleAnimation.setRepeatCount(3);
                scaleAnimation.setAnimationListener(new Animation.AnimationListener() {
                    private int countDown = 3;

                    @Override
                    public void onAnimationStart(Animation animation) {
                        binding.tvTask.setText(String.valueOf(countDown));
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        binding.tvTask.setTextSize(35);
                        binding.contentContainer.startAnimation(animUpperContainer);
                        binding.keyboardContainer.startAnimation(animKeyboard);

                        binding.keyboardContainer.setVisibility(View.VISIBLE);
                        binding.ivCorrect.setVisibility(View.VISIBLE);
                        binding.ivIncorrect.setVisibility(View.VISIBLE);
                        binding.ivTime.setVisibility(View.VISIBLE);
                        setTask(false);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        --countDown;
                        binding.tvTask.setText(String.valueOf(countDown));
                    }
                });
                binding.tvTask.startAnimation(scaleAnimation);
            }
        });

        errorAnim = new TranslateAnimation(0, 15, 0, 0);
        errorAnim.setDuration(300);
        errorAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                int color = ContextCompat.getColor(ActivityPlay.this, R.color.colorTextPrimary);
                binding.tvTask.setTextColor(color);
                binding.tvAnswer.setTextColor(color);
                setTask(false);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        errorAnim.setInterpolator(new BounceInterpolator());

        animCorrect = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_scale_up_down);
        animCorrect.setTarget(binding.ivCorrect);

        animIncorrect = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_scale_up_down);
        animIncorrect.setTarget(binding.ivIncorrect);

        showTimer = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_show_timer);
        showTimer.setTarget(binding.tvTimer);

        hideTimer = (AnimatorSet) AnimatorInflater.loadAnimator(this, R.animator.animator_hide_timer);
        hideTimer.setTarget(binding.tvTimer);
    }

    @Override
    public void onStart() {
        super.onStart();
        isOnTop = true;
        if (!isCountingDown)
            showEndDialog();
    }

    @Override
    protected void onStop() {
        isOnTop = false;
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (timer != null)
            timer.cancel();
        super.onDestroy();
    }
}
