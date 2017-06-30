package com.quotemate.qmate.model;

import io.realm.RealmObject;

/**
 * Created by anji kinnara on 6/20/17.
 */

public class RealmString extends RealmObject {
    private String val;

    public RealmString(String val) {
        this.val = val;
    }
    public RealmString() {
    }
    public String getValue() {
        return val;
    }

    public void setValue(String value) {
        this.val = value;
    }
}