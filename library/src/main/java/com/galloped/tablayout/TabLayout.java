package com.galloped.tablayout;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.FloatRange;
import androidx.annotation.Nullable;

import com.galloped.tablayout.tab.Tab;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gallop 2019/7/4
 * Copyright (c) 2019 holike
 * tabLayout
 */
public class TabLayout extends LinearLayout {
    private int mTabWidth;
    private boolean isTabSpaceEqual;
    private int mIconWidth;  //icon 长
    private int mIconHeight; //icon 宽
    private int mSelectedTextColor; //选中tab 文字颜色
    private int mUnSelectTextColor; //未选中tab 文字颜色
    private int mIconGravity;   //icon 位置（left、top、right、bottom）
    private float mDrawablePadding;   //icon 距离文字边距
    private float mTextSize;  // 文字大小

    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_ITALIC = 1;
    public static final int STYLE_BOLD = 2;
    public static final int STYLE_BOLD_ITALIC = 3;

    private int mTextStyle = -1; //文字Typeface
    private Typeface mTextTypeface, mSelectedTextTypeface, mUnSelectTextTypeface;
    private int mSelectedTextStyle = -1; //选中文字Typeface
    private int mUnSelectTextStyle = -1; //未选中文字Typeface
    private float mSelectedTextSize;  //选中时文字大小
    private float mUnSelectTextSize; //未选中时文字大小
    private boolean isAnimEnabled; //是否需要动画
    private int mAnimDuration; //动画持续时间
    private float mAlphaStart; //动画开始时透明度
    private boolean isReselectedAnimEnabled;  //重复选中tab是否需要动画
    private boolean isAnimRecycle = false;  //切换到另一个tab是否取消上次动画
    private ValueAnimator mValueAnimator;

    /*drawable gravity*/
    public static final int ICON_LEFT = 0;
    public static final int ICON_TOP = 1;
    public static final int ICON_RIGHT = 2;
    public static final int ICON_BOTTOM = 3;

    private boolean isTabSetup = false;
    private List<Tab> mTabList = new ArrayList<>();
    private int mCurrentTab;  //当前选中tab位置
    private OnTabSelectListener mOnTabSelectListener;

    public TabLayout(Context context) {
        this(context, null);
    }

    public TabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setClipChildren(false);  //tab超出父元素边界也显示
        setOrientation(HORIZONTAL);
        setupAttrs(context, attrs);
    }

    private void setupAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TabLayout);
        final int tabWidth = ta.getDimensionPixelSize(R.styleable.TabLayout_tl_tabWidth, 0);
        final boolean isTabSpaceEqual = ta.getBoolean(R.styleable.TabLayout_tl_tab_space_equal, true);
        setTabWidth(tabWidth);
        setTabSpaceEqual(isTabSpaceEqual);
        mIconWidth = ta.getDimensionPixelSize(R.styleable.TabLayout_tl_iconWidth, 0);
        mIconHeight = ta.getDimensionPixelSize(R.styleable.TabLayout_tl_iconHeight, 0);
        mSelectedTextColor = ta.getColor(R.styleable.TabLayout_tl_selectedTextColor, Color.parseColor("#222222"));
        mUnSelectTextColor = ta.getColor(R.styleable.TabLayout_tl_unSelectTextColor, Color.parseColor("#666666"));
        final int iconGravity = ta.getInt(R.styleable.TabLayout_tl_iconGravity, ICON_TOP);
        setIconGravity(iconGravity);
        final float drawablePadding = ta.getDimension(R.styleable.TabLayout_tl_drawablePadding, Unit.dp2px(context, 4f));
        setDrawablePadding(drawablePadding);
        final int textStyle = ta.getInt(R.styleable.TabLayout_tl_textStyle, -1);
        final int selectedTextStyle = ta.getInt(R.styleable.TabLayout_tl_selectedTextStyle, -1);
        final int unnSelectTextStyle = ta.getInt(R.styleable.TabLayout_tl_unSelectTextStyle, -1);
        setSelectTextStyle(selectedTextStyle);
        setUnSelectTextStyle(unnSelectTextStyle);
        setTextStyle(textStyle);
        final float textSize = ta.getDimension(R.styleable.TabLayout_tl_textSize, Unit.dp2px(context, 13f));
        final float selectedTextSize = ta.getDimension(R.styleable.TabLayout_tl_selectedTextSize, 0);
        final float unSelectTextSize = ta.getDimension(R.styleable.TabLayout_tl_unSelectTextSize, 0);
        setSelectedTextSize(selectedTextSize);
        setUnSelectTextSize(unSelectTextSize);
        setTextSize(textSize);
        final boolean isAnimEnabled = ta.getBoolean(R.styleable.TabLayout_tl_animEnabled, true);
        setAnimEnabled(isAnimEnabled);
        /*动画持续时间默认1000*/
        final int animDuration = ta.getInteger(R.styleable.TabLayout_tl_animDuration, 1000);
        setAnimDuration(animDuration);
        final boolean isReselectedAnimEnabled = ta.getBoolean(R.styleable.TabLayout_tl_reselectedAnimEnabled, false);
        setReselectedAnimEnabled(isReselectedAnimEnabled);
        final boolean isAnimRecycle = ta.getBoolean(R.styleable.TabLayout_tl_animIsRecycle, false);
        setAnimRecycle(isAnimRecycle);
        /*动画开始透明度默认0.2f*/
        float alphaStart = ta.getFloat(R.styleable.TabLayout_tl_alphaStart, 0.2f);
        setAlphaStart(alphaStart);
        ta.recycle();
    }

    /*设置tab数据源*/
    public void setupTab(@Nullable List<Tab> tabList) {
        if (tabList == null || tabList.isEmpty()) return;
        this.mTabList.clear();
        this.mTabList.addAll(tabList);
        if (!isTabSetup) {
            isTabSetup = true;
        }
        notifyDataSetChanged();
    }

    /*刷新数据*/
    public void notifyDataSetChanged() {
        if (isTabSetup) {
            this.removeAllViews();
            for (int i = 0; i < mTabList.size(); i++) {
                TabTextView tabTextView = obtainTabView(i);
                addTab(i, tabTextView);
            }
            setCurrentTab(mCurrentTab);
        }
    }

    private TabTextView obtainTabView(int position) {
        TabTextView tvTab = new TabTextView(getContext());
        tvTab.setTag(position);
        tvTab.setGravity(Gravity.CENTER); //文字居中
        tvTab.setCompoundDrawablePadding((int) mDrawablePadding);
        return tvTab;
    }

    private void addTab(int i, TabTextView tabTextView) {
        final int position = (Integer) tabTextView.getTag();
        tabTextView.setText(mTabList.get(i).getTabText());
        tabTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mUnSelectTextSize);
        tabTextView.setTextColor(mUnSelectTextColor);
        if (mUnSelectTextTypeface != null) {
            tabTextView.setTypeface(mUnSelectTextTypeface);
        }
        Drawable unSelectDrawable = mTabList.get(i).getUnSelectDrawable();
        setDrawable(tabTextView, unSelectDrawable);
        tabTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentTab != position) {
                    setCurrentTab(position);
                    if (mOnTabSelectListener != null) {
                        mOnTabSelectListener.onTabSelect(position);
                    }
                } else {
                    if (isReselectedAnimEnabled) {
                        startValueAnim(position);
                    }
                    if (mOnTabSelectListener != null) {
                        mOnTabSelectListener.onTabReselect(position);
                    }
                }
            }
        });
        LayoutParams params = isTabSpaceEqual ? new LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f) : new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if (mTabWidth > 0) {
            params = new LinearLayout.LayoutParams(mTabWidth, LayoutParams.MATCH_PARENT);
        }
        tabTextView.setLayoutParams(params);
        addView(tabTextView);
    }

    private void updateTabSelection(int position) {
        for (int i = 0; i < mTabList.size(); ++i) {
            final boolean isSelect = i == position;
            TextView tabView = (TextView) getChildAt(i);
            tabView.setTextSize(TypedValue.COMPLEX_UNIT_PX, isSelect ? mSelectedTextSize : mUnSelectTextSize);
            Drawable drawable = isSelect ? mTabList.get(i).getSelectedDrawable() : mTabList.get(i).getUnSelectDrawable();
            setDrawable(tabView, drawable);
            tabView.setTextColor(isSelect ? mSelectedTextColor : mUnSelectTextColor);
            Typeface typeface = isSelect ? mSelectedTextTypeface : mUnSelectTextTypeface;
            if (typeface != null) {
                tabView.setTypeface(typeface);
            }
        }
    }

    private void setDrawable(TextView tabView, Drawable drawable) {
        if (mIconWidth > 0 && mIconHeight > 0 && drawable != null) {
            drawable.setBounds(0, 0, mIconWidth, mIconHeight);
            if (mIconGravity == ICON_LEFT) { //drawableLeft
                tabView.setCompoundDrawables(drawable, null, null, null);
            } else if (mIconGravity == ICON_TOP) { //drawableTop
                tabView.setCompoundDrawables(null, drawable, null, null);
            } else if (mIconGravity == ICON_RIGHT) { //drawableRight
                tabView.setCompoundDrawables(null, null, drawable, null);
            } else if (mIconGravity == ICON_BOTTOM) {    //drawableBottom
                tabView.setCompoundDrawables(null, null, null, drawable);
            }
        } else {
            if (mIconGravity == ICON_LEFT) { //drawableLeft
                tabView.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
            } else if (mIconGravity == ICON_TOP) { //drawableTop
                tabView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
            } else if (mIconGravity == ICON_RIGHT) { //drawableRight
                tabView.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null);
            } else if (mIconGravity == ICON_BOTTOM) {    //drawableBottom
                tabView.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable);
            }
        }
    }

    private void startValueAnim(int position) {
        if (isAnimEnabled) {
            final TextView tabView = (TextView) getChildAt(position);
            if (isAnimRecycle) {
                stopValueAnim();
            }
            mValueAnimator = ValueAnimator.ofFloat(mAlphaStart, 1);
            mValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float alpha = (Float) valueAnimator.getAnimatedValue();
                    tabView.setAlpha(alpha);
                }
            });
            mValueAnimator.setDuration(mAnimDuration).start();
        }
    }

    private void stopValueAnim() {
        if (mValueAnimator != null) {
            mValueAnimator.cancel();
            mValueAnimator = null;
        }
    }

    /*设置当前tab*/
    public void setCurrentTab(int position) {
        if (position < 0 || position >= mTabList.size()) return;
        this.mCurrentTab = position;
        updateTabSelection(position);
        startValueAnim(position);
    }

    public void setTabWidth(int tabWidth) {
        this.mTabWidth = tabWidth;
        notifyDataSetChanged();
    }

    public int getTabWidth() {
        return this.mTabWidth;
    }

    public void setTabSpaceEqual(boolean isTabSpaceEqual) {
        this.isTabSpaceEqual = isTabSpaceEqual;
        notifyDataSetChanged();
    }

    public boolean isTabSpaceEqual() {
        return this.isTabSpaceEqual;
    }

    public int getIconWidth() {
        return mIconWidth;
    }

    public void setIconWidth(int iconWidth) {
        this.mIconWidth = iconWidth;
        notifyDataSetChanged();
    }

    public int getIconHeight() {
        return mIconHeight;
    }

    public void setIconHeight(int iconHeight) {
        this.mIconHeight = iconHeight;
        notifyDataSetChanged();
    }

    public int getSelectedTextColor() {
        return mSelectedTextColor;
    }

    public void setSelectedTextColor(int selectedTextColor) {
        this.mSelectedTextColor = selectedTextColor;
        notifyDataSetChanged();
    }

    public int getUnSelectTextColor() {
        return mUnSelectTextColor;
    }

    public void setUnSelectTextColor(int unSelectTextColor) {
        this.mUnSelectTextColor = unSelectTextColor;
        notifyDataSetChanged();
    }

    public int getIconGravity() {
        return mIconGravity;
    }

    public void setIconGravity(int iconGravity) {
        this.mIconGravity = iconGravity;
        notifyDataSetChanged();
    }

    public float getDrawablePadding() {
        return mDrawablePadding;
    }

    public void setDrawablePadding(float drawablePadding) {
        this.mDrawablePadding = drawablePadding;
        notifyDataSetChanged();
    }

    public float getTextSize() {
        return mTextSize;
    }

    public void setTextSize(float textSize) {
        this.mTextSize = textSize;
        if (mSelectedTextSize == 0) {   //未设置选中tab的文字大小，默认取textSize值
            mSelectedTextSize = mTextSize;
        }
        if (mUnSelectTextSize == 0) {   //未设置未选中tab的文字大小，默认取textSize值
            mUnSelectTextSize = mTextSize;
        }
        notifyDataSetChanged();
    }

    public void setTextStyle(int textStyle) {
        this.mTextStyle = textStyle;
        setTextStyle(createTypeface(this.mTextStyle));
    }

    @Nullable
    public Typeface createTypeface(int style) {
        Typeface typeface = null;
        if (style == STYLE_NORMAL) {
            typeface = Typeface.defaultFromStyle(Typeface.NORMAL);
        } else if (style == STYLE_BOLD) {
            typeface = Typeface.defaultFromStyle(Typeface.BOLD);
        } else if (style == STYLE_ITALIC) {
            typeface = Typeface.defaultFromStyle(Typeface.ITALIC);
        } else if (style == STYLE_BOLD_ITALIC) {
            typeface = Typeface.defaultFromStyle(Typeface.BOLD_ITALIC);
        }
        return typeface;
    }

    public int getTextStyle() {
        return mTextStyle;
    }

    public void setTextStyle(@Nullable Typeface typeface) {
        this.mTextTypeface = typeface;
        if (this.mTextTypeface != null) {
            if (mSelectedTextTypeface == null) {
                mSelectedTextTypeface = typeface;
            }
            if (mUnSelectTextTypeface == null) {
                mUnSelectTextTypeface = typeface;
            }
        }
        notifyDataSetChanged();
    }

    @Nullable
    public Typeface getTextTypeface() {
        return this.mTextTypeface;
    }

    public void setSelectTextStyle(int selectTextStyle) {
        this.mSelectedTextStyle = selectTextStyle;
        setSelectTextStyle(createTypeface(this.mSelectedTextStyle));
    }

    public int getSelectTextStyle() {
        return this.mSelectedTextStyle;
    }

    public void setSelectTextStyle(@Nullable Typeface selectTypeface) {
        this.mSelectedTextTypeface = selectTypeface;
        notifyDataSetChanged();
    }

    @Nullable
    public Typeface getSelectTextTypeface() {
        return this.mSelectedTextTypeface;
    }

    public void setUnSelectTextStyle(int unSelectTextStyle) {
        this.mUnSelectTextStyle = unSelectTextStyle;
        setUnSelectTextStyle(createTypeface(this.mUnSelectTextStyle));
    }

    public int getUnSelectTextStyle() {
        return this.mUnSelectTextStyle;
    }

    public void setUnSelectTextStyle(@Nullable Typeface unSelectTypeface) {
        this.mUnSelectTextTypeface = unSelectTypeface;
        notifyDataSetChanged();
    }

    @Nullable
    public Typeface getUnSelectTextTypeface() {
        return this.mUnSelectTextTypeface;
    }

    public float getSelectedTextSize() {
        return mSelectedTextSize;
    }

    public void setSelectedTextSize(float selectedTextSize) {
        this.mSelectedTextSize = selectedTextSize;
        notifyDataSetChanged();
    }

    public float getUnSelectTextSize() {
        return mUnSelectTextSize;
    }

    public void setUnSelectTextSize(float unSelectTextSize) {
        this.mUnSelectTextSize = unSelectTextSize;
        notifyDataSetChanged();
    }

    public void setAnimEnabled(boolean animEnabled) {
        isAnimEnabled = animEnabled;
    }

    public void setAnimRecycle(boolean animRecycle) {
        isAnimRecycle = animRecycle;
    }

    public void setReselectedAnimEnabled(boolean reselectedAnimEnabled) {
        isReselectedAnimEnabled = reselectedAnimEnabled;
    }

    public int getAnimDuration() {
        return mAnimDuration;
    }

    public void setAnimDuration(int mAnimDuration) {
        this.mAnimDuration = mAnimDuration;
    }

    public float getAlphaStart() {
        return mAlphaStart;
    }

    public void setAlphaStart(@FloatRange(from = 0.0, to = 1.0) float alphaStart) {
        if (alphaStart > 1.0f) {
            alphaStart = 1.0f;
        } else if (alphaStart < 0.0f) {
            alphaStart = 0.0f;
        }
        this.mAlphaStart = alphaStart;
    }

    public void setOnTabSelectListener(OnTabSelectListener onTabSelectListener) {
        this.mOnTabSelectListener = onTabSelectListener;
    }

    public interface OnTabSelectListener {
        void onTabSelect(int position);

        void onTabReselect(int position);
    }
}
