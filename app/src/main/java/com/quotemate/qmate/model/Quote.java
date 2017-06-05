package com.quotemate.qmate.model;

/**
 * Created by anji kinnara on 6/2/17.
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Quote implements Serializable {
    public static List<Quote> SampleQuotes = new ArrayList() { {
        add(new Quote("If you want to shine like a sun, first burn like a sun","Abdul kalam"));
        add(new Quote("Keep love in your heart. A life without it is like a sunless garden when the flowers are dead.","Oscar Wilde"));
        add(new Quote("Success is a science; if you have the conditions, you get the result.","Oscar Wilde"));
        add(new Quote("Men always want to be a woman's first love - women like to be a man's last romance.","Oscar Wilde"));
        add(new Quote("Women are made to be loved, not understood.","Oscar Wilde"));
        add(new Quote("True friends stab you in the front.","Oscar Wilde"));
        add(new Quote("Live as if you were to die tomorrow. Learn as if you were to live forever","Mahatma Gandhi"));
        add(new Quote("An eye for an eye will only make the whole world blind.","Mahatma Gandhi"));
    }
    };

    public Quote(String id, String text, String author, String authorId, int likes, int shares, String moodCode) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.authorId = authorId;
        this.likes = likes;
        this.shares = shares;
        this.moodCode = moodCode;
    }

    public Quote(String text, String author) {
        this.text = text;
        this.author = author;
        this.authorId = "1";
        this.likes = 12;
        this.shares = 23;
        this.moodCode = "2";
    }

    public String id;
    public String text;
    public String author;
    public String authorId;
    public int likes;
    public int shares;
    public String moodCode;
}
