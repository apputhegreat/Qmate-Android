package com.quotemate.qmate.util;

import android.support.annotation.NonNull;

import com.facebook.AccessToken;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.MainActivity;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.model.Quote;


/**
 * Created by anji kinnara on 6/28/17.
 */

public class FBUtil {
    public  static boolean NEW_LOGIN = false;
    public static void updateLikes(String quoteId, final long increment) {
        FirebaseDatabase.getInstance().getReference("quotes").child(quoteId).child("likes").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + increment);
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    System.out.println("Firebase counter increment failed.");
                } else {
                    System.out.println("Firebase counter increment succeeded.");
                }
            }
        });
    }

    public static FirebaseAuth.AuthStateListener getAuthStateListener(final MainActivity activity) {
       FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser fUser = firebaseAuth.getCurrentUser();
                if (fUser != null) {
                    final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid());
                    setCurrentUser(fUser, mUserRef, activity);
                } else {
                    User.currentUser = null;
                    activity.setSpinTxtAppearance();
                }
            }
        };
        return authListener;
    }

    private static void setCurrentUser(final FirebaseUser fUser, final DatabaseReference mUserRef, final MainActivity activity) {
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserRef.child("deviceOS").setValue("Android");
                if (!dataSnapshot.exists()) {
                    UserInfo profile = null;
                    for(UserInfo profilei : fUser.getProviderData()) {
                        profile = profilei;
                    }
                    User user = new User();
                    user.id = fUser.getUid();
                    mUserRef.child("name").setValue(profile.getDisplayName());
                    user.name = profile.getDisplayName();
                    user.readbleId = profile.getDisplayName();
                    if (profile.getEmail() != null) {
                        mUserRef.child("email").setValue(profile.getEmail());
                        user.email = profile.getEmail();
                    }
                    mUserRef.child("readableId").setValue(profile.getDisplayName());
                    if (profile.getPhotoUrl() != null) {
                        mUserRef.child("photoURL").setValue(profile.getPhotoUrl().toString());
                        user.photoURL = profile.getPhotoUrl().toString();
                    }
                    User.currentUser = user;
                } else {
                    User.currentUser = dataSnapshot.getValue(User.class);
                    User.currentUser.id =  fUser.getUid();
                    for(UserInfo profilei : fUser.getProviderData()) {
                        User.currentUser.photoURL = profilei.getPhotoUrl().toString();
                    }
                }
                if(FBUtil.NEW_LOGIN) {
                    for (Quote quote: QuotesUtil.quotes
                         ) {
                        QuotesUtil.synchWithUser(quote);
                    }
                }
                if(User.currentUser!= null && User.currentUser.id!=null) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        String facebookUserId  = null;
                        for(UserInfo profile : fUser.getProviderData()) {
                            facebookUserId = profile.getUid();
                        }
                        User.currentUser.photoURL =  "https://graph.facebook.com/" + facebookUserId + "/picture?height=500";
                    }
                }
                activity.setSpinTxtAppearance();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
