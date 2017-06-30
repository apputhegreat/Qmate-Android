package com.quotemate.qmate.util;

import io.realm.RealmObject;

/**
 * Created by anji kinnara on 6/20/17.
 */

public class BookMarkQuoteId extends RealmObject {
    public String id;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
}
