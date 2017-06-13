package com.quotemate.qmate;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;

import com.quotemate.qmate.adapters.KeyValueAdapter;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.util.QuotesUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SearchActivity extends AppCompatActivity {
    EditText searchBar;
    GridView authorGridView;
    GridView tagGridView;
    ArrayList<String> tags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        tags = QuotesUtil.tags;
        searchBar = (EditText) findViewById(R.id.search_bar);
        authorGridView = (GridView) findViewById(R.id.trending_authors_grid);
        tagGridView = (GridView) findViewById(R.id.trending_tags_grid);
        final String author = getIntent().getStringExtra("author");
        final String tag = getIntent().getStringExtra("tag");
        String editString = "";
        if(author!=null) {
            editString = editString+author;
        }
        if(tag!=null) {
            editString  = editString + "," + tag;
        }
        searchBar.setText(editString);
        final ArrayList<Pair<String,String>> pairs = new ArrayList<>();
        LinkedHashMap<String,Author> hash = QuotesUtil.authors;
        for(String key: hash.keySet()) {
            pairs.add(new Pair<>(key, hash.get(key).name));
        }
        Log.d("pairs", "onCreate: " +pairs.get(0));
        authorGridView.setAdapter(new KeyValueAdapter(this,pairs));
        tagGridView.setAdapter(new ArrayAdapter<>(this, R.layout.rectangle_string_row, tags));
        authorGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent();
                Log.d("onAuthorClick", "onItemClick: " + pairs.get(i).first);
                intent.putExtra("authorId", pairs.get(i).first);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        tagGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent= new Intent();
                Log.d("on Tag click", "onItemClick: " + tags.get(i));
                intent.putExtra("tag", tags.get(i));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
