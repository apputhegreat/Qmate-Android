package com.quotemate.qmate.model;

/**
 * Created by anji kinnara on 6/2/17.
 */

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Quote extends RealmObject {

    public Quote() {
    }

    @PrimaryKey
    public String id;
    public String text;
    public RealmList<RealmString> tags;
    public String author;
    public String authorId;
    public long likes;
    public long shares;
    public String moodCode;
    public String mood;
    public boolean isBookMarked;
    public boolean isLiked;
}
