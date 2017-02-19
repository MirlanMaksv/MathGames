package com.example.acer.transitions_everywhere;

import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.example.acer.transitions_everywhere.preferences.PrefsHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Mirlan on 23.09.2016.
 */
public class Utils {

    public static ArrayList<String> getButtons() {
        ArrayList<String> buttons = new ArrayList<>();
        String btn = PrefsHelper.getPrefsHelper().getString(PrefsHelper.BUTTONS);
        if (btn != null)
            Collections.addAll(buttons, btn.split(","));
        return buttons;
    }

    public static int convertDpToPixel(int dp){
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return Math.round(px);
    }

    public static int convertPixelsToDp(int px) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return Math.round(px / (metrics.densityDpi / 160f));
    }
}
