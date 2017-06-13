package com.quotemate.qmate.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.quotemate.qmate.R;

import java.util.ArrayList;

/**
 * Created by anji kinnara on 6/10/17.
 */

public class KeyValueAdapter extends ArrayAdapter<Pair<String,String>> {
    ArrayList<Pair<String,String>> pairs;
    private static class ViewHolder {
        TextView displayText;
    }
    public KeyValueAdapter(@NonNull Context context, ArrayList<Pair<String ,String>> pairs) {
        super(context, 0);
        this.pairs = pairs;
    }

    @Override
    public int getCount() {
        return pairs.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        ViewHolder holder; // to reference the child views for later actions

        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.rectangle_string_row, null);

            // cache view fields into the holder
            holder = new ViewHolder();
            holder.displayText = (TextView) v.findViewById(R.id.text1);

            // associate the holder with the view for later lookup
            v.setTag(holder);
        }
        else {
            // view already exists, get the holder instance from the view
            holder = (ViewHolder)v.getTag();
        }
        Pair<String,String> pair = pairs.get(position);
        holder.displayText.setText(pair.second);
        return v;
    }
}
