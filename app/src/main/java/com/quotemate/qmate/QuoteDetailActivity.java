package com.quotemate.qmate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.quotemate.qmate.util.Analytics;
import com.quotemate.qmate.util.Transitions;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuoteDetailActivity extends AppCompatActivity {

    private AppCompatImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);
        Analytics.sendBookmarksDeatailEvent();
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        backButton = (AppCompatImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackPressed();
            }
        });

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction
            Bundle arguments = new Bundle();
            arguments.putString(QuoteDetailFragment.ARG_QUOTE_ID,
                    getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_ID));
            arguments.putString(QuoteDetailFragment.ARG_QUOTE_TEXT,
                    getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_TEXT));
            arguments.putString(QuoteDetailFragment.ARG_QUOTE_AUTHOR,
                    getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_AUTHOR));
            arguments.putString(QuoteDetailFragment.ARG_QUOTE_AUTHOR_IMAGE,
                    getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_AUTHOR_IMAGE));
            QuoteDetailFragment fragment = new QuoteDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.quote_detail_container, fragment)
                    .commit();
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
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
