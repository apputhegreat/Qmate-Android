package com.quotemate.qmate.model;

/**
 * Created by anji kinnara on 6/2/17.
 */

import java.io.Serializable;
import java.util.ArrayList;

public class Quote implements Serializable {

    public Quote() {
    }

    public String id;
    public String text;
    public ArrayList<String> tags;
    public String author;
    public String authorId;
    public int likes;
    public int shares;
    public String moodCode;
    public String mood;
}
