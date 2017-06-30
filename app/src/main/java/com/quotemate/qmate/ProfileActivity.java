package com.quotemate.qmate;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.AccessToken;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.quotemate.qmate.util.Constants;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {
    Button bookMarksBtn;
    Button logoutBtn;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        bookMarksBtn = (Button) findViewById(R.id.book_marks_btn);
        bookMarksBtn.setOnClickListener(this);
        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);
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
        }
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
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(Constants.FROM_SCREEN,Constants.BOOK_MARKS_SCREEN);
        finish();
        startActivity(intent);
    }
}
