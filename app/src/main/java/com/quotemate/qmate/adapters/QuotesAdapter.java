package com.quotemate.qmate.adapters;

/**
 * Created by anji kinnara on 6/1/17.
 */

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.FBUtil;
import com.quotemate.qmate.util.QuotesUtil;
import com.squareup.picasso.Picasso;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import io.realm.Realm;

public class QuotesAdapter extends PagerAdapter{
    Context context;
    ArrayList<Quote> quotes = new ArrayList<>();
    LayoutInflater layoutInflater;
    boolean isQuoteOftheDay;


    public QuotesAdapter(Context context, ArrayList<Quote> quotes, boolean isQuoteOftheDay) {
        this.context = context;
        this.quotes = quotes;
        this.isQuoteOftheDay = isQuoteOftheDay;
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
        final View itemView = layoutInflater.inflate(R.layout.fragment_content, container, false);
        final Quote quote = quotes.get(position);

        TextView quoteText = (TextView) itemView.findViewById(R.id.quote_text);
        quoteText.setText(quote.text);
        TextView authorText = (TextView) itemView.findViewById(R.id.quote_author);
        authorText.setText(quote.author);
        CircleImageView authorImg = (CircleImageView) itemView.findViewById(R.id.author_image);
        Author author = QuotesUtil.authors.get(quote.authorId);
        if(author!=null && author.image!=null) {
            setImageFromRef(author.image,authorImg);
        }
        MaterialRippleLayout likeBtn = (MaterialRippleLayout) itemView.findViewById(R.id.like_quote_btn);
        final TextView likeCountBadge = (TextView) itemView.findViewById(R.id.badge_like);
        if(quote.likes!=0) {
            likeCountBadge.setVisibility(View.VISIBLE);
            likeCountBadge.setText(String.valueOf(quote.likes));
        } else {
            likeCountBadge.setVisibility(View.INVISIBLE);
        }
        MaterialRippleLayout bookMarkBtn = (MaterialRippleLayout) itemView.findViewById(R.id.book_mark_quote_btn);

        MaterialRippleLayout shareBtn = (MaterialRippleLayout) itemView.findViewById(R.id.share_quote_btn);
        if(isQuoteOftheDay){
            RelativeLayout actionsLayout =(RelativeLayout) itemView.findViewById(R.id.action_btns_quote);
            actionsLayout.setVisibility(View.GONE);
        }
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleLike(quote,likeCountBadge);
            }
        });
        bookMarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBookMark(quote);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShare(quote);
            }
        });
        container.addView(itemView);

        return itemView;
    }

    private void setImageFromRef(String imageURL, final ImageView view) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri mImageDownloadUrl = task.getResult();
                    Picasso.with(context).load(mImageDownloadUrl)
                            .placeholder(ContextCompat.getDrawable(context, R.drawable.gandhi)).into(view);
                }
            }
        });
    }

    private void handleShare(Quote quote) {
    }

    private void handleBookMark(Quote quote) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        try {
            realm.copyToRealm(quote);
            realm.commitTransaction();
        } catch (io.realm.exceptions.RealmPrimaryKeyConstraintException ex) {
            Toast.makeText(context,"Already added to book marks", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleLike(Quote quote, TextView badge) {
        FBUtil.updateLikes(quote.id);
        quote.likes = quote.likes+1;
        if(badge!=null) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(quote.likes));
        }
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }
}
