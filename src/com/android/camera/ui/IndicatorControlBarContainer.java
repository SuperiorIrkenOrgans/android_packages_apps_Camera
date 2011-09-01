/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.camera.ui;

import com.android.camera.CameraPreference.OnPreferenceChangedListener;
import com.android.camera.PreferenceGroup;
import com.android.camera.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * The IndicatorControlBarContainer is a IndicatorControl containing
 * IndicatorControlBar, SecondIndicatorControlBar and ZoomControlBar for Phone UI.
 */
public class IndicatorControlBarContainer extends IndicatorControlContainer
        implements OnIndicatorEventListener {
    private static final String TAG = "IndicatorControlBarContainer";

    private Animation mFadeIn, mFadeOut;
    private IndicatorControlBar mIndicatorControlBar;
    private ZoomControlBar mZoomControlBar;
    private ZoomIndexBar mZoomIndexBar;
    private SecondLevelIndicatorControlBar mSecondLevelIndicatorControlBar;

    public IndicatorControlBarContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void initialize(Context context, PreferenceGroup group,
            String flashSetting, boolean isZoomSupported,
            String[] secondLevelKeys, String[] secondLevelOtherSettingKeys) {
        mZoomControlBar = (ZoomControlBar)
                findViewById(R.id.zoom_control);
        mZoomControlBar.setOnIndicatorEventListener(this);
        mZoomIndexBar = (ZoomIndexBar) findViewById(R.id.zoom_index_bar);
        mZoomControlBar.setOnZoomIndexChangeListener(mZoomIndexBar);

        // We need to show/hide the zoom slider icon accordingly.
        // From UI spec, we have camera_flash setting on the first level.
        mIndicatorControlBar = (IndicatorControlBar)
                findViewById(R.id.indicator_bar);
        mIndicatorControlBar.initialize(context, group, flashSetting,
                isZoomSupported);
        mIndicatorControlBar.setOnIndicatorEventListener(this);

        mSecondLevelIndicatorControlBar = (SecondLevelIndicatorControlBar)
                findViewById(R.id.second_level_indicator_bar);
        mSecondLevelIndicatorControlBar.initialize(context, group, secondLevelKeys,
                secondLevelOtherSettingKeys);
        mSecondLevelIndicatorControlBar.setOnIndicatorEventListener(this);

        mFadeIn = AnimationUtils.loadAnimation(
                context, R.anim.grow_fade_in_from_bottom);
        mFadeOut = AnimationUtils.loadAnimation(
                context, R.anim.shrink_fade_out_from_top);
    }

    public void setDegree(int degree) {
        mIndicatorControlBar.setDegree(degree);
        mSecondLevelIndicatorControlBar.setDegree(degree);
        mZoomControlBar.setDegree(degree);
        mZoomIndexBar.setDegree(degree);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mIndicatorControlBar.dispatchTouchEvent(event);
        } else if (mSecondLevelIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mSecondLevelIndicatorControlBar.dispatchTouchEvent(event);
        } else if (mZoomControlBar.getVisibility() == View.VISIBLE) {
            return mZoomControlBar.dispatchTouchEvent(event);
        }
        return true;
    }

    public void onIndicatorEvent(int event) {
        switch (event) {
            case OnIndicatorEventListener.EVENT_ENTER_SECOND_LEVEL_INDICATOR_BAR:
                mIndicatorControlBar.setVisibility(View.GONE);
                mSecondLevelIndicatorControlBar.startAnimation(mFadeIn);
                mSecondLevelIndicatorControlBar.setVisibility(View.VISIBLE);
                break;

            case OnIndicatorEventListener.EVENT_LEAVE_SECOND_LEVEL_INDICATOR_BAR:
                mSecondLevelIndicatorControlBar.startAnimation(mFadeOut);
                mSecondLevelIndicatorControlBar.setVisibility(View.GONE);
                mIndicatorControlBar.setVisibility(View.VISIBLE);
                break;

            case OnIndicatorEventListener.EVENT_ENTER_ZOOM_CONTROL:
                mIndicatorControlBar.setVisibility(View.GONE);
                mZoomControlBar.setVisibility(View.VISIBLE);
                mZoomIndexBar.setVisibility(View.VISIBLE);
                mZoomControlBar.startZoomControl();
                break;

            case OnIndicatorEventListener.EVENT_LEAVE_ZOOM_CONTROL:
                mZoomControlBar.setVisibility(View.GONE);
                mZoomIndexBar.setVisibility(View.GONE);
                mIndicatorControlBar.setVisibility(View.VISIBLE);
                break;
        }
    }

    public void reloadPreferences() {
        mIndicatorControlBar.reloadPreferences();
        mSecondLevelIndicatorControlBar.reloadPreferences();
    }

    public void setListener(OnPreferenceChangedListener listener) {
        mIndicatorControlBar.setListener(listener);
        mSecondLevelIndicatorControlBar.setListener(listener);
    }

    @Override
    public View getActiveSettingPopup() {
        if (mIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mIndicatorControlBar.getActiveSettingPopup();
        } else if (mSecondLevelIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mSecondLevelIndicatorControlBar.getActiveSettingPopup();
        }
        return null;
    }

    public boolean dismissSettingPopup() {
        if (mIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mIndicatorControlBar.dismissSettingPopup();
        } else if (mSecondLevelIndicatorControlBar.getVisibility() == View.VISIBLE) {
            return mSecondLevelIndicatorControlBar.dismissSettingPopup();
        }
        return false;
    }

    @Override
    public void overrideSettings(final String ... keyvalues) {
        if (mSecondLevelIndicatorControlBar.getVisibility() == View.VISIBLE) {
            mSecondLevelIndicatorControlBar.overrideSettings(keyvalues);
        }
    }
}