package com.galloped.tablayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.TextView;

/**
 * 自定义TextView drawableLeft、drawableTop、drawableRight、drawableBottom等与文字一起居中显示
 */
@SuppressLint("AppCompatCustomView")
class TabTextView extends TextView {

    public TabTextView(Context context) {
        super(context);
    }

    public TabTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TabTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        Drawable[] drawables = getCompoundDrawables();
        if (drawables[0] != null || drawables[2] != null) {
            // 左、右
            setGravity(Gravity.CENTER_VERTICAL | (drawables[0] != null ? Gravity.START : Gravity.END));
        } else if (drawables[1] != null || drawables[3] != null) {
            // 上、下
            setGravity(Gravity.CENTER_HORIZONTAL | (drawables[1] == null ? Gravity.BOTTOM : Gravity.TOP));
        }
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int drawablePadding = getCompoundDrawablePadding();
        Drawable[] drawables = getCompoundDrawables();
        if (drawables[0] != null) {// 左
            int drawableWidth = drawables[0].getIntrinsicWidth();
            float bodyWidth;
            if (TextUtils.isEmpty(getText())) {
                bodyWidth = drawableWidth;
            } else {
                float textWidth = getPaint().measureText(getText().toString());
                bodyWidth = textWidth + drawableWidth + drawablePadding + getPaddingLeft() + getPaddingRight();
            }
            canvas.translate((getWidth() - bodyWidth) / 2, 0f);
        } else if (drawables[2] != null) { // 右
            int drawableWidth = drawables[2].getIntrinsicWidth();
            float bodyWidth;
            if (TextUtils.isEmpty(getText())) {
                bodyWidth = drawableWidth;
            } else {
                float textWidth = getPaint().measureText(getText().toString());
                bodyWidth = textWidth + drawableWidth + drawablePadding + getPaddingLeft() + getPaddingRight();
            }
            canvas.translate((bodyWidth - getWidth()) / 2, 0f);
        } else if (drawables[1] != null) {   // 上
            int drawableHeight = drawables[1].getIntrinsicHeight();
            float bodyHeight;
            if (TextUtils.isEmpty(getText())) {
                bodyHeight = drawableHeight;
            } else {
                Paint.FontMetrics fm = getPaint().getFontMetrics();
                float fontHeight = (float) Math.ceil((fm.descent - fm.ascent));
                bodyHeight = fontHeight + drawableHeight + drawablePadding + getPaddingLeft() + getPaddingRight();
            }
            canvas.translate(0f, (getHeight() - bodyHeight) / 2);
        } else if (drawables[3] != null) {// 下
            int drawableHeight = drawables[3].getIntrinsicHeight();
            float bodyHeight;
            if (TextUtils.isEmpty(getText())) {
                bodyHeight = drawableHeight;
            } else {
                Paint.FontMetrics fm = getPaint().getFontMetrics();
                float fontHeight = (float) Math.ceil((fm.descent - fm.ascent));
                bodyHeight = fontHeight + drawableHeight + drawablePadding + getPaddingLeft() + getPaddingRight();
            }
            canvas.translate(0f, (bodyHeight - getHeight()) / 2);
        }
        super.onDraw(canvas);
    }
}
