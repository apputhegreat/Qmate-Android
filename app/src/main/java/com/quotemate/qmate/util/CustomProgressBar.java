package com.quotemate.qmate.util;

/**
 * Created by anji kinnara on 6/30/17.
 */

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.quotemate.qmate.R;


/**
 * Created by anji kinnara on 11/29/16.
 */

public class CustomProgressBar {
    private AlertDialog mProgressBar;
    private TextView mProgressText;
    private AppCompatActivity mActivity;
    private boolean mIsCancelable = false;

    public CustomProgressBar(AppCompatActivity activity, boolean isCancelable) {
        mActivity = activity;
        mProgressBar = getProgressDialog();
        mIsCancelable = isCancelable;
    }

    public void showProgressBar() {
        mProgressBar.show();
    }

    public void hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.dismiss();
        }
    }

    public void setProgressBarMessage(String message) {
        mProgressText.setText(message);
    }

    public AlertDialog getProgressDialog() {
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress, null);
        mProgressText = (TextView) dialogView.findViewById(R.id.progress_message);
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        builder.setView(dialogView);
        final AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.dialog_animation);
        dialog.getWindow().getAttributes().width = 300;
        dialog.setCancelable(mIsCancelable);
        return dialog;
    }

    public void destroyProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.dismiss();
            mProgressBar = null;
        }
    }
}
