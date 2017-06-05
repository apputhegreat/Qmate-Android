package com.quotemate.qmate;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.quotemate.qmate.model.Quote;

import me.kaelaela.verticalviewpager.VerticalViewPager;
import me.kaelaela.verticalviewpager.transforms.ZoomOutTransformer;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("");
        initViewPager();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void initViewPager() {
        VerticalViewPager viewPager = (VerticalViewPager) findViewById(R.id.vertical_viewpager);
        viewPager.setPageTransformer(false, new ZoomOutTransformer());
        //viewPager.setPageTransformer(true, new StackTransformer());
        ContentFragmentAdapter.Holder holder = new ContentFragmentAdapter.Holder(getSupportFragmentManager());
        for (Quote quote:Quote.SampleQuotes
             ) {
            holder.add(ContentFragment.newInstance(quote));
        }
        viewPager.setAdapter(holder.set());
        //If you setting other scroll mode, the scrolled fade is shown from either side of display.
        //viewPager.setPageTransformer(false, new DefaultTransformer());
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }
}
