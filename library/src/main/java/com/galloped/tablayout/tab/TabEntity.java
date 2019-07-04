package com.galloped.tablayout.tab;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public class TabEntity implements Tab {
    private String mTabText;
    private Drawable mSelectedDrawable;
    private Drawable mUnSelectDrawable;

    public TabEntity(@Nullable String tabText) {
        this(tabText, null, null);
    }

    public TabEntity(@Nullable String tabText, @Nullable Drawable selectedDrawable, @Nullable Drawable unSelectDrawable) {
        this.mTabText = tabText;
        this.mSelectedDrawable = selectedDrawable;
        this.mUnSelectDrawable = unSelectDrawable;
    }

    @Nullable
    @Override
    public CharSequence getTabText() {
        return mTabText;
    }

    @Nullable
    @Override
    public Drawable getSelectedDrawable() {
        return mSelectedDrawable;
    }

    @Nullable
    @Override
    public Drawable getUnSelectDrawable() {
        return mUnSelectDrawable;
    }
}
