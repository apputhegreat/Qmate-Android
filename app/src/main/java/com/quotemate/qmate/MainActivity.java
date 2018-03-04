package com.quotemate.qmate;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.quotemate.qmate.CustomViews.MyVerticalViewPager;
import com.quotemate.qmate.Recievers.AlarmReceiver;
import com.quotemate.qmate.adapters.QuotesAdapter;
import com.quotemate.qmate.login.FBLoginFragment;
import com.quotemate.qmate.model.Author;
import com.quotemate.qmate.model.Quote;
import com.quotemate.qmate.model.User;
import com.quotemate.qmate.util.Analytics;
import com.quotemate.qmate.util.Constants;
import com.quotemate.qmate.util.CustomProgressBar;
import com.quotemate.qmate.util.FBUtil;
import com.quotemate.qmate.util.FilterQuotes;
import com.quotemate.qmate.util.IntroUtil;
import com.quotemate.qmate.util.QuotesUtil;
import com.quotemate.qmate.util.RandomSelector;
import com.quotemate.qmate.util.RemoteConfigController;
import com.quotemate.qmate.util.Transitions;
import com.quotemate.qmate.util.UpdateAppDialog;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

public class MainActivity extends AppCompatActivity {
    private QuotesUtil quotesUtil;
    private MyVerticalViewPager viewPager;
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
    private RelativeLayout mSpinTag;
    private RelativeLayout mSpinAuthor;
    public CustomProgressBar myProgressBar;
    public boolean isZoomView = false;
    private TextView spinTxt;
    private IntroUtil mIntroUtil;
    private float x1, x2;
    static final int MIN_DISTANCE = 350;
    private Handler zoomViewHandle;
    private Runnable zoomViewRuunable;
    private int currentListSize;
    private boolean isTouched;
    private RemoteConfigController mRemoteConfigController;
    private AlertDialog updateDialog;
    private int mspinCount = 0;
    private LinearLayout noQuotesLayout;
    private int totalRead = 0;
    private int lastPos = 0;
    private boolean isRefreshed = false;
    private AppCompatImageView moreBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mIntroUtil = new IntroUtil(this);
        totalRead = 0;
        mRemoteConfigController = new RemoteConfigController(this);
        checkVersionInfo();
        handleAuth();
        initProgressBar();
        zoomViewHandle = new Handler();
        toolBar = (Toolbar) findViewById(R.id.toolbar);
        moreBtn = (AppCompatImageView) findViewById(R.id.more_btn);
        moreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoProfilePage();
            }
        });
        searchBar = (TextView) findViewById(R.id.search_bar);
        searchBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });

        currentAutohrText = (TextView) findViewById(R.id.current_author_text);
        currentTagText = (TextView) findViewById(R.id.current_tag_text);

        //mQuoteOFtheDayLabel = (TextView) findViewById(R.id.quote_of_day_label);

        handleAdView();
        noQuotesLayout = (LinearLayout) findViewById(R.id.no_quotes_view);
        bottomLayout = (RelativeLayout) findViewById(R.id.bottom_layout);
        spin = (MaterialRippleLayout) findViewById(R.id.spin);
        spinTxt = (TextView) findViewById(R.id.spin_text);
        mSpinTag = (RelativeLayout) findViewById(R.id.tag_spin);
        mSpinTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
        mSpinAuthor = (RelativeLayout) findViewById(R.id.author_spin);
        mSpinAuthor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
        spin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSpinnerClick();
            }
        });
        setSpinTxtAppearance();

        setTitle("");

        initViewPager();
        //mIntroUtil.showSearchInfo(searchBar, 1500, "search quotes by author or any tag");
        //mIntroUtil.showSpinInfo(spin, 6000, "spin here to select random author and tags");
        zoomViewRuunable = new Runnable() {
            public void run() {
                showZooView(true);
            }
        };
        zoomViewHandle.postDelayed(zoomViewRuunable, 1500);
        setAlarmQuoteOftheDay();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isTouched) {
            isTouched = true;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                float deltaX = x2 - x1;
                if (x1 == 0) {
                    showZooView(false);
                    break;
                }
                x1 = 0;
                if (deltaX < -1 * (MIN_DISTANCE)) {
                    gotoProfilePage();
                } else if (Math.abs(deltaX) > MIN_DISTANCE) {
                    startSearchActivity();
                } else {
                    showZooView(false);
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setAlarmQuoteOftheDay() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getBoolean("firstTime", true)) {

            Intent alarmIntent = new Intent(this, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, 8);
            calendar.set(Calendar.MINUTE, 5);
            calendar.set(Calendar.SECOND, 1);

            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY, pendingIntent);

            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("firstTime", false);
            editor.commit();
            editor.apply();
        }
    }

    public void setSpinTxtAppearance() {
        if (User.currentUser != null) {
            spinTxt.setTextColor(ContextCompat.getColor(this, R.color.contentColor));
            spinTxt.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_spin_yellow));
            mSpinTag.setVisibility(View.VISIBLE);
            mSpinAuthor.setVisibility(View.VISIBLE);
        } else {
            spinTxt.setTextColor(ContextCompat.getColor(this, R.color.cardColor));
            spinTxt.setBackground(ContextCompat.getDrawable(this, R.drawable.bg_spin_gray));
            mSpinTag.setVisibility(View.GONE);
            mSpinAuthor.setVisibility(View.GONE);
        }
    }

    private void handleAdView() {
        long showAds = FirebaseRemoteConfig.getInstance().getLong(Constants.show_ads);
        if(showAds != 0) {
            mAdView = findViewById(R.id.ad_view);
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                    .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                    // Check the LogCat to get your test device dID
                    .addTestDevice("5BF4A0AFE64771B1CB3559898CD956F2")
                    .build();
            mAdView.loadAd(adRequest);
            mAdView.setVisibility(View.VISIBLE);
        }

    }

    private void initProgressBar() {
        myProgressBar = new CustomProgressBar(this, false);
        myProgressBar.setProgressBarMessage("Loading");
    }

    private void handleAuth() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = FBUtil.getAuthStateListener(this);
        mAuth.addAuthStateListener(mAuthListener);
    }

    private void gotoProfilePage() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            Analytics.sendProfileEvent();
            Intent intent = new Intent(this, ProfileActivity.class);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            startActivity(intent);
            Transitions.rightToLeft(this);
        } else {
            openLoginDialog();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if(!isFirstInstance && !isRefreshed) {
            viewPager.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        isFirstInstance = false;
        isRefreshed = false;
    }

    @Override
    public void onStop() {
        super.onStop();

    }

    private void openLoginDialog() {
        fbfragment = new FBLoginFragment();
        fbfragment.show(getSupportFragmentManager(), MainActivity.class.getSimpleName());
    }

    private void selectRandomAuthorAndTag() {
        //Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(this, R.anim.shuffle_anim);
        RandomSelector.setXRotaionAnimation(currentAutohrText, 1, 800);
        RandomSelector.setXRotaionAnimation(currentTagText, 1, 800);
        //currentAutohrText.startAnimation(RandomSelector.getAnimation(5));
        // currentTagText.startAnimation(RandomSelector.getAnimation(5));
        currentAuthor = RandomSelector.getRandomAuthor(QuotesUtil.authors);
        currentTag = RandomSelector.getRandomTag(QuotesUtil.tags);
        currentAutohrText.setText("Author");
        currentTagText.setText("Tag");
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (currentAuthor != null && currentTag != null) {
                            filterQuotesAndUpdateView(currentAuthor.id, currentTag, true);
                            currentAutohrText.setText(currentAuthor.name);
                            currentTagText.setText(currentTag);
                        }
                    }
                },
                700);
    }

    private void startSearchActivity() {
        Analytics.sendSearchEvent();
        Intent intent = new Intent(this, SearchActivity.class);
        intent.putExtra("authorId", currentAutohrText.getText().toString());
        intent.putExtra("tag", currentTagText.getText().toString());
        this.startActivityForResult(intent, 100);
        Transitions.leftToRight(this);
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
                if (currentAuthor != null) {
                    authorId = currentAuthor.id;
                }
            }
            String tag = data.getStringExtra("tag");
            if (tag != null) {
                currentTag = tag;
                currentTagText.setText(currentTag);
            } else {
                if (currentTag != null) {
                    tag = currentTag;
                }
            }
            filterQuotesAndUpdateView(authorId, tag, false);
            isRefreshed = true;
        }
    }

    private void filterQuotesAndUpdateView(String authorId, String tag, boolean isSpin) {
        ArrayList<Quote> filteredQuotes = FilterQuotes.getFilteredQuotes(authorId, tag);
        currentListSize = filteredQuotes.size();
        if (isSpin && filteredQuotes.isEmpty()) {
            filteredQuotes = FilterQuotes.getFilteredQuotes("-1", tag);
            if (filteredQuotes.isEmpty()) {
                currentTag = "All";
                currentTagText.setText("All");
                filteredQuotes = FilterQuotes.getFilteredQuotes(authorId, "All");
                if (filteredQuotes.isEmpty()) {
                    currentAuthor = QuotesUtil.authors.get("-1");
                    currentAutohrText.setText("All");
                    filteredQuotes = FilterQuotes.getFilteredQuotes("-1", "All");
                }
            } else {
                currentAuthor = QuotesUtil.authors.get("-1");
                currentAutohrText.setText("All");
            }
            updateView(filteredQuotes, null);
        } else {
            if (filteredQuotes.isEmpty()) {
                noQuotesLayout.setVisibility(View.VISIBLE);
                viewPager.setVisibility(View.GONE);
            } else {
                updateView(filteredQuotes, null);

            }
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    public void initViewPager() {
        viewPager = (MyVerticalViewPager) findViewById(R.id.vertical_viewpager);
        viewPager.setPageTransformer(false, new ZoomOutTransformer());
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (!isZoomView) {
                    zoomViewHandle.removeCallbacks(zoomViewRuunable);
                    zoomViewHandle.postDelayed(zoomViewRuunable, 1500);
                }
                if (position > lastPos) {
                    totalRead++;
                }
                if (position == currentListSize - 1) {
                    Toast.makeText(MainActivity.this, "Great! you have read all the quote in this category", Toast.LENGTH_SHORT).show();
                }
                lastPos = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //viewPager.setPageTransformer(true, new StackTransformer());
        quotesUtil = new QuotesUtil(this);
        if (isFirstInstance) {
            quotesUtil.setDataThenUpdateView();
        }
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        //viewPager.setPageTransformer(false, new DefaultTransformer());
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    public void showZooView(boolean isZoomView) {
        if (!isTouched) {
            return;
        }
        int visiblity = View.VISIBLE;
        if (isZoomView) {
            visiblity = View.INVISIBLE;
            bottomLayout.getRootView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toolBar.setVisibility(View.VISIBLE);
                    bottomLayout.setVisibility(View.VISIBLE);
                }
            });
        }
        bottomLayout.getRootView().setOnClickListener(null);
        toolBar.setVisibility(visiblity);
        bottomLayout.setVisibility(visiblity);
        this.isZoomView = isZoomView;
    }

    public void updateView(ArrayList<Quote> quotes, Quote quoteOftheDay) {
        noQuotesLayout.setVisibility(View.GONE);
        viewPager.setVisibility(View.VISIBLE);
        if (quoteOftheDay != null) {
            if (!quotes.isEmpty()) {
                quotes.remove(quoteOftheDay);
                Collections.shuffle(quotes);
                quotes.set(0, quoteOftheDay);
            }
        }
        adapter = new QuotesAdapter(MainActivity.this, quotes, false);
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (myProgressBar != null) {
            myProgressBar.hideProgressBar();
        }
    }

    @Override
    protected void onDestroy() {
        if (myProgressBar != null) {
            myProgressBar.destroyProgressBar();
        }
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        quotesUtil.removeQuotesListener();
        quotesUtil.removeAuthorsListenr();
        if (updateDialog != null) {
            updateDialog.dismiss();
            updateDialog = null;
        }
        super.onDestroy();
    }

    private void handleSpinnerClick() {
        Analytics.sendSpinEvent();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            if (isFirstInstance) {
                isFirstInstance = false;
                //mAdView.setVisibility(View.VISIBLE);
            }
            if (QuotesUtil.quotes == null || QuotesUtil.quotes.isEmpty()) {
                myProgressBar.showProgressBar();
                quotesUtil.addQuotesListener(true);
                return;
            }
            selectRandomAuthorAndTag();
        } else {
            openLoginDialog();
        }
    }

    @Override
    public void onBackPressed() {
        Analytics.cardsOnCloseApp(totalRead);
        moveTaskToBack(true);
    }

    private void checkVersionInfo() {
        String versionName = "";
        long versionCode = -1;
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionName = packageInfo.versionName;
            versionCode = (long) packageInfo.versionCode;
            long playStoreVersion = FirebaseRemoteConfig.getInstance().getLong(Constants.play_store_version_code);
            if (playStoreVersion != 0) {
                if (playStoreVersion > versionCode) {
                    handleUpdateApp();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void handleUpdateApp() {
        updateDialog = new UpdateAppDialog(this).dialog;
        updateDialog.setCancelable(false);
        updateDialog.show();
    }
}
