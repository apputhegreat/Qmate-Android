package com.quotemate.qmate;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.quotemate.qmate.adapters.BookMarksAdapter;
import com.quotemate.qmate.model.Quote;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_marks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
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
}
