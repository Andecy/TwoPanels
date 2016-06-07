package com.desarrollodroide.twopanels;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.widget.LinearLayout;

public abstract class TwoPanelsBaseActivity extends FragmentActivity implements LeftFragment.OnSliderLeftListener, RightFragment.OnSliderRightListener {

    protected Fragment mRightFragment = new Fragment();
    protected Fragment mLeftFragment = new Fragment();
    private TwoPaneLayout mRootPanel = null;
    private Boolean mIsLeftShowing = true;
    private Boolean mIsRightShowing = true;

    // Default weight of fragments
    protected float mLeftWeight = 0.30f;
    protected float mRightWeight = 0.70f;

    // Default orientation
    private int mFragmentsOrientation = LinearLayout.HORIZONTAL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setPanelsView();
        mRootPanel = (TwoPaneLayout) findViewById(R.id.root);
        mRootPanel.setOrientation(mFragmentsOrientation);

        if (mFragmentsOrientation == LinearLayout.VERTICAL) {
            mRootPanel.setHeightsFromWeight(mLeftWeight, mRightWeight);
        } else {
            mRootPanel.setWidthsFromWeight(mLeftWeight, mRightWeight);
        }
    }

    protected abstract void setPanelsView();

    protected void switchSliderVisibility() {
        if (mIsLeftShowing && mIsRightShowing) {
            mRootPanel.changeSliderVisibility();
        }
    }

    protected void setSliderVisibility(Boolean visibility) {
        mRootPanel.setSliderVisibility(visibility);
    }

    protected void switchButtonsSliderVisibilility(){
        mRootPanel.switchButtonsSliderVisibilility();
    }

    protected void setBaseOrientation(int orientation) {
        if (orientation == LinearLayout.HORIZONTAL) {
            if (mFragmentsOrientation == LinearLayout.VERTICAL) {
                changeOrientation(LinearLayout.HORIZONTAL);
            }
        } else if (orientation == LinearLayout.VERTICAL) {
            if (mFragmentsOrientation == LinearLayout.HORIZONTAL) {
                changeOrientation(LinearLayout.VERTICAL);
            }
        }
    }

    public void setSlidersDrawables(int verticalDrawable, int horizontalDrawable) {
        mRootPanel.setSlidersDrawables(verticalDrawable, horizontalDrawable);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        updateButtonSliderOrientation();
        mRootPanel.updateWidgetsOnOrientationChange(mIsLeftShowing, mIsRightShowing, true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // Saving state for rotations
        outState.putBoolean("isLeftShowing", mIsLeftShowing);
        outState.putBoolean("isRightShowing", mIsRightShowing);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore value of members from saved state
        mIsLeftShowing = savedInstanceState.getBoolean("isLeftShowing");
        mIsRightShowing = savedInstanceState.getBoolean("isRightShowing");
    }

    @Override
    public void slideFragmentsToLeft() {
        if (mIsLeftShowing && mIsRightShowing) {
            mRootPanel.hideLeft();
            mIsLeftShowing = false;
            mIsRightShowing = true;
        } else if (mIsLeftShowing) {
            mRootPanel.showRight();
            mIsRightShowing = true;
            mIsLeftShowing = true;
        }
    }

    @Override
    public void slideFragmentsToRight() {
        if (mIsRightShowing && mIsLeftShowing) {
            mRootPanel.hideRight();
            mIsRightShowing = false;
            mIsLeftShowing = true;
        } else if (mIsRightShowing) {
            mRootPanel.showLeft();
            mIsRightShowing = true;
            mIsLeftShowing = true;
        }
    }

    public void hideLeft() {
        if (mIsLeftShowing) {
            mRootPanel.hideLeftNoAnimate();
            mIsLeftShowing = false;
            mIsRightShowing = true;
        }
    }

    public void hideRight() {
        if (mIsRightShowing) {
            mRootPanel.hideRightNoAnimate();
            mIsLeftShowing = true;
            mIsRightShowing = false;
        }
    }

    public void showTwoFragments() {
        mRootPanel.showTwoPanels();
        mIsLeftShowing = true;
        mIsRightShowing = true;
    }

    // Called when finish onCreate()
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        mRightFragment = getSupportFragmentManager().findFragmentById(R.id.right);
        mLeftFragment = getSupportFragmentManager().findFragmentById(R.id.left);
    }

    private void changeOrientation(int orientation) {
        mFragmentsOrientation = orientation;
        mRootPanel.setOrientation(orientation);
        mRootPanel.setParamsValues();
        updateButtonSliderOrientation();
        mRootPanel.updateWidgetsOnOrientationChange(mIsLeftShowing, mIsRightShowing, false);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public int getScreenHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.heightPixels;
    }

    public int getScreenWidth() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        return displaymetrics.widthPixels;
    }

    public int getActionBarHeight() {
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            return TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        } else {
            return 0;
        }
    }

    public void updateButtonSliderOrientation() {
        // Manage errors if fragments not extends from RightFragment and
        // LeftFragment
        try {
            if (getFragmentsOrientation() == LinearLayout.VERTICAL) {
                ((LeftFragment) mLeftFragment).updateSlideLeftButtonOrientationVertical();
                ((RightFragment) mRightFragment).updateSlideRightButtonOrientationVertical();
            } else {
                ((LeftFragment) mLeftFragment).updateSlideLeftButtonOrientationHorizontal();
                ((RightFragment) mRightFragment).updateSlideRightButtonOrientationHorizontal();
            }
        } catch (Exception e) {
            Log.e("updateButtonSlider", e.toString());
        }
    }

    public Fragment getmRightFragment() {
        return mRightFragment;
    }

    public Fragment getmLeftFragment() {
        return mLeftFragment;
    }

    public float getmLeftWeight() {
        return mLeftWeight;
    }

    public float getmRightWeight() {
        return mRightWeight;
    }

    public int getFragmentsOrientation() {
        return mFragmentsOrientation;
    }

    protected void setSliderSize(int size) {
        mRootPanel.setmSliderBarConst(size);
    }

}
