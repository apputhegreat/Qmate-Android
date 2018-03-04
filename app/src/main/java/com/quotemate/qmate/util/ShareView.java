package com.quotemate.qmate.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.quotemate.qmate.R;
import com.quotemate.qmate.model.Quote;
import com.vipul.hp_hp.library.Layout_to_Image;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by anji kinnara on 7/1/17.
 */

public class ShareView {
    public static Bitmap takeScreenshot(View view) {
        view.setDrawingCacheEnabled(true);
        return view.getDrawingCache();
    }

    public static File saveBitmap(Bitmap bitmap) {
        File imagePath = new File(Environment.getExternalStorageDirectory() + "/screenshot.png");
        FileOutputStream fos;
        try {
            fos = new FileOutputStream(imagePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            Log.e("GREC", e.getMessage(), e);
        } catch (IOException e) {
            Log.e("GREC", e.getMessage(), e);
        }
        return imagePath;
    }

    public static void shareIt(Context context, File imagePath, String shareBody) {
        Uri uri = FileProvider.getUriForFile(context,
                context.getString(R.string.file_provider_authority),
                imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Qtoniq");
        if(shareBody!=null) {
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        }
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }

    public static Bitmap convertLayout(View view)
    {
        view.setDrawingCacheEnabled(true);

        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());

        view.buildDrawingCache(true);

        Bitmap bMap = Bitmap.createBitmap(view.getDrawingCache());

        return bMap;
    }


    public static void handleShare(Context context, Quote quote) {
        Analytics.sendShareEvent();
        Permissions.requestAppPermissions(context);
        boolean resultExternal = Permissions.checkExternalStoragePermission(context);
        if (resultExternal) {
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View shareview = inflater.inflate(R.layout.share_quote_layout, null);
            QuotesUtil.setQuoteView(context,shareview, quote);

            Bitmap bitmap = ShareView.convertLayout(shareview);
            File imagePath = ShareView.saveBitmap(bitmap);
            String shareBody = quote.text + "\n-" + quote.author + "\n" + "https://play.google.com/store/apps/details?id=com.quotemate.qmate&hl=en";
            ShareView.shareIt(context, imagePath, shareBody);
        }
    }

}
