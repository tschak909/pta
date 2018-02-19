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
    private int drawingColorFG;
    private int drawingColorBG;
    private boolean boldWritingMode;
    private int[] currentFont;
    private boolean verticalWritingMode;
    private PlatoRAM ram;
    private boolean modeXOR;
    private PLATOFont mFonts;


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

    public boolean isBoldWritingMode() {
        return boldWritingMode;
    }

    public void setBoldWritingMode(boolean boldWritingMode) {
        this.boldWritingMode = boldWritingMode;
    }

    public int[] getCurrentFont() {
        return currentFont;
    }

    public void setCurrentFont(int[] currentFont) {
        this.currentFont = currentFont;
    }

    public boolean isVerticalWritingMode() {
        return verticalWritingMode;
    }

    public void setVerticalWritingMode(boolean verticalWritingMode) {
        this.verticalWritingMode = verticalWritingMode;
    }

    public PlatoRAM getRam() {
        return ram;
    }

    public void setRam(PlatoRAM ram) {
        this.ram = ram;
    }

    public boolean isModeXOR() {
        return modeXOR;
    }

    public void setModeXOR(boolean modeXOR) {
        this.modeXOR = modeXOR;
    }

    public int getDrawingColorBG() {
        return drawingColorBG;
    }

    public void setDrawingColorBG(int drawingColorBG) {
        this.drawingColorBG = drawingColorBG;
    }

    public PLATOFont getFonts() {
        return mFonts;
    }

    public void setFonts(PLATOFont Font) {
        this.mFonts = Font;
    }

    public int getDrawingColorFG() {
        return drawingColorFG;
    }

    public void setDrawingColorFG(int dc) {
        this.drawingColorFG = dc;
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
        setFonts(new PLATOFont());
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

        // TODO: scale to aspect ratio instead of naive stretch.
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

        x = x & 0x1FF;
        y = y & 0x1FF ^ 0x1FF; // Flip vertical axes.

        if (bXOR) {
            int prevColor = mBitmap.getPixel(x, y);
            newColor = prevColor ^ color;
        } else {
            newColor = color;
        }

        mBitmap.setPixel(x, y, newColor);
    }

    /**
     * Plot line using current drawingColorFG
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
        setPoint(x1, y1, getDrawingColorFG(), false);

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
                setPoint(x1, y1, getDrawingColorFG(), false);

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
                setPoint(x1, y1, getDrawingColorFG(), false);
            }
        }
    }

    /**
     * Erase the entire display.
     */
    public void erase() {
        mBitmap.eraseColor(0);
    }


    /**
     * Erase a block of the display.
     *
     * @param x1 Beginning X coordinate of erase (0-511)
     * @param y1 Beginning Y coordinate of erase (0-511)
     * @param x2 Ending X coordinate of erase (0-511)
     * @param y2 Ending Y coordinate of erase (0-511)
     */
    public void erase(int x1, int y1, int x2, int y2) {
        // TODO: implement selective erase(x1,y1,x2,y2)
    }

    /**
     * Draw character onto canvas.
     *
     * @param x       X coordinate to place text (0-511)
     * @param y       Y coordinate to place text (0-511)
     * @param charset Character set to choose (0, 1, 2, 3)
     * @param charnum Character to select
     * @param autobs  Automatic backspace?
     */
    public void drawChar(int x, int y, int charset, int charnum, boolean autobs) {

        // Save colors here so we can swap if needed for inverse video.
        int fgcolor = getDrawingColorFG();
        int bgcolor = getDrawingColorBG();

        // Drawing indexes
        int i = 0, j = 0, saveY = 0, dx = 0, dy = 0, sdy = 0;

        // Current Char being drawn.
        int currentChar = 0;
        int charindex = 0;

        // Current X and Y position (initially at origin.)
        int cx = x, cy = y;

        if (isVerticalWritingMode()) {
            cx = y;
            cy = x;
        }
        saveY = cy;
        dx = dy = (isBoldWritingMode() ? 2 : 1);
        sdy = 1;

        if (isVerticalWritingMode()) {
            sdy = -1;
            dy = -dy;
        }

        switch (charset) {
            case 0:
                setCurrentFont(getFonts().getPlato_m0());
                break;
            case 1:
                setCurrentFont(getFonts().getPlato_m1());
                break;
            case 2:
                setCurrentFont(getFonts().getPlato_m2());
                break;
            case 3:
                setCurrentFont(getFonts().getPlato_m3());
                break;
        }

        if (modeXOR || (getRam().getWeMode() == 1))   // Inverse text.
        {
            // Swap colors if we're asked to do inverse video.
            fgcolor = getDrawingColorBG();
            bgcolor = getDrawingColorFG();
        }

        // Select the current character
        charindex = charnum * 8;

        // Set current x and y positions to origin.

        // Draw the character onto the bitmap
        for (j = 0; j < 8; j++) {
            x = cx;
            cy = saveY;
            currentChar = getCurrentFont()[charindex];
            for (i = 0; i < 16; i++) {
                y = cy;
                if ((currentChar & 1) == 0)
                {
                    // Blank pixel, background do we erase?
                    if ((getRam().getMode() & 2) == 0) {
                        // Yes, blit the background color.
                        setPoint(x, y, bgcolor, isModeXOR());
                        if (isBoldWritingMode()) {
                            setPoint(x + 1, y, bgcolor, false);
                            setPoint(x, y + sdy, bgcolor, false);
                            setPoint(x + 1, y + sdy, bgcolor, false);
                        }
                    }
                } else {
                    // Not blank pixel, blit the foreground color.
                    setPoint(x, y, fgcolor, isModeXOR());
                    if (isBoldWritingMode()) {
                        setPoint(x + 1, y, fgcolor, isModeXOR());
                        setPoint(x, y + sdy, fgcolor, isModeXOR());
                        setPoint(x + 1, y + sdy, fgcolor, isModeXOR());
                    }
                }
                currentChar >>= 1;
                cy += dy;
                y = cy;
            }
            charindex++;
            cx += dx;
        }

    }
}
