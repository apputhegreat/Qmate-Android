package com.quotemate.qmate.util;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.quotemate.qmate.BookMarksActivity;
import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.User;

import java.util.Objects;

/**
 * Created by anji kinnara on 7/8/17.
 */

public class BookMarkUtil {
    AppCompatActivity context;
    public BookMarkUtil(AppCompatActivity activity) {
        context = activity;
    }

    public void handleBookMark(Quote quote, AppCompatImageView bookMarkImgView,final boolean updateUI) {
        if (User.currentUser != null) {
            if(quote.isBookMarked) {
                int pos=-1;
                for (int i = 0; i <User.currentUser.bookMarkedQuoteIds.size() ; i++) {
                    if(Objects.equals(User.currentUser.bookMarkedQuoteIds.get(i),quote.id)){
                        pos = i;
                    }
                }
                if(pos!=-1) {
                    quote.isBookMarked = false;
                    User.currentUser.bookMarkedQuoteIds.remove(pos);
                    if(updateUI) {
                        bookMarkImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_border_black_24dp));
                    }
                }
            } else {
                if(updateUI) {
                    bookMarkImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_star_black_24dp));
                }
                quote.isBookMarked = true;
                User.currentUser.bookMarkedQuoteIds.add(quote.id);
            }
            FirebaseDatabase.getInstance().getReference("users")
                    .child(User.currentUser.id)
                    .child("bookMarkedQuoteIds")
                    .setValue(User.currentUser.bookMarkedQuoteIds);
        }
    }
}
