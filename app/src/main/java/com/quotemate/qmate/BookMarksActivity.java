package com.quotemate.qmate;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.quotemate.qmate.adapters.BookMarksAdapter;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.Analytics;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.RecyclerViewDivider;
import com.quotemate.qmate.util.Transitions;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class BookMarksActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Quote> quotesList = new ArrayList<>();
    private BookMarksAdapter mAdapter;
    private TextView title;
    private AppCompatImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);
        Analytics.sendBookmarksEvent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        backButton = (AppCompatImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackPressed();
            }
        });
        title = (TextView) findViewById(R.id.book_marks_title);
        recyclerView = (RecyclerView) findViewById(R.id.book_marks_recycler_view);
        mAdapter = new BookMarksAdapter(this,quotesList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
        recyclerView.addItemDecoration(new RecyclerViewDivider(this));
        prepareBookMarksData();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void prepareBookMarksData() {
        for (Quote quote : QuotesUtil.quotes
                ) {
            if (quote.isBookMarked) {
                quotesList.add(quote);
            }
        }
        setBookMarksTitle(quotesList.size());
        mAdapter.notifyDataSetChanged();
    }

    public void setBookMarksTitle(int size) {
        title.setText("Bookmarks" + "(" + quotesList.size() + ")");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            handleBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void handleBackPressed() {
        finish();
        Transitions.leftToRight(this);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }
}
