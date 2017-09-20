package com.quotemate.qmate.util;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.User;

import java.util.Objects;

/**
 * Created by anji kinnara on 7/8/17.
 */

public class LikeUtil {
    AppCompatActivity context;
    public LikeUtil(AppCompatActivity activity) {
        context = activity;
    }

    public void handleLike(Quote quote, AppCompatImageView likeImgView, TextView badge) {
        Analytics.sendLikeEvent();
        if (User.currentUser != null) {
            if (quote.isLiked) {
                int pos = -1;
                for (int i = 0; i < User.currentUser.likedQuoteIds.size(); i++) {
                    if (Objects.equals(User.currentUser.likedQuoteIds.get(i), quote.id)) {
                        pos = i;
                    }
                }
                if (pos != -1) {
                    quote.isLiked = false;
                    User.currentUser.likedQuoteIds.remove(pos);
                    likeImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.like));
                    FBUtil.updateLikes(quote.id, -1);
                    quote.likes = quote.likes - 1;
                }
            } else {
                quote.isLiked = true;
                likeImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.liked));
                User.currentUser.likedQuoteIds.add(quote.id);
                FBUtil.updateLikes(quote.id, +1);
                quote.likes = quote.likes + 1;
            }
            FirebaseDatabase.getInstance().getReference("users")
                    .child(User.currentUser.id)
                    .child("likedQuoteIds")
                    .setValue(User.currentUser.likedQuoteIds);
        }
        if (badge != null) {
            if (quote.likes > 0) {
                badge.setVisibility(View.VISIBLE);
            } else {
                badge.setVisibility(View.INVISIBLE);
            }
            badge.setText(String.valueOf(quote.likes));
        }
    }
}
