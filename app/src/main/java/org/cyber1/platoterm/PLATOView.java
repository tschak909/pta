/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    private int drawingColor;

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

    public int getDrawingColor() {
        return drawingColor;
    }

    public void setDrawingColor(int drawingColor) {
        this.drawingColor = drawingColor;
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

        mRenderRect.top = 0;
        mRenderRect.left = 0;
        mRenderRect.bottom = getDisplayMetrics().heightPixels;
        mRenderRect.right = getDisplayMetrics().widthPixels;

        canvas.drawBitmap(mBitmap, null, mRenderRect, null);

    }

    /**
     * Plot point into PLATO bitmap.
     *
     * @param x     X coordinate (0-511)
     * @param y     Y coordinate (0-511)
     * @param color (32-bit color value, sans alpha)
     * @param bXOR  true to plot pixel using XOR, otherwise, plot pixel directly.
     */
    public void setPoint(int x, int y, int color, boolean bXOR) {
        int newColor;
        if (bXOR) {
            int prevColor = mBitmap.getPixel(x, y);
            newColor = prevColor ^ color;
        } else {
            newColor = color;
        }
        if ((x > 0 && x < mBitmap.getWidth()) || (y > 0 && y < mBitmap.getHeight()))
            mBitmap.setPixel(x, y, newColor);
    }

    /**
     * Plot line using current drawingColor
     *
     * @param x1 Beginning X coordinate of line (0-511)
     * @param y1 Beginning Y coordinate of line (0-511)
     * @param x2 Ending X coordinate of line (0-511)
     * @param y2 Ending Y coordinate of line (0-511)
     */
    public void plotLine(int x1, int y1, int x2, int y2) {
        int dx, dy = 0;
        int sx, sy = 0;

        dx = x2 - x1;
        dy = y2 - y1;

        if (dx < 0) {
            dx = -dx;
            sx = -1;
        } else {
            sx = 1;
        }

        if (dy < 0) {
            dy = -dy;
            sy = -1;
        } else {
            sx = 1;
        }
        dx <<= 1;
        dy <<= 1;

        // Draw first point
        setPoint(x1, y1, getDrawingColor(), false);

        // Check for shallow line
        if (dx > dy) {
            int fraction = dy - (dx >> 1);
            while (x1 != x2) {
                if (fraction >= 0) {
                    y1 += sy;
                    fraction -= dx;
                }
                x1 += sx;
                fraction += dy;
                setPoint(x1, y1, getDrawingColor(), false);

            }
        }
        // otherwise, steep line.
        else {
            int fraction = dx - (dy >> 1);
            while (y1 != y2) {
                if (fraction >= 0) {
                    x1 += sx;
                    fraction -= dy;
                }
                y1 += sy;
                fraction += dx;
                setPoint(x1, y1, getDrawingColor(), false);
            }
        }
    }

}
