package com.quotemate.qmate.model;

import java.util.ArrayList;

/**
 * Created by anji kinnara on 6/2/17.
 */

public class Quote {

    public Quote() {
    }

    public String id;
    public String text;
    public ArrayList<String> tags;
    public String author;
    public String authorId;
    public long likes;
    public long shares;
    public String moodCode;
    public String mood;
    public boolean isBookMarked;
    public boolean isLiked;
}
