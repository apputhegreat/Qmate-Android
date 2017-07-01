package com.quotemate.qmate;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.quotemate.qmate.util.Constants;
import com.quotemate.qmate.util.InviteDialog;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookMarksBtn;
    Button logoutBtn;
    Button inviteButton;
    FirebaseAuth firebaseAuth;
    private InviteDialog mInviteDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        firebaseAuth = FirebaseAuth.getInstance();
        bookMarksBtn = (Button) findViewById(R.id.book_marks_btn);
        bookMarksBtn.setOnClickListener(this);
        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
        inviteButton = (Button) findViewById(R.id.invite_btn);
        inviteButton.setOnClickListener(this);
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
        mInviteDialog = new InviteDialog(this);
        mInviteDialog.show();
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
    private void logoutUser() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            FirebaseAuth.getInstance().signOut();
            if (AccessToken.getCurrentAccessToken() != null) {
                LoginManager.getInstance().logOut();
            }
            finish();
        }
    }

    private void openBookMarks() {
        Intent intent = new Intent(this, BookMarksActivity.class);
        intent.putExtra(Constants.FROM_SCREEN,Constants.BOOK_MARKS_SCREEN);
        finish();
        startActivity(intent);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
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
}
