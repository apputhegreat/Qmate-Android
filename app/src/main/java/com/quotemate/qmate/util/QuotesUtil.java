package com.quotemate.qmate.util;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.Interfaces.IUpdateView;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.RealmString;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import io.realm.RealmList;

/**
 * Created by anji kinnara on 6/7/17.
 */

public class QuotesUtil {
    private final ValueEventListener tagsListener;
    private final ChildEventListener quotesChildListener;
    private ValueEventListener quotesListener;
    private ValueEventListener authorsListener;
    private IUpdateView updateView;
    DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("quotes");
    DatabaseReference authorsRef = FirebaseDatabase.getInstance().getReference("authors");
    DatabaseReference tagsRef = FirebaseDatabase.getInstance().getReference("tags");
    public static LinkedHashMap<String, Author> authors = new LinkedHashMap<>();
    public static ArrayList<Quote> quotes = new ArrayList<>();
    public static ArrayList<String> tags = new ArrayList<>();
    public static ArrayList<BookMarkQuoteId> bookMarkQuoteIds = new ArrayList<>();

    public QuotesUtil(final IUpdateView updateView) {
        this.updateView = updateView;
        quotesChildListener =  new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Quote quote = getQuote(dataSnapshot);
                    quotes.add(quote);
                    callUpdateView(quotes);
                } catch (Exception ex) {
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
//
//        quotesListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                ArrayList<Quote> quotesNew = new ArrayList<>();
//                for (DataSnapshot snap : dataSnapshot.getChildren()
//                        ) {
//                    try {
//                        Quote quote = getQuote(snap);
//                        quotesNew.add(quote);
//                    } catch (Exception ex) {
//                        continue;
//                    }
//                }
//                quotes = quotesNew;
//                callUpdateView(quotes);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        };
        authorsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedHashMap<String, Author> authorsNew = new LinkedHashMap<>();
                Author all = new Author();
                all.name = "All";
                all.id = "-1";
                authorsNew.put("-1",all);
                for (DataSnapshot snap : dataSnapshot.getChildren()
                        ) {
                    Author author = snap.getValue(Author.class);
                    authorsNew.put(snap.getKey(), author);
                }
                authors = authorsNew;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        tagsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> tagsNew = new ArrayList<>();
                tagsNew.add("All");
                for (DataSnapshot snap : dataSnapshot.getChildren()
                        ) {
                    String tag = snap.getValue().toString();
                    tagsNew.add(tag);
                }
                tags = tagsNew;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    public static Quote getQuote(DataSnapshot snap) {
        Quote quote = new Quote();
        try {
            quote.id = snap.getKey();
            quote.text = snap.child("text").getValue().toString();
            quote.authorId = snap.child("authorId").getValue().toString();
            quote.author = snap.child("author").getValue().toString();
            if( snap.child("likes").exists()) {
                quote.likes =(int) snap.child("likes").getValue();
            }
            quote.tags = new RealmList<>();
            for (DataSnapshot sanpshot: snap.child("tags").getChildren()
                 ) {
                quote.tags.add(new RealmString(sanpshot.getValue().toString()));
            }
        } catch (Exception ex) {
          throw  ex;
        }
        return quote;
    }

    public void addQuotesListener() {
        quotesRef.addChildEventListener(quotesChildListener);
    }

    public void addAuthorsListener() {
        authorsRef.addListenerForSingleValueEvent(authorsListener);
        tagsRef.addListenerForSingleValueEvent(tagsListener);
    }

    public void removeAuthorsListenr() {
        authorsRef.removeEventListener(authorsListener);
    }

    public void removeQuotesListener() {
        quotesRef.removeEventListener(quotesChildListener);
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
