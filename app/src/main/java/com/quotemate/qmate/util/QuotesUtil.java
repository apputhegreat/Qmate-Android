package com.quotemate.qmate.util;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.MainActivity;
import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.User;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by anji kinnara on 6/7/17.
 */

public class QuotesUtil {
    private final ValueEventListener tagsListener;
    private final ChildEventListener quotesChildListener;
    private final ValueEventListener quoteOftheDayListener;
    private final ValueEventListener quotesEventListener;
    private final ValueEventListener trendingsListener;
    private ValueEventListener authorsListener;

    MainActivity mainActivity;
    DatabaseReference quotesRef = FirebaseDatabase.getInstance().getReference("quotes");
    DatabaseReference authorsRef = FirebaseDatabase.getInstance().getReference("authors");
    DatabaseReference tagsRef = FirebaseDatabase.getInstance().getReference("tags");
    DatabaseReference quoteOfDayRef = FirebaseDatabase.getInstance().getReference("quoteOftheDay");
    DatabaseReference trendingRef = FirebaseDatabase.getInstance().getReference("trending");
    public static LinkedHashMap<String, Author> authors = new LinkedHashMap<>();
    public static ArrayList<Quote> quotes = new ArrayList<>();
    public static ArrayList<String> tags = new ArrayList<>();
    private static String quoteOftheDayId;
    public static Quote quoteOftheDay;
    public static ArrayList<String> trendingAuthors = new ArrayList<>();
    public static ArrayList<String> trendingTags = new ArrayList<>();
    int count = 0;

    public QuotesUtil(final MainActivity mainActivity) {
        this.mainActivity = mainActivity;
        trendingRef.keepSynced(true);
        tagsRef.keepSynced(true);
        quoteOfDayRef.keepSynced(true);
        quotesRef.keepSynced(true);
        authorsRef.keepSynced(true);
        quotesEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               Log.d("done","We're done loading the initial "+dataSnapshot.getChildrenCount()+" items");
                updateView();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        quotesChildListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                try {
                    Quote quote = getQuote(dataSnapshot);
                    quotes.add(quote);
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

        authorsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                LinkedHashMap<String, Author> authorsNew = new LinkedHashMap<>();
                Author all = new Author();
                all.name = "All";
                all.id = "-1";
                authorsNew.put("-1", all);
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

        quoteOftheDayListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    quoteOftheDayId = dataSnapshot.child("id").getValue().toString();
                } else {
                    mainActivity.myProgressBar.hideProgressBar();
                }
                quotesRef.addChildEventListener(quotesChildListener);
                quotesRef.addListenerForSingleValueEvent(quotesEventListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                mainActivity.myProgressBar.hideProgressBar();
            }
        };

        trendingsListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot trendAuth = dataSnapshot.child("authors");
                    if (trendAuth.exists()) {
                        for (DataSnapshot item : trendAuth.getChildren()
                                ) {
                            trendingAuthors.add(item.getValue().toString());
                        }
                    }
                    DataSnapshot trentags = dataSnapshot.child("tags");
                    if (trentags.exists()) {
                        for (DataSnapshot item : trentags.getChildren()
                                ) {
                            trendingTags.add(item.getValue().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
    }

    private void updateView() {
        mainActivity.myProgressBar.hideProgressBar();
        mainActivity.updateView(quotes, quoteOftheDay);
    }

    public Quote getQuote(DataSnapshot snap) {
        Quote quote = new Quote();
        try {
            quote.id = snap.getKey();
            quote.text = snap.child("text").getValue().toString();
            quote.authorId = snap.child("authorId").getValue().toString();
            quote.author = snap.child("author").getValue().toString();
            if (snap.child("likes").exists()) {
                quote.likes = (long) snap.child("likes").getValue();
            }
            quote.tags = new ArrayList<>();
            for (DataSnapshot sanpshot : snap.child("tags").getChildren()
                    ) {
                quote.tags.add(sanpshot.getValue().toString());
            }
            quote = synchWithUser(quote);
            if (Objects.equals(quote.id, quoteOftheDayId)) {
                quoteOftheDay = quote;
            }
        } catch (Exception ex) {
            if (mainActivity.myProgressBar != null) {
                mainActivity.myProgressBar.hideProgressBar();
            }
            throw ex;
        }
        return quote;
    }

    private static Quote synchWithUser(Quote quote) {
        if (User.currentUser != null) {
            if (!User.currentUser.bookMarkedQuoteIds.isEmpty()) {
                if (User.currentUser.bookMarkedQuoteIds.contains(quote.id)) {
                    quote.isBookMarked = true;
                }
            }
            if (!User.currentUser.likedQuoteIds.isEmpty()) {
                if (User.currentUser.likedQuoteIds.contains(quote.id)) {
                    quote.isLiked = true;
                }
            }
            return quote;
        } else {
            return quote;
        }
    }

    public void addQuotesListener(boolean addAllListeners) {
        if (addAllListeners) {
            quoteOfDayRef.addListenerForSingleValueEvent(quoteOftheDayListener);
            addAuthorsListener();
        } else {
            quotesRef.addChildEventListener(quotesChildListener);
        }
    }

    public void addAuthorsListener() {
        authorsRef.addListenerForSingleValueEvent(authorsListener);
        tagsRef.addListenerForSingleValueEvent(tagsListener);
        trendingRef.addListenerForSingleValueEvent(trendingsListener);
    }

    public void removeAuthorsListenr() {
        authorsRef.removeEventListener(authorsListener);
    }

    public void removeQuotesListener() {
        quotesRef.removeEventListener(quotesChildListener);
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

    public void setDataThenUpdateView() {
        mainActivity.myProgressBar.showProgressBar();
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            DatabaseReference mUserRef = FirebaseDatabase.getInstance().getReference("users")
                    .child(fUser.getUid());
            mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User.currentUser = dataSnapshot.getValue(User.class);
                    User.currentUser.id = fUser.getUid();
                    setQuotes();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    if (mainActivity.myProgressBar != null) {
                        mainActivity.myProgressBar.hideProgressBar();
                    }
                }
            });
        } else {
            setQuotes();
        }
    }

    private void setQuotes() {
        addQuotesListener(true);
    }

    @NonNull
    public static void setQuoteView(Context context, View itemView, Quote quote) {
        TextView quoteText = (TextView) itemView.findViewById(R.id.quote_text);
        quoteText.setText(quote.text);
        TextView authorText = (TextView) itemView.findViewById(R.id.quote_author);
        authorText.setText(quote.author);
        CircleImageView authorImg = (CircleImageView) itemView.findViewById(R.id.author_image);
        Author author = QuotesUtil.authors.get(quote.authorId);
        if (author != null && author.image != null) {
           GlideUtil.setImageFromRef(context,author.image, authorImg);
        }
    }
}
