package com.quotemate.qmate;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.facebook.AccessToken;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.quotemate.qmate.Interfaces.IUpdateView;
import com.quotemate.qmate.adapters.QuotesAdapter;
import com.quotemate.qmate.login.FBLoginFragment;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.RealmString;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.util.Constants;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.RandomSelector;
import com.quotemate.qmate.util.RealmUtil;

import java.util.ArrayList;
import java.util.Objects;

import io.realm.Realm;
import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class MainActivity extends AppCompatActivity implements IUpdateView {

    private QuotesUtil quotesUtil;
    private VerticalViewPager viewPager;
    private TextView searchBar;
    private QuotesAdapter adapter;
    private TextView currentAutohrText;
    private TextView currentTagText;
    private MaterialRippleLayout spin;
    private String currentTag;
    private Author currentAuthor;
    private RelativeLayout bottomLayout;
    private DatabaseReference mUserRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FBLoginFragment fbfragment;
    private Toolbar toolBar;
    private PublisherAdView mAdView;
    public static boolean isFirstInstance = true;
    private TextView mQuoteOFtheDayLabel;
    private RelativeLayout mSpinTag;
    private RelativeLayout mSpinAuthor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(getApplicationContext());
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolBar);
        searchBar = (TextView) findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
        currentAutohrText = (TextView) findViewById(R.id.current_author_text);
        currentTagText = (TextView) findViewById(R.id.current_tag_text);
        mQuoteOFtheDayLabel = (TextView) findViewById(R.id.quote_of_day_label) ;
        mAdView =(PublisherAdView) findViewById(R.id.ad_view);
        PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                // Check the LogCat to get your test device dID
                .addTestDevice("5BF4A0AFE64771B1CB3559898CD956F2")
                .build();
        mAdView.loadAd(adRequest);
        mAdView.setVisibility(View.GONE);
        spin = (MaterialRippleLayout) findViewById(R.id.spin);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        mAuth = FirebaseAuth.getInstance();
        mSpinTag = (RelativeLayout) findViewById(R.id.tag_spin);
        mSpinAuthor = (RelativeLayout) findViewById(R.id.author_spin);
        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
                    if(isFirstInstance) {
                        isFirstInstance = false;
                        mQuoteOFtheDayLabel.setVisibility(View.GONE);
                        mSpinTag.setVisibility(View.VISIBLE);
                        mSpinAuthor.setVisibility(View.VISIBLE);
                        mAdView.setVisibility(View.VISIBLE);
                    }
                    if(QuotesUtil.quotes==null || QuotesUtil.quotes.isEmpty()) {
                        toolBar.setVisibility(View.VISIBLE);
                        quotesUtil.addQuotesListener();
                    }
                    selectRandomAuthorAndTag();
                } else {
                    openLoginDialog();
                }
            }
        });
        setTitle("");
        initViewPager();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String fromScreen = intent.getStringExtra(Constants.FROM_SCREEN);
        if(Objects.equals(fromScreen, Constants.BOOK_MARKS_SCREEN)) {
            updateView(RealmUtil.getBookMarks());
        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_profile:
                gotoProfilePage();
                return true;
            default: return  super.onOptionsItemSelected(item);
        }
    }

    private void gotoProfilePage() {
        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
        } else {
            openLoginDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(getAuthListener());
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    private void openLoginDialog() {
        fbfragment  = new FBLoginFragment();
        fbfragment.show(getSupportFragmentManager(),MainActivity.class.getSimpleName());
    }

    private FirebaseAuth.AuthStateListener getAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        mUserRef = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
                        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                mUserRef.child("deviceOS").setValue("Android");
                                if (!dataSnapshot.exists()) {
                                        UserInfo profile = user.getProviderData().get(0);
                                        User user = new User();
                                        mUserRef.child("name").setValue(profile.getDisplayName());
                                        user.name = profile.getDisplayName();
                                         user.readbleId = profile.getDisplayName();
                                        if (profile.getEmail() != null) {
                                            mUserRef.child("email").setValue(profile.getEmail());
                                            user.email = profile.getEmail();
                                        }
                                        mUserRef.child("readableId").setValue(profile.getDisplayName());
                                        if (profile.getPhotoUrl() != null) {
                                            mUserRef.child("photoURL").setValue(profile.getPhotoUrl().toString());
                                            user.photoURL = profile.getPhotoUrl().toString();
                                        }
                                        User.currentUser = user;
                                } else {
                                    User.currentUser = dataSnapshot.getValue(User.class);
                                }
                                finishDialog();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                finishDialog();
                            }
                        });
                    }
                }
            }
        };
        return mAuthListener;
    }

    private void showProgress(boolean b) {
    }

    private void finishDialog() {
        Log.d("fbDialog", "finishDialog: ");
        if(fbfragment!=null)
        fbfragment.dismiss();
    }

    private void selectRandomAuthorAndTag() {
        //Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.shuffle_anim);
        RandomSelector.setXRotaionAnimation(currentAutohrText,1,800);
        RandomSelector.setXRotaionAnimation(currentTagText,1,800);
        //currentAutohrText.startAnimation(RandomSelector.getAnimation(5));
       // currentTagText.startAnimation(RandomSelector.getAnimation(5));
        currentAuthor = RandomSelector.getRandomAuthor(QuotesUtil.authors);
        currentTag = RandomSelector.getRandomTag(QuotesUtil.tags);
        currentAutohrText.setText("Author");
        currentTagText.setText("Tag");
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if(currentAuthor!=null && currentTag!=null) {
                            filterQuotesAndUpdateView(currentAuthor.id, currentTag, true);
                            currentAutohrText.setText(currentAuthor.name);
                            currentTagText.setText(currentTag);
                        }
                    }
                },
                1800);
    }

    private void startSearchActivity() {
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("authorId", currentAutohrText.getText().toString());
        intent.putExtra("tag", currentTagText.getText().toString());
        this.startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100 && resultCode == RESULT_OK) {
            String authorId = data.getStringExtra("authorId");
            if (authorId != null) {
                currentAuthor = QuotesUtil.authors.get(authorId);
                currentAutohrText.setText(currentAuthor.name);
            } else {
                currentAutohrText.setText("All");
            }
            String tag = data.getStringExtra("tag");
            if(tag != null) {
                currentTag = tag;
                currentTagText.setText(currentTag);
            } else {
                currentTagText.setText("All");
            }
            filterQuotesAndUpdateView(authorId, tag, false);
        }
    }

    private void filterQuotesAndUpdateView(String authorId, String tag, boolean isSpin) {
        ArrayList<Quote> filteredQuotes = new ArrayList<>();
        if (authorId == null || Objects.equals(authorId, "-1")) {
            if (tag != null && !Objects.equals(tag,"All")) {
                for (Quote quote : QuotesUtil.quotes
                        ) {
                    for (RealmString mood : quote.tags
                            ) {
                        if (Objects.equals(mood.getValue().trim(), tag.trim())) {
                            filteredQuotes.add(quote);
                            break;
                        }
                    }
                }
            } else {
                filteredQuotes = QuotesUtil.quotes;
            }
        } else {
            for (Quote quote : QuotesUtil.quotes
                    ) {
                if (Objects.equals(quote.authorId, authorId)) {
                    if (tag != null && !Objects.equals(tag,"All")) {
                        for (RealmString mood : quote.tags
                                ) {
                            if (Objects.equals(mood.getValue().trim(), tag.trim())) {
                                filteredQuotes.add(quote);
                                break;
                            }
                        }
                    } else {
                        filteredQuotes.add(quote);
                    }
                }
            }
        }
        if(isSpin && filteredQuotes.isEmpty()) {
            selectRandomAuthorAndTag();
        } else {
            updateView(filteredQuotes);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViewPager() {
        viewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
        viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        quotesUtil = new QuotesUtil(this);
        quotesUtil.addAuthorsListener();
        if (isFirstInstance) {
            showQuoteoFtheDay();
        }
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        //viewPager.setPageTransformer(false, new DefaultTransformer());
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    public void updateView(ArrayList<Quote> quotes) {
        Log.d("updateView", "updateView: " + quotes);
        if (isFirstInstance) {
            showQuoteoFtheDay();
        } else {
            mQuoteOFtheDayLabel = (TextView) findViewById(R.id.quote_of_day_label);
            mQuoteOFtheDayLabel.setVisibility(View.GONE);
            adapter = new QuotesAdapter(MainActivity.this, quotes, false);
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    private void showQuoteoFtheDay() {
        toolBar.setVisibility(View.GONE);
        FirebaseDatabase.getInstance().getReference().child("quoteOftheDay").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<Quote> quotes = new ArrayList<>();
                if(dataSnapshot.exists()) {
                    mQuoteOFtheDayLabel.setVisibility(View.VISIBLE);
                    Quote quote = QuotesUtil.getQuote(dataSnapshot);
                    quotes.add(quote);
                    adapter = new QuotesAdapter(MainActivity.this, quotes, true);
                    viewPager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        quotesUtil.removeQuotesListener();
        quotesUtil.removeAuthorsListenr();
        super.onDestroy();
    }
}
