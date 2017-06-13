package com.quotemate.qmate.adapters;

/**
 * Created by anji kinnara on 6/1/17.
 */

import android.content.Context;
import android.view.View;

import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.QuotesUtil;
import com.squareup.picasso.Picasso;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuotesAdapter extends PagerAdapter{
    Context context;
    ArrayList<Quote> quotes = new ArrayList<>();
    LayoutInflater layoutInflater;


    public QuotesAdapter(Context context, ArrayList<Quote> quotes) {
        this.context = context;
        this.quotes = quotes;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return quotes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.fragment_content, container, false);
        Quote quote = quotes.get(position);

        TextView quoteText = (TextView) itemView.findViewById(R.id.quote_text);
        quoteText.setText(quote.text);
        TextView authorText = (TextView) itemView.findViewById(R.id.quote_author);
        authorText.setText(quote.author);
        CircleImageView authorImg = (CircleImageView) itemView.findViewById(R.id.author_image);
        Author author = QuotesUtil.authors.get(quote.authorId);
        if(author!=null) {
            Picasso.with(context).load(author.image).into(authorImg);
        }
        container.addView(itemView);

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
