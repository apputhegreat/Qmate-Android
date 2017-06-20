package com.quotemate.qmate.selectAuthor;

import android.support.v4.util.Pair;
import android.widget.Filter;

import com.quotemate.qmate.adapters.KeyValueAdapter;

import java.util.ArrayList;

/**
 * Created by anji kinnara on 6/16/17.
 */

public class SearchFilter extends Filter{
    public ArrayList<Pair<String,String>> mOriginalList;
    private ArrayList<Pair<String,String>> mFilteredList = new ArrayList<>();
    private KeyValueAdapter mAdapter;

    public SearchFilter(KeyValueAdapter adapter, ArrayList<Pair<String,String>> list) {
        mOriginalList = new ArrayList<>(list);
        mAdapter = adapter;
    }

    @Override
    protected Filter.FilterResults performFiltering(CharSequence charSequence) {
        mFilteredList.clear();
        final Filter.FilterResults results = new Filter.FilterResults();
        if (charSequence == null || charSequence.length() == 0) {
            mFilteredList.addAll(mOriginalList);
        } else {
            String filterPattern = charSequence.toString().toLowerCase().trim();
            for (Pair<String,String> item : mOriginalList) {
                if (item.second.toLowerCase().contains(filterPattern)) {
                    mFilteredList.add(item);
                }
            }
        }
        results.values = mFilteredList;
        results.count = mFilteredList.size();
        return results;
    }

    @Override
    protected void publishResults(CharSequence charSequence, Filter.FilterResults filterResults) {
        if (filterResults.count != 0) {
            mAdapter.clear();
            mAdapter.addAll((ArrayList) filterResults.values);
            mAdapter.notifyDataSetChanged();
        }
    }
}
