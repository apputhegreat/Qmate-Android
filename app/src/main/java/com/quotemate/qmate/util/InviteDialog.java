package com.quotemate.qmate.util;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.quotemate.qmate.R;

/**
 * Created by anji kinnara on 7/1/17.
 */

public class InviteDialog extends Dialog {

    public InviteDialog(final AppCompatActivity context) {
        super(context, R.style.FullScreenDialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setCancelable(false);
        setContentView(R.layout.dialog_share_or_invite);
        Button inviteBtn = (Button) findViewById(R.id.invite_btn);

        inviteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.invitation_message));
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, "Send to a friend"));
            }
        });

        AppCompatImageView closeBtn = (AppCompatImageView) findViewById(R.id.close_share);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InviteDialog.this.dismiss();
            }
        });
    }
}
