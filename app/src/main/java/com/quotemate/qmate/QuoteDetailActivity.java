package com.quotemate.qmate;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.Permissions;
import com.quotemate.qmate.util.ShareView;
import com.quotemate.qmate.util.Transitions;

import java.io.File;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class QuoteDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShare(findViewById(R.id.quote_detail_container));
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
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

    private void handleShare(View view) {
        boolean resultExternal = Permissions.checkExternalStoragePermission(this);
        if(resultExternal) {
            Bitmap bitmap = ShareView.takeScreenshot(view);
            File imagePath = ShareView.saveBitmap(bitmap);
            String shareBody = getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_TEXT)
                    + "\n-" + getIntent().getStringExtra(QuoteDetailFragment.ARG_QUOTE_AUTHOR);
            ShareView.shareIt(this,imagePath,shareBody);
        }
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
