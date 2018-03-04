package com.quotemate.qmate.adapters;

/**
 * Created by anji kinnara on 6/1/17.
 */

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.quotemate.qmate.MainActivity;
import com.quotemate.qmate.R;
import com.quotemate.qmate.login.FBLoginFragment;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.util.Analytics;
import com.quotemate.qmate.util.BookMarkUtil;
import com.quotemate.qmate.util.IntroUtil;
import com.quotemate.qmate.util.LikeUtil;
import com.quotemate.qmate.util.Permissions;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.ShareView;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class QuotesAdapter extends PagerAdapter {
    private final LikeUtil likeUtil;
    private final IntroUtil mIntroUtil;
    MainActivity context;
    ArrayList<Quote> quotes = new ArrayList<>();
    LayoutInflater layoutInflater;
    boolean isQuoteOftheDay;
    private File imagePath;
    private BookMarkUtil bookMarkUtil;


    public QuotesAdapter(MainActivity context, ArrayList<Quote> quotes, boolean isQuoteOftheDay) {
        this.context = context;
        this.quotes = quotes;
        this.isQuoteOftheDay = isQuoteOftheDay;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        bookMarkUtil = new BookMarkUtil(context);
        likeUtil = new LikeUtil(context);
        mIntroUtil = new IntroUtil(context);
    }

    @Override
    public int getCount() {
        return quotes.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = layoutInflater.inflate(R.layout.fragment_content, container, false);
        final Quote quote = quotes.get(position);
        TextView authorText = itemView.findViewById(R.id.quote_author);
        QuotesUtil.setQuoteView(context, itemView, quote);
        MaterialRippleLayout likeBtn = itemView.findViewById(R.id.like_quote_btn);
        final TextView likeCountBadge = itemView.findViewById(R.id.badge_like);
        final AppCompatImageView likeImgView = itemView.findViewById(R.id.like_image);
        if (quote.likes != 0) {
            likeCountBadge.setVisibility(View.VISIBLE);
            likeCountBadge.setText(String.valueOf(quote.likes));
        } else {
            likeCountBadge.setVisibility(View.INVISIBLE);
        }
        MaterialRippleLayout bookMarkBtn = itemView.findViewById(R.id.book_mark_quote_btn);
        final AppCompatImageView bookMarkImgView = itemView.findViewById(R.id.book_mark_img);
        MaterialRippleLayout shareBtn = itemView.findViewById(R.id.share_quote_btn);
        if (isQuoteOftheDay) {
            RelativeLayout actionsLayout = itemView.findViewById(R.id.action_btns_quote);
            actionsLayout.setVisibility(View.GONE);
        } else {
            mIntroUtil.showSwipeInfo(itemView, 100, "Swipe up to view more quotes");
        }
        if (quote.isBookMarked) {
            bookMarkImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmarked));
        }
        if (quote.isLiked) {
            likeImgView.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.liked));
        }
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.currentUser == null) {
                    openLoginDialog();
                    return;
                }
                likeUtil.handleLike(quote, likeImgView, likeCountBadge);
            }
        });
        bookMarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (User.currentUser == null) {
                    openLoginDialog();
                    return;
                }
                bookMarkUtil.handleBookMark(quote, bookMarkImgView, true);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareView.handleShare(context, quote);
            }
        });
        container.addView(itemView);
        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }

    private void openLoginDialog() {
        FBLoginFragment fbfragment = new FBLoginFragment();
        fbfragment.show(context.getSupportFragmentManager(), MainActivity.class.getSimpleName());
    }
}
