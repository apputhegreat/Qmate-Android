package com.quotemate.qmate.util;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import co.mobiwise.materialintro.shape.Focus;
import co.mobiwise.materialintro.shape.FocusGravity;
import co.mobiwise.materialintro.view.MaterialIntroView;

/**
 * Created by anji kinnara on 7/13/17.
 */

public class IntroUtil {
    AppCompatActivity context;

    public IntroUtil(AppCompatActivity context) {
        this.context = context;
    }

    public void showSpinInfo(View target, int delay, String info) {
        new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(delay)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(info)
                .setTarget(target)
                .setUsageId("showSpinInfo") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    public void showLoginInfo(View target, int delay, String info) {
        new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(delay)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(info)
                .setTarget(target)
                .setUsageId("showLoginInfo") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    public void showActionsInfo(View target, int delay, String info) {
        new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(delay)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(info)
                .setTarget(target)
                .setUsageId("showActionsInfo") //THIS SHOULD BE UNIQUE ID
                .show();
    }

    public void showSwipeInfo(View target, int delay, String info) {
       final MaterialIntroView view = new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(delay)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(info)
                .setTarget(target)
                .setUsageId("showSwipeInfo") //THIS SHOULD BE UNIQUE ID
                .show();
       new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.dismiss();
            }
        }, 5000);
    }

    public void showSearchInfo(View target, int delay, String info) {
        new MaterialIntroView.Builder(context)
                .enableDotAnimation(true)
                .enableIcon(false)
                .setFocusGravity(FocusGravity.CENTER)
                .setFocusType(Focus.MINIMUM)
                .setDelayMillis(delay)
                .enableFadeAnimation(true)
                .performClick(true)
                .setInfoText(info)
                .setTarget(target)
                .setUsageId("showSearchInfo") //THIS SHOULD BE UNIQUE ID
                .show();
    }
}
