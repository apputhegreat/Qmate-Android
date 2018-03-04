package com.quotemate.qmate.Recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.Services.NotificationQOD;

/**
 * Created by anji kinnara on 7/15/17.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        FirebaseDatabase.getInstance().getReference("quoteOftheDay/id")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            String id = dataSnapshot.getValue().toString();
                            FirebaseDatabase.getInstance().getReference("quotes/"+id).addListenerForSingleValueEvent(
                                    new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if(dataSnapshot.exists()) {
                                                String text = dataSnapshot.child("text").getValue().toString();
                                                String author = dataSnapshot.child("author").getValue().toString();
                                                Intent intent1 = new Intent(context, NotificationQOD.class);
                                                intent1.putExtra("body", text + "\n" + "--"+author);
                                                intent1.putExtra("quoteOftheDay", true);
                                                intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                context.startService(intent1);
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    }
                            );

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

    }
}
