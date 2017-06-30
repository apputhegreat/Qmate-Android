package com.quotemate.qmate.util;

import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;

import com.quotemate.qmate.model.Author;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.Random;

/**
 * Created by anji kinnara on 6/13/17.
 */

public class RandomSelector {
    public static Random randomGenerator = new Random();

    public static Author getRandomAuthor(LinkedHashMap<String, Author> authors) {
        Author author = null;
        if (authors != null && !authors.isEmpty()) {
            Object[] keys = authors.keySet().toArray();
            int next = randomGenerator.nextInt(keys.length);
            Object key = keys[next];
            author = authors.get(key.toString());
        }
        return author;
    }

    public static String getRandomTag(ArrayList<String> tags) {
        String tag = null;
        if (tags != null && !tags.isEmpty()) {
            int next = randomGenerator.nextInt(tags.size());
            tag = tags.get(next);
        }
        return tag;
    }

    public static Animation getAnimation(int counts) {
//        Animation translateAnimation = new TranslateAnimation(0, 0,0, 1);
//        translateAnimation.setInterpolator(new CycleInterpolator(counts));
//        translateAnimation.setDuration(1200);
//        return translateAnimation;
        RotateAnimation rotate = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotate.setDuration(1200);
        rotate.setInterpolator(new LinearInterpolator());
        return rotate;

    }

    public static void setXRotaionAnimation(View view, int count, int duration) {
        ObjectAnimator animation = ObjectAnimator.ofFloat(view, "rotationX", 0.0f, 7200.0f);
        animation.setDuration(duration);
        animation.setRepeatCount(count);
        animation.setInterpolator(new LinearInterpolator());
        animation.start();
    }
}
