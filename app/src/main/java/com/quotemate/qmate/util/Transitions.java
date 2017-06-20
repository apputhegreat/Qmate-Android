package com.quotemate.qmate.util;

/**
 * Created by anji kinnara on 6/16/17.
 */

import android.support.v7.app.AppCompatActivity;

import com.quotemate.qmate.R;

/**
 * Created by anji kinnara on 1/24/17.
 */

public class Transitions {
    public static void leftToRight(AppCompatActivity compatActivity) {
        compatActivity.overridePendingTransition(R.anim.left_to_right, R.anim.exit_left_to_right);
    }

    public static void rightToLeft(AppCompatActivity compatActivity) {
        compatActivity.overridePendingTransition(R.anim.right_to_left, R.anim.exit_right_to_left);
    }
}