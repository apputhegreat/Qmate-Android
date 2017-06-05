package com.quotemate.qmate;

/**
 * Created by anji kinnara on 6/1/17.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quotemate.qmate.model.Quote;

public class ContentFragment extends Fragment {
    public ContentFragment() {
    }

    public static  Fragment newInstance(Quote quote) {
        Bundle args = new Bundle();
        args.putString("text", quote.text);
        args.putString("author", quote.author);
        ContentFragment fragment = new ContentFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_content, container, false);
        initToolbar(view);
        return view;
    }

    private void initToolbar(View view) {
        TextView textView = (TextView) view.findViewById(R.id.quote_text);
        textView.setText(getQuoteText());
        TextView author = (TextView) view.findViewById(R.id.cquote_author);
        author.setText(getAuthor());
    }

    public String getQuoteText() {
        return getArguments().getString("text");
    }

    public String getAuthor() {
        return getArguments().getString("author");
    }
}
