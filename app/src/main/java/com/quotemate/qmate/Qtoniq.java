package com.quotemate.qmate;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.util.QuotesUtil;

/**
 * Created by anji kinnara on 7/4/17.
 */

public class Qtoniq extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/SourceSansPro-Regular.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build()
//        );
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(fUser.getUid());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        User.currentUser = dataSnapshot.getValue(User.class);
                        User.currentUser.id =  fUser.getUid();
                        setQuotes();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            setQuotes();
        }
    }

    private void setQuotes() {
        QuotesUtil quotesUtil = new QuotesUtil(null);
        quotesUtil.addQuotesListener();
    }
}
