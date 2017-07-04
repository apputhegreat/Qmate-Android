package com.quotemate.qmate;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
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
        QuotesUtil quotesUtil = new QuotesUtil(null);
        quotesUtil.addQuotesListener();
    }
}
