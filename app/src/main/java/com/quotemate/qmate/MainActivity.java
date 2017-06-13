package com.quotemate.qmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.quotemate.qmate.Interfaces.IUpdateView;
import com.quotemate.qmate.adapters.QuotesAdapter;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.QuotesUtil;

import java.util.ArrayList;
import java.util.Objects;

import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity implements IUpdateView {

    private QuotesUtil quotesUtil;
    private VerticalViewPager viewPager;
    private TextView searchBar;
    private QuotesAdapter adapter;
    private TextView currentAutohrText;
    private TextView currentTagText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        searchBar = (TextView) findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
        currentAutohrText = (TextView) findViewById(R.id.current_author_text);
        currentTagText = (TextView) findViewById(R.id.current_tag_text);
        setTitle("");
        initViewPager();
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("authorId", currentAutohrText.getText().toString());
        intent.putExtra("tag", currentTagText.getText().toString());
        this.startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String authorId = data.getStringExtra("authorId");
            if (authorId != null) {
                currentAutohrText.setText(QuotesUtil.authors.get(authorId).name);
            } else {
                currentAutohrText.setText("All");
            }
            String tag = data.getStringExtra("tag");
            if(tag != null) {
                currentTagText.setText(tag);
            } else {
                currentTagText.setText("All");
            }
            filterQuotesAndUpdateView(authorId, tag);
        }
    }

    private void filterQuotesAndUpdateView(String authorId, String tag) {
        ArrayList<Quote> filteredQuotes = new ArrayList<>();
        if (authorId == null) {
            if (tag != null) {
                for (Quote quote : QuotesUtil.quotes
                        ) {
                    String[] moods = quote.tags.split(",");
                    for (String mood : moods
                            ) {
                        if (Objects.equals(mood.trim(), tag.trim())) {
                            filteredQuotes.add(quote);
                            break;
                        }
                    }
                }
            }
        } else {
            for (Quote quote : QuotesUtil.quotes
                    ) {
                if (Objects.equals(quote.authorId, authorId)) {
                    if (tag != null) {
                        String[] moods = quote.tags.split(",");
                        for (String mood : moods
                                ) {
                            if (Objects.equals(mood.trim(), tag.trim())) {
                                filteredQuotes.add(quote);
                                break;
                            }
                        }
                    } else {
                        filteredQuotes.add(quote);
                    }
                }
            }
        }
        updateView(filteredQuotes);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViewPager() {
        viewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
        viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        quotesUtil = new QuotesUtil(this);
        quotesUtil.addQuotesListener();
        quotesUtil.addAuthorsListener();
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        //viewPager.setPageTransformer(false, new DefaultTransformer());
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void updateView(ArrayList<Quote> quotes) {
        Log.d("updateView", "updateView: " + quotes);
        adapter = new QuotesAdapter(MainActivity.this, quotes);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        quotesUtil.removeQuotesListener();
        quotesUtil.removeAuthorsListenr();
        super.onDestroy();
    }
}
