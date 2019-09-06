package ru.ystu.myystu.Utils;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.facebook.drawee.drawable.ProgressBarDrawable;

public class CircleProgressBar extends ProgressBarDrawable {

    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mBackgroundColor;
    private int mColor;
    private int mBarWidth = 20;
    private int mLevel = 0;
    private boolean mHideWhenZero = false;
    private int radius = 60;

    public CircleProgressBar() {
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(12f);
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public void setColor(int color) {
        if (mColor != color) {
            mColor = color;
            invalidateSelf();
        }
    }

    public int getColor() {
        return mColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        if (mBackgroundColor != backgroundColor) {
            mBackgroundColor = backgroundColor;
            invalidateSelf();
        }
    }

    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    public void setBarWidth(int barWidth) {
        if (mBarWidth != barWidth) {
            mBarWidth = barWidth;
            invalidateSelf();
        }
    }

    public int getBarWidth() {
        return mBarWidth;
    }

    public void setHideWhenZero(boolean hideWhenZero) {
        mHideWhenZero = hideWhenZero;
    }

    public boolean getHideWhenZero() {
        return mHideWhenZero;
    }

    @Override
    protected boolean onLevelChange(int level) {
        mLevel = level;
        invalidateSelf();
        return true;
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
    }

    @Override
    public void draw(Canvas canvas) {
        if (mHideWhenZero && mLevel == 0) {
            return;
        }
        drawCircle(canvas, mBackgroundColor);
        drawArc(canvas, mLevel, mColor);
    }

    private final int MAX_LEVEL = 10000;

    private void drawArc(Canvas canvas, int level, int color) {
        mPaint.setColor(color);

        Rect bounds = getBounds();
        int xpos = bounds.left + bounds.width() / 2;
        int ypos = bounds.bottom - bounds.height() / 2;
        RectF rectF = new RectF(xpos - radius, ypos - radius, xpos + radius, ypos + radius);
        float degree = (float) level / (float) MAX_LEVEL * 360;
        canvas.drawArc(rectF, 270, degree, false, mPaint);
    }

    private void drawCircle(Canvas canvas, int color) {
        mPaint.setColor(color);
        Rect bounds = getBounds();
        int xpos = bounds.left + bounds.width() / 2;
        int ypos = bounds.bottom - bounds.height() / 2;
        canvas.drawCircle(xpos, ypos, radius, mPaint);
    }
}
