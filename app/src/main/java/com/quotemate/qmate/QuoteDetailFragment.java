package com.quotemate.qmate;

import android.app.Activity;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A fragment representing a single Quote detail screen.
 * This fragment is either contained in a {@link QuoteListActivity}
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
        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    final Uri mImageDownloadUrl = task.getResult();
                    Picasso.with(getContext()).load(mImageDownloadUrl)
                            .placeholder(ContextCompat.getDrawable(getContext(), R.drawable.gandhi)).into(view);
                }
            }
        });
    }
}
