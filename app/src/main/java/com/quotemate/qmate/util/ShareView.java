package com.quotemate.qmate.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;

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
        Uri uri = Uri.fromFile(imagePath);
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image/*");
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Qtoniq");
        if(shareBody!=null) {
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        }
        sharingIntent.putExtra(Intent.EXTRA_STREAM, uri);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
}
