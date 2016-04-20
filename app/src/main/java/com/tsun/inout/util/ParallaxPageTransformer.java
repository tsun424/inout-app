package com.tsun.inout.util;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Ben on 17/04/2016.
 */
public class ParallaxPageTransformer implements ViewPager.PageTransformer {

    public void transformPage(View view, float position) {

        int pageWidth = view.getWidth();


        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(1);

        }else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page
            view.setAlpha(1);
            view.setTranslationX(0);
        }else if (position <= 1) { // [-1,1]

            view.setAlpha(1);
            view.setTranslationX(0);

            //view.setAlpha(1 - position);

            // Counteract the default slide transition
            // view.setTranslationX(pageWidth * position);
            // view.setTranslationX(-position * (pageWidth / 2)); //Half the normal speed

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(1);
        }


    }
}