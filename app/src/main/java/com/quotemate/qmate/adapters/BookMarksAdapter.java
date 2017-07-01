package com.quotemate.qmate.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.quotemate.qmate.QuoteDetailActivity;
import com.quotemate.qmate.QuoteDetailFragment;
import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.Transitions;

import java.util.ArrayList;

/**
 * Created by anji kinnara on 7/1/17.
 */

public class BookMarksAdapter extends RecyclerView.Adapter<BookMarksAdapter.MyViewHolder> {
    ArrayList<Quote> quotesList = new ArrayList<>();
    AppCompatActivity mActivity ;
    public BookMarksAdapter(AppCompatActivity activity,ArrayList<Quote> quotes) {
        this.mActivity = activity;
        this.quotesList = quotes;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
       View itemView = LayoutInflater.from(parent.getContext())
               .inflate(R.layout.bookmarks_row, parent,false);
        return  new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        final Quote quote = quotesList.get(position);
        holder.quoteText.setText(quote.text);
        holder.authorText.setText("-"+quote.author);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, QuoteDetailActivity.class);
                    intent.putExtra(QuoteDetailFragment.ARG_QUOTE_TEXT, quote.text);
                    intent.putExtra(QuoteDetailFragment.ARG_QUOTE_AUTHOR, quote.author);
                    intent.putExtra(QuoteDetailFragment.ARG_QUOTE_AUTHOR_IMAGE, QuotesUtil.authors.get(quote.authorId).image);
                    context.startActivity(intent);
                    Transitions.rightToLeft(mActivity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.quotesList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView quoteText, authorText;
        View mView;
        public MyViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            quoteText = (TextView) itemView.findViewById(R.id.book_mark_quote_txt);
            authorText = (TextView) itemView.findViewById(R.id.book_mark_quote_author);
        }
    }
}
