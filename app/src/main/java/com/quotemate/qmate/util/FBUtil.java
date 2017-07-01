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
import com.quotemate.qmate.model.User;

/**
 * Created by anji kinnara on 6/28/17.
 */

public class FBUtil {
    public static void updateLikes(String quoteId) {
        FirebaseDatabase.getInstance().getReference("quotes").child(quoteId).child("likes").runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue() == null) {
                    currentData.setValue(1);
                } else {
                    currentData.setValue((Long) currentData.getValue() + 1);
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

    public static FirebaseAuth.AuthStateListener getAuthStateListener() {
       FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser fUser = firebaseAuth.getCurrentUser();
                if (fUser != null) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                       final DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("users/" + fUser.getUid());
                        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mUserRef.child("deviceOS").setValue("Android");
                                if (!dataSnapshot.exists()) {
                                    UserInfo profile = fUser.getProviderData().get(0);
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
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        };
        return authListener;
    }
}
