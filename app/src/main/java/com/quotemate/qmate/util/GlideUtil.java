package com.quotemate.qmate.util;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by anji kinnara on 10/7/17.
 */

public class GlideUtil {
    public static void setImageFromRef(Context context, String imageURL, final ImageView view) {
        StorageReference ref = FirebaseStorage.getInstance().getReferenceFromUrl(imageURL);
        Glide.with(context)
                .using(new FirebaseImageLoader())
                .load(ref)
                .into(view);
    }
}
