package com.quotemate.qmate.util;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by anji kinnara on 8/15/17.
 */

public class UpdateAppDialog {
    AppCompatActivity activity;
    public AlertDialog dialog;

    public UpdateAppDialog(AppCompatActivity activity) {
        this.activity = activity;
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setPositiveButton("Update", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                final String appPackageName = UpdateAppDialog.this.activity.getPackageName(); // getPackageName() from Context or Activity object
                try {
                    UpdateAppDialog.this.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    UpdateAppDialog.this.activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
        builder.setMessage("There is a new version available on the play store. Please update the app");
        dialog = builder.create();
    }
}
