package com.galloped.tablayout;

import android.content.Context;

/**
 * Created by gallop 2019/7/4
 * Copyright (c) 2019 holike
 */
class Unit {
    static int dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    static int sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scale + 0.5f);
    }
}
