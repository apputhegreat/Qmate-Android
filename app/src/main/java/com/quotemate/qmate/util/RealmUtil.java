package com.quotemate.qmate.util;

import com.quotemate.qmate.model.Quote;

import java.util.ArrayList;

import io.realm.Realm;

/**
 * Created by anji kinnara on 6/20/17.
 */

public class RealmUtil {
    public static ArrayList<Quote> getBookMarks() {
        Realm realm = Realm.getDefaultInstance();
        ArrayList<Quote> list = new ArrayList<>();
        try {
            list = new ArrayList<>(realm.where(Quote.class).findAll());

        } catch (Exception ex) {
            return list;
        }
        return list;
    }
}
