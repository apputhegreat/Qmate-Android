package com.quotemate.qmate;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.firebase.database.FirebaseDatabase;
import com.quotemate.qmate.util.Analytics;

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
        Analytics.init(this);
        Analytics.sendStartedAppEvent();
    }



    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) { // connected to the internet
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                // connected to wifi
            } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // connected to the mobile provider's data plan
            }
            return true;
        } else {
            // not connected to the internet
            return false;
        }

    }
}
