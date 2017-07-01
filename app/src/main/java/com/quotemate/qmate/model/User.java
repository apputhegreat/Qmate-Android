package com.quotemate.qmate.model;

import java.util.ArrayList;

/**
 * Created by anji kinnara on 6/30/17.
 */

public class User {
    public static User currentUser;
    public ArrayList<String> likedQuoteIds = new ArrayList<>();
    public ArrayList<String> bookMarkedQuoteIds = new ArrayList<>();
    public String id;
    public String email;
    public String name;
    public String photoURL;
    public String readbleId;

    public User() {
    }

}
