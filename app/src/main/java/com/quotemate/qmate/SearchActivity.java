package com.quotemate.qmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.quotemate.qmate.adapters.KeyValueAdapter;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.selectAuthor.SearchFilter;
import com.quotemate.qmate.util.KeyBoardUtil;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.Transitions;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class SearchActivity extends AppCompatActivity {
    EditText searchBar;
    GridView authorGridView;
    GridView tagGridView;
    Spinner searchSpinner;
    AppCompatImageView cancelSearch;
    ArrayList<String> tags;
    List<String> categories = new ArrayList<>();
    String selectedCategory;
    private ListView mListView;
    private ArrayList<Pair<String,String>> authorPairList = new ArrayList<>();
    private ArrayList<Pair<String,String>> tagPairList = new ArrayList<>();
    private Pair<String, String> mSelectedKeyValueapair;
    private KeyValueAdapter mAdapter;
    private TextWatcher mtextChabgeListner;
    private ArrayList<Pair<String, String>> mList;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        tags = QuotesUtil.tags;
        searchBar = (EditText) findViewById(R.id.search_bar);
        cancelSearch = (AppCompatImageView) findViewById(R.id.close_search);
        searchSpinner = (Spinner) findViewById(R.id.search_spinner);
        categories.add("Author");
        categories.add("Tags");
        selectedCategory = "Author";
        searchBar.setHint("Search "+selectedCategory);
        searchBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(b) {
                    mListView.setVisibility(View.VISIBLE);
                    setAdapter();
                } else {
                    mListView.setVisibility(View.GONE);
                }
            }
        });
        cancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListView.setVisibility(View.GONE);
                searchBar.clearFocus();
                KeyBoardUtil.hideKeyboard(SearchActivity.this);
            }
        });
        LinkedHashMap<String,Author> hash = QuotesUtil.authors;
        for(String key: hash.keySet()) {
            authorPairList.add(new Pair<>(key, hash.get(key).name));
        }
        for (String str:QuotesUtil.tags
             ) {
            tagPairList.add(new Pair<>(str,str));
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(R.layout.spinner_view);

        // attaching data adapter to spinner
        searchSpinner.setAdapter(dataAdapter);
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCategory = categories.get(i);
                searchBar.setHint("Search " + selectedCategory);
                setAdapter();
                Log.d("", "onItemClick: ");
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        // search list
        mListView = (ListView) findViewById(R.id.search_list);
        handleSearch();
        // trending
        authorGridView = (GridView) findViewById(R.id.trending_authors_grid);
        tagGridView = (GridView) findViewById(R.id.trending_tags_grid);
        authorGridView.setAdapter(new KeyValueAdapter(this, new ArrayList<Pair<String, String>>(authorPairList.subList(0,5)) ));
        tagGridView.setAdapter(new  KeyValueAdapter(this ,new ArrayList<Pair<String,String>>(tagPairList.subList(0,5))));
        authorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sendSelectedAuthor(authorPairList.get(i).first);
            }
        });
        tagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sendSelectedTag(tags.get(i));
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX < 0) {
                    handleBackPressed();
                } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }
    private void sendSelectedTag(String tag) {
        Intent intent = new Intent();
        intent.putExtra("tag", tag);
        setResult(RESULT_OK, intent);
        handleBackPressed();
    }

    private void sendSelectedAuthor(String authorId) {
        Intent intent = new Intent();
        intent.putExtra("authorId", authorId);
        setResult(RESULT_OK, intent);
        handleBackPressed();
    }

    private void handleSearch() {
        mListView = (ListView) findViewById(R.id.search_list);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSelectedKeyValueapair = mAdapter.getItem(i);
                returnResult();
            }
        });

        mtextChabgeListner = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                mAdapter.getFilter().filter(editable.toString());
            }
        };
        searchBar.addTextChangedListener(mtextChabgeListner);
    }

    private void setAdapter() {
        mList = new ArrayList<>();
        switch (selectedCategory) {
            case "Author":
                mList = new ArrayList<>(authorPairList);
                break;
            case "Tags":
                mList =  new ArrayList<>(tagPairList);
                break;
        }
        if(mAdapter==null) {
            mAdapter = new KeyValueAdapter(SearchActivity.this, mList);
            mListView.setAdapter(mAdapter);
        } else {
            mAdapter.clear();
            mAdapter.addAll(mList);
            SearchFilter filter = (SearchFilter)mAdapter.getFilter();
            filter.mOriginalList.clear();
            filter.mOriginalList.addAll(mList);
            mAdapter.notifyDataSetChanged();
        }
    }

    private void returnResult() {
        switch (selectedCategory) {
            case "Author":
                sendSelectedAuthor(mSelectedKeyValueapair.first);
                break;
            case "Tags":
                sendSelectedTag(mSelectedKeyValueapair.first);
                break;
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handleBackPressed();
    }

    private void handleBackPressed() {
        finish();
        Transitions.rightToLeft(this);
    }
}
