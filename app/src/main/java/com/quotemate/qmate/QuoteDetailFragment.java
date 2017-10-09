package com.quotemate.qmate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.util.BookMarkUtil;
import com.quotemate.qmate.util.GlideUtil;
import com.quotemate.qmate.util.LikeUtil;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.ShareView;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A fragment representing a single Quote detail screen.
 * in two-pane mode (on tablets) or a {@link QuoteDetailActivity}
 * on handsets.
 */
public class QuoteDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_QUOTE_ID = "QUOTE_ID";
    public static final String ARG_QUOTE_TEXT = "QUOTE_TEXT";
    public static final String ARG_QUOTE_AUTHOR = "QUOTE_AUTHOR";
    public static final String ARG_QUOTE_AUTHOR_IMAGE = "AUTHOR_IMAGE";
    private String mText;
    private String mQuoteAuthor;
    private String mQuoteAuthorImg;
    private Quote mSelectedQuote;
    private BookMarkUtil bookMarkUtil;
    private LikeUtil likeUtil;
    /**
     * The dummy content this fragment is presenting.
     */

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public QuoteDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the dummy content specified by the fragment
        // arguments. In a real-world scenario, use a Loader
        // to load content from a content provider.
        mText = getArguments().getString(ARG_QUOTE_TEXT);
        mQuoteAuthor = getArguments().getString(ARG_QUOTE_AUTHOR);
        mQuoteAuthorImg = getArguments().getString(ARG_QUOTE_AUTHOR_IMAGE);
        mSelectedQuote = getQuoteById(getArguments().getString(ARG_QUOTE_ID));
        bookMarkUtil = new BookMarkUtil((AppCompatActivity) getActivity());
        likeUtil = new LikeUtil((AppCompatActivity) getActivity());
    }

    private Quote getQuoteById(String id) {
        Quote quote = null;
        for (Quote item : QuotesUtil.quotes
                ) {
            if (Objects.equals(item.id, id)) {
                quote = item;
                break;
            }
        }
        return quote;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.quote_detail, container, false);
        TextView quoteTxt = (TextView) rootView.findViewById(R.id.quote_text);
        TextView authorTxt = (TextView) rootView.findViewById(R.id.quote_author);
        CircleImageView imageView = (CircleImageView) rootView.findViewById(R.id.author_image);
        if (mText != null) {
            quoteTxt.setText(mText);
        }
        if (mQuoteAuthor != null) {
            authorTxt.setText(mQuoteAuthor);
        }
        if (mQuoteAuthorImg != null) {
            GlideUtil.setImageFromRef(getContext(), mQuoteAuthorImg, imageView);
        }
        MaterialRippleLayout likeBtn = (MaterialRippleLayout) rootView.findViewById(R.id.like_quote_btn);
        final TextView likeCountBadge = (TextView) rootView.findViewById(R.id.badge_like);
        final AppCompatImageView likeImgView = (AppCompatImageView) rootView.findViewById(R.id.like_image);
        if (mSelectedQuote.likes != 0) {
            likeCountBadge.setVisibility(View.VISIBLE);
            likeCountBadge.setText(String.valueOf(mSelectedQuote.likes));
        } else {
            likeCountBadge.setVisibility(View.INVISIBLE);
        }
        MaterialRippleLayout bookMarkBtn = (MaterialRippleLayout) rootView.findViewById(R.id.book_mark_quote_btn);
        final AppCompatImageView bookMarkImgView = (AppCompatImageView) rootView.findViewById(R.id.book_mark_img);
        MaterialRippleLayout shareBtn = (MaterialRippleLayout) rootView.findViewById(R.id.share_quote_btn);
        if (mSelectedQuote.isBookMarked) {
            bookMarkImgView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.bookmarked));
        }
        if (mSelectedQuote.isLiked) {
            likeImgView.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.liked));
        }
        likeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeUtil.handleLike(mSelectedQuote, likeImgView, likeCountBadge);
            }
        });
        bookMarkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bookMarkUtil.handleBookMark(mSelectedQuote, bookMarkImgView, true);
            }
        });
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ShareView.handleShare(getContext(), mSelectedQuote);
            }
        });
        return rootView;
    }

}
