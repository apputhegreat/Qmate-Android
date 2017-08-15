package com.quotemate.qmate.util;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.quotemate.qmate.BuildConfig;
import com.quotemate.qmate.R;

/**
 * Created by anji kinnara on 1/31/17.
 */

public class RemoteConfigController {
    AppCompatActivity mActivity;
    FirebaseRemoteConfig mFirebaseRemoteConfig;

    public RemoteConfigController(AppCompatActivity activity) {
        mActivity = activity;
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);
        mFirebaseRemoteConfig.setDefaults(R.xml.remote_config_defaults);
        fetchRemoteConfig();
    }

    void fetchRemoteConfig() {
        long cacheExpiration = 24 * 3600 ;
        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();
                        } else {
                            Log.d("Config", "fetch failed");
                        }
                    }
                });
    }
}