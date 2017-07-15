package com.quotemate.qmate.login;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.quotemate.qmate.MainActivity;
import com.quotemate.qmate.Qtoniq;
import com.quotemate.qmate.R;
import com.quotemate.qmate.util.Constants;
import com.quotemate.qmate.util.CustomProgressBar;
import com.quotemate.qmate.util.KeyBoardUtil;

public class FBLoginFragment extends DialogFragment {
    private LoginButton loginButton;

    private AppCompatActivity mActivity;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    private CustomProgressBar mProgressBar;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton gloginBtn;

    private FacebookCallback<LoginResult> mCallback = new FacebookCallback<LoginResult>() {
        private ProfileTracker mProfileTracker;

        @Override
        public void onSuccess(LoginResult loginResult) {
            if (Profile.getCurrentProfile() == null) {
                mProfileTracker = new ProfileTracker() {
                    @Override
                    protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
                        mProfileTracker.stopTracking();
                    }
                };
            }
            String accessToken = loginResult.getAccessToken().getToken();
            Log.d("AccessToken", "onSuccess: " + accessToken);
            handleFacebookAccessToken(loginResult.getAccessToken());
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {
            Log.d("faceBook error", "onError: " + error);
        }
    };

    public FBLoginFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mCallbackManager = CallbackManager.Factory.create();
        mActivity = (MainActivity) getActivity();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .enableAutoManage(mActivity, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d("GoogleApiClient", "onConnectionFailed: ");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mProgressBar = new CustomProgressBar(mActivity,false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_fblogin, container, false);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER;
        params.windowAnimations = R.style.DialogAnimation;
        getDialog().getWindow().setAttributes(params);
        this.setStyle(DialogFragment.STYLE_NORMAL,R.style.CustomDialog);
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstance) {
        super.onViewCreated(view, savedInstance);
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.setReadPermissions("user_status");
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(mCallbackManager, mCallback);
        Button fbloginCustomBtn = (Button) view.findViewById(R.id.fb_login_custom_btn);
        Button gloginCustomButton =(Button) view.findViewById(R.id.g_login_custom_btn);
        gloginCustomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });
        fbloginCustomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Qtoniq.isConnectedToInternet(getActivity())) {
                    loginButton.performClick();
                } else {
                    Toast.makeText(getActivity(),"No Internet connection",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signInWithGoogle() {
        if(Qtoniq.isConnectedToInternet(getActivity())) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, Constants.RC_SIGN_IN);
        } else {
            Toast.makeText(getActivity(),"No Internet connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleGSignInResult(result);
        }
        else if (mCallbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    private void handleGSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = result.getSignInAccount();
            firebaseAuthWithGoogle(account);
        } else {
            // Google Sign In failed, update UI appropriately
            // ...
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("firebaseAuthWithGoogle", "firebaseAuthWithGoogle:" + account.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        signInWithCredential(credential);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(mProgressBar!=null) {
            mProgressBar.hideProgressBar();
        }
    }

    private void handleFacebookAccessToken(AccessToken accessToken) {
        Log.d("token", "handleFacebookAccessToken:" + accessToken);
        // Exchange credentials with facebook
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        signInWithCredential(credential);
    }

    private void signInWithCredential(AuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        showProgress(false);
                        Log.d("Facebooke sign in", "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w("Facebooke sign in", "signInWithCredential", task.getException());
                            Toast.makeText(getActivity(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        FBLoginFragment.this.dismiss();
                    }
                });
    }

    private void showProgress(final boolean show) {
        if(mProgressBar!=null) {
            if(show) {
                mProgressBar.showProgressBar();
            } else {
                mProgressBar.hideProgressBar();
            }
        }

        KeyBoardUtil.hideKeyboard(mActivity);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }
}