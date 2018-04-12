/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class PLATOView extends View {
    private static final int WIDTH=512;
    private static final int HEIGHT=512;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;
    int contentWidth;
    int contentHeight;
    private Bitmap mBitmap;
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

    private void init(AttributeSet attrs, int defStyle) {

        if (attrs != null) {
            Log.d("PLATOActivity", "PLATOView init called with attrs " + attrs.toString());
        }

        Log.d("PLATOActivity", "PLATOView init called with defstyle " + defStyle);

        mRenderRect = new RectF();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paddingLeft = getPaddingLeft();
        paddingTop = getPaddingTop();
        paddingRight = getPaddingRight();
        paddingBottom = getPaddingBottom();

        contentWidth = getWidth() - paddingLeft - paddingRight;
        contentHeight = getHeight() - paddingTop - paddingBottom;

        mRenderRect.top = paddingTop;
        mRenderRect.left = (contentWidth / 2) - (contentHeight / 2) + paddingLeft;
        mRenderRect.bottom = contentHeight - paddingBottom;
        mRenderRect.right = mRenderRect.left + contentHeight - paddingRight;

        canvas.drawBitmap(mBitmap, null, mRenderRect, null);

    }

    /**
     * Set the view's bitmap to the appropriate PLATOTerminal bitmap
     *
     * @param bitmap the appropriate PLATOTerminal bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.mBitmap = bitmap;
    }
}


