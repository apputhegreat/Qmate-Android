package com.quotemate.qmate;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
    public static final String ARG_QUOTE_TEXT = "QUOTE_TEXT";
    public static final String ARG_QUOTE_AUTHOR = "QUOTE_AUTHOR";
    public static final String ARG_QUOTE_AUTHOR_IMAGE = "AUTHOR_IMAGE";
    private String mText;
    private String mQuoteAuthor;
    private String mQuoteAuthorImg;
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.quote_detail, container, false);
        TextView quoteTxt = (TextView) rootView.findViewById(R.id.quote_text);
        TextView authorTxt = (TextView) rootView.findViewById(R.id.quote_author);
        CircleImageView imageView = (CircleImageView) rootView.findViewById(R.id.author_image);
        if(mText!=null) {
            quoteTxt.setText(mText);
        }
        if(mQuoteAuthor!=null) {
            authorTxt.setText(mQuoteAuthor);
        }
        if(mQuoteAuthorImg!=null) {
           setImageFromRef(mQuoteAuthorImg, imageView);
        }
        return rootView;
    }

    private void setImageFromRef(String imageURL, final ImageView view) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        Glide.with(getContext())
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(view);
    }
}
