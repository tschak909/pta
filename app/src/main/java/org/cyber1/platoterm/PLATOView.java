/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class PLATOView extends View {
    private static final int WIDTH=512;
    private static final int HEIGHT=512;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;

    private Bitmap mBitmap;
    private DisplayMetrics mDisplayMetrics;
    private RectF mRenderRect;

    public PLATOView(Context context) {
        super(context);
        init(null, 0);
    }

    public PLATOView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public PLATOView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    public DisplayMetrics getDisplayMetrics() {
        return mDisplayMetrics;
    }

    public void setDisplayMetrics(DisplayMetrics mDisplayMetrics) {
        this.mDisplayMetrics = mDisplayMetrics;
    }

    private void init(AttributeSet attrs, int defStyle) {

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.ARGB_8888);
        }

        mRenderRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO: consider storing these as member variables to reduce
        // allocations per draw cycle.
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        int contentWidth = getWidth() - paddingLeft - paddingRight;
        int contentHeight = getHeight() - paddingTop - paddingBottom;


        for (int i = 0; i < 512; i++) {
            mBitmap.setPixel(i, i, Color.RED);
        }

        mRenderRect.top = 0;
        mRenderRect.left = 0;
        mRenderRect.bottom = getDisplayMetrics().heightPixels;
        mRenderRect.right = getDisplayMetrics().widthPixels;

        canvas.drawBitmap(mBitmap, null, mRenderRect, null);

    }


}
