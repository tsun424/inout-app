package com.tsun.inout.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.widget.Toast;

import com.tsun.inout.R;
import com.tsun.inout.model.ActivityBean;
import com.tsun.inout.util.DepthPageTransformer;
import com.tsun.inout.util.ParallaxPageTransformer;

/**
 *	browse details of an activity
 ************************************************************************
 *	@Author Xiaoming Yang
 *	@Date	16-04-2016 11:43
 ************************************************************************
 *	update time			editor				updated information
 */

public class BrowseActivityPager extends FragmentActivity {

    public static final int NUM_PAGES = 2;

    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_slide);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setCurrentItem(1);
        mPager.setPageTransformer(true, new ParallaxPageTransformer());
        mPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When changing pages, reset the action bar actions since they are dependent
                // on which page is currently active. An alternative approach is to have each
                // fragment expose actions itself (rather than the activity exposing actions),
                // but for simplicity, the activity provides the actions in this sample.
                // finish();
                // invalidateOptionsMenu();
                if(position == 0){
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    /**
     * A simple pager adapter that represents several ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0){
                return new TransparentFragment();
            }else if(position == 1){
                ActivityDetailFragment activityDetailFragment = new ActivityDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putParcelable("activityBean", getIntent().getExtras().getParcelable("activityBean"));
                activityDetailFragment.setArguments(bundle);
                return activityDetailFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
