package com.quotemate.qmate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.util.Analytics;
import com.quotemate.qmate.util.Constants;
import com.quotemate.qmate.util.InviteDialog;
import com.quotemate.qmate.util.Transitions;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookMarksBtn;
    Button logoutBtn;
    Button inviteButton;
    CircleImageView profileImage;
    TextView userNameTxt;
    FirebaseAuth firebaseAuth;
    private InviteDialog mInviteDialog;
    private float x1, x2;
    static final int MIN_DISTANCE = 150;
    private AppCompatImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        backButton = (AppCompatImageView) findViewById(R.id.back_btn);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleBackPressed();
            }
        });
        firebaseAuth = FirebaseAuth.getInstance();

        bookMarksBtn = (Button) findViewById(R.id.book_marks_btn);
        bookMarksBtn.setOnClickListener(this);

        profileImage = (CircleImageView) findViewById(R.id.user_img);
        userNameTxt = (TextView) findViewById(R.id.user_name);

        if(User.currentUser!=null) {
            Glide.with(this)
                    .load(User.currentUser.photoURL)
                    .into(profileImage);
            userNameTxt.setText(User.currentUser.name);
        }

        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        inviteButton = (Button) findViewById(R.id.invite_btn);
        inviteButton.setOnClickListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (deltaX < 0) {
                   break;
                } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                    handleBackPressed();
                } else {
                    // consider as something else - a screen tap for example
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.book_marks_btn:
                openBookMarks();
                break;
            case R.id.logout_btn:
                logoutUser();
                break;
            case R.id.invite_btn:
                gotoSharePage();
                break;
        }
    }

    void gotoSharePage() {
        Analytics.sendInviteEvent();
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "A good quote on a bad day can rewrite the journey. Download the 'Qtoniq' app \n" + "https://play.google.com/store/apps/details?id=com.quotemate.qmate&hl=en");
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.invitation_message));
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Send to a friend"));
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void logoutUser() {
        Analytics.sendLogoutEvent();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseAuth.getInstance().signOut();
            User.currentUser = null;
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
            handleBackPressed();
        }
    }

    private void openBookMarks() {
        Analytics.sendBookmarksEvent();
        Intent intent = new Intent(this, BookMarksActivity.class);
        intent.putExtra(Constants.FROM_SCREEN,Constants.BOOK_MARKS_SCREEN);
        startActivity(intent);
        Transitions.rightToLeft(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            handleBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mInviteDialog!=null) {
            mInviteDialog.dismiss();
            mInviteDialog=null;
        }
    }

    private void handleBackPressed() {
        finish();
        Transitions.leftToRight(this);
    }

    @Override
    public void onBackPressed() {
        handleBackPressed();
    }
}
