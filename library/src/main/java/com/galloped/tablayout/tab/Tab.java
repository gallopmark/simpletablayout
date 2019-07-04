package com.galloped.tablayout.tab;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

public interface Tab {
    @Nullable
    CharSequence getTabText();

    @Nullable
    Drawable getSelectedDrawable();

    @Nullable
    Drawable getUnSelectDrawable();
}
