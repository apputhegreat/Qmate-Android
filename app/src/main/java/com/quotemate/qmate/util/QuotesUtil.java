package com.quotemate.qmate.util;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.Interfaces.IUpdateView;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Created by anji kinnara on 6/7/17.
 */

public class QuotesUtil {
    private final ValueEventListener tagsListener;
    private ValueEventListener quotesListener;
    private ValueEventListener authorsListener;
    private IUpdateView updateView;
    DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("quotes");
    DatabaseReference authorsRef = FirebaseDatabase.getInstance().getReference("authors");
    DatabaseReference tagsRef = FirebaseDatabase.getInstance().getReference("tags");
    public static LinkedHashMap<String, Author> authors = new LinkedHashMap<>();
    public static ArrayList<Quote> quotes = new ArrayList<>();
    public static ArrayList<String> tags = new ArrayList<>();

    public QuotesUtil(final IUpdateView updateView) {
        this.updateView = updateView;
        quotesListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()
                        ) {
                    Quote quote = snap.getValue(Quote.class);
                    quotes.add(quote);
                }
                callUpdateView(quotes);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        authorsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()
                        ) {
                    Author author = snap.getValue(Author.class);
                    authors.put(snap.getKey(), author);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snap : dataSnapshot.getChildren()
                        ) {
                    String tag = snap.getValue().toString();
                    tags.add(tag);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public void addQuotesListener() {
        quotesRef.addValueEventListener(quotesListener);
        tagsRef.addValueEventListener(tagsListener);
    }

    public void addAuthorsListener() {
        authorsRef.addValueEventListener(authorsListener);
    }

    public void removeAuthorsListenr() {
        authorsRef.removeEventListener(authorsListener);
    }

    public void removeQuotesListener() {
        quotesRef.removeEventListener(quotesListener);
        tagsRef.removeEventListener(tagsListener);
    }

    public void callUpdateView(ArrayList<Quote> quotes) {
        this.updateView.updateView(quotes);
    }

    public static Author getAuthorByName(String name) {
        Author result = null;
        if (Objects.equals(name, "All"))
            return null;
        for (String key : authors.keySet()) {
            Author author = authors.get(key);
            if (Objects.equals(author.name, name)) {
                result = author;
                break;
            }
        }
        return result;
    }
}
