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
        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        final View itemView = layoutInflater.inflate(R.layout.fragment_content, container, false);
        final Quote quote = quotes.get(position);
        TextView authorText = (TextView) itemView.findViewById(R.id.quote_author);
        setQuoteView(itemView, quote);
        MaterialRippleLayout likeBtn = (MaterialRippleLayout) itemView.findViewById(R.id.like_quote_btn);
        final TextView likeCountBadge = (TextView) itemView.findViewById(R.id.badge_like);
        final AppCompatImageView likeImgView = (AppCompatImageView) itemView.findViewById(R.id.like_image);
        if (quote.likes != 0) {
            likeCountBadge.setVisibility(View.VISIBLE);
            likeCountBadge.setText(String.valueOf(quote.likes));
        } else {
            likeCountBadge.setVisibility(View.INVISIBLE);
        }
        MaterialRippleLayout bookMarkBtn = (MaterialRippleLayout) itemView.findViewById(R.id.book_mark_quote_btn);
        final AppCompatImageView bookMarkImgView = (AppCompatImageView) itemView.findViewById(R.id.book_mark_img);
        MaterialRippleLayout shareBtn = (MaterialRippleLayout) itemView.findViewById(R.id.share_quote_btn);
        if (isQuoteOftheDay) {
            RelativeLayout actionsLayout = (RelativeLayout) itemView.findViewById(R.id.action_btns_quote);
            actionsLayout.setVisibility(View.GONE);
        } else {
            mIntroUtil.showSwipeInfo(authorText,500,"Swipe up to view more quotes");
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
                bookMarkUtil.handleBookMark(quote, bookMarkImgView,true);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleShare(quote, itemView);
            }
        });
        container.addView(itemView);
        return itemView;
    }

    @NonNull
    private void setQuoteView(View itemView, Quote quote) {
        TextView quoteText = (TextView) itemView.findViewById(R.id.quote_text);
        quoteText.setText(quote.text);
        TextView authorText = (TextView) itemView.findViewById(R.id.quote_author);
        authorText.setText(quote.author);
        CircleImageView authorImg = (CircleImageView) itemView.findViewById(R.id.author_image);
        Author author = QuotesUtil.authors.get(quote.authorId);
        if (author != null && author.image != null) {
            setImageFromRef(author.image, authorImg);
        }
    }

    private void setImageFromRef(String imageURL, final ImageView view) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(view);
    }

    private void handleShare(Quote quote, View view) {
        Analytics.sendShareEvent();
        boolean resultExternal = Permissions.checkExternalStoragePermission(context);
        if (resultExternal) {
            final LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View shareview = inflater.inflate(R.layout.share_quote_layout, null);
            setQuoteView(shareview, quote);
            Layout_to_Image layout_to_image=new Layout_to_Image(context,shareview);

            //Bitmap bitmap = getViewBitmap(shareview);
            Bitmap bitmap = layout_to_image.convert_layout();
            File imagePath = ShareView.saveBitmap(bitmap);
            String shareBody = quote.text + "\n-" + quote.author;
            ShareView.shareIt(context, imagePath, shareBody);
        }
    }

    private Bitmap getViewBitmap(View v)
    {
        v.measure(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        int width= v.getMeasuredHeight();
        int height= v.getMeasuredWidth();
        Bitmap b = Bitmap.createBitmap(width ,height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, width, height);
        v.draw(c);
        return b;
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
