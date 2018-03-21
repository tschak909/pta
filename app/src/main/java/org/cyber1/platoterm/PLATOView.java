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
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * TODO: document your custom view class.
 */
public class PLATOView extends View {
    private static final int WIDTH=512;
    private static final int HEIGHT=512;
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    private static final boolean TEST_BOLD_ON = true;
    private static final boolean TEST_VERTICAL_ON = true;
    private static final boolean TEST_XOR_ON = true;
    private static final int COLOR_WHITE = 0xFFFFFFFF;
    private static final int COLOR_BLACK = 0x00000000;
    private static final int TOP_RIGHT_X = 511;
    private static final int TOP_RIGHT_Y = 511;
    private static final int BOTTOM_LEFT_X = 0;
    private static final int BOTTOM_LEFT_Y = 0;
    private static final int MIDDLE_X = 256;
    private static final int MIDDLE_Y = 256;
    private static final int CHARSET_0 = 0;
    private static final int CHAR_A = 1;
    private static final boolean AUTOBS_TEST = true;
    int paddingLeft;
    int paddingTop;
    int paddingRight;
    int paddingBottom;
    int contentWidth;
    int contentHeight;
    private Bitmap mBitmap;
    private DisplayMetrics mDisplayMetrics;
    private RectF mRenderRect;
    private int drawingColorFG;
    private int drawingColorBG;
    private boolean boldWritingMode;
    private int[] currentFont;
    private boolean verticalWritingMode;
    private PLATORam ram;
    private boolean modeXOR;
    private boolean touchPanel;
    private PLATOFont font;
    private int[] walkstack;
    private int currentPaintX;
    private int currentPaintY;
    private int currentPaintPixel;
    private int currentPaintChar;


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

    public PLATORam getRam() {
        return ram;
    }

    public void setRam(PLATORam ram) {
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

        walkstack = new int[512 * 512 + 2];

        if (mBitmap == null) {
            mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, BITMAP_CONFIG);
        }

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
     * Draw point using current drawing mode
     *
     * @param x X coordinate (0-511)
     * @param y Y coordinate (0-511)
     */
    public void drawPoint(int x, int y) {
        if (isModeXOR() || (getRam().getWeMode() & 1) == 1) {
            // Mode rewrite/write
            setPoint(x, y, getDrawingColorFG(), isModeXOR());
        } else {
            // Mode inverse or erase
            setPoint(x, y, getDrawingColorBG(), isModeXOR());
        }
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
        int dx, dy;
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
            sy = 1;
        }
        dx <<= 1;
        dy <<= 1;

        // Draw first point
        drawPoint(x1, y1);

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
                drawPoint(x1, y1);

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
                drawPoint(x1, y1);
            }
        }
    }

    /**
     * Erase the entire display.
     */
    public void erase() {
        boolean savexor = isModeXOR();
        int saveMode = getRam().getMode();

        setModeXOR(false);
        getRam().setMode(2);
        if (isModeXOR() || (getRam().getWeMode() & 1) == 1) {
            mBitmap.eraseColor(drawingColorFG);
        } else {
            mBitmap.eraseColor(drawingColorBG);
        }
        setModeXOR(savexor);
        getRam().setMode(saveMode);
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
        int t;
        int x, y;
        int currentColor;

        if (x1 > x2) {
            t = x1;
            x1 = x2;
            x2 = t;
        }
        if (y1 > y2) {
            t = y1;
            y1 = y2;
            y2 = t;
        }

        if (isModeXOR() || (getRam().getWeMode() & 1) == 1) {
            // mode rewrite or write
            currentColor = drawingColorFG;
        } else {
            // mode inverse or erase
            currentColor = drawingColorBG;
        }

        for (y = y1; y <= y2; y++) {
            for (x = x1; x <= x2; x++) {
                setPoint(x, y, currentColor, isModeXOR());
            }
        }
        // todo: implement textmap.
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

        if (autobs) {
            Log.d("PLATOActivity", "autobs set, but not implemented.");
        }

        // Save colors here so we can swap if needed for inverse video.
        int fgcolor = getDrawingColorFG();
        int bgcolor = getDrawingColorBG();

        // Drawing indexes
        int i, j, saveY, dx, dy, sdy;
        boolean blankout=false;

        // Current Char being drawn.
        int currentChar;
        int charindex;

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
                setCurrentFont(getFont().getPlato_m0());
                break;
            case 1:
                setCurrentFont(getFont().getPlato_m1());
                break;
            case 2:
                setCurrentFont(getFont().getPlato_m2());
                break;
            case 3:
                setCurrentFont(getFont().getPlato_m3());
                break;
        }

        if (modeXOR || ((getRam().getWeMode() & 1) == 1))   // write or rewrite
        {
            fgcolor = drawingColorFG;
            bgcolor = drawingColorBG;
        } else {
            // Swap colors if we're asked to do inverse video or erase.
            fgcolor = drawingColorBG;
            bgcolor = drawingColorFG;

            if ((getRam().getMode() & 2) == 0)
                blankout=true;
        }

        if (blankout) {
            charindex = 0x2D * 8; // Space
        } else {
            charindex = charnum * 8;
        }

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
                        setPoint(x, y, bgcolor, false);
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
            }
            charindex++;
            cx += dx;
        }
    }

    /**
     * Produce scrolling by block copying 0,0,511,495 into temporary space, blanking bitmap, and
     * then copying temporary space back onto main bitmap, shifted upward.
     */
    public void scrollUp() {
        int[] temp = new int[WIDTH * HEIGHT * 4];
        mBitmap.getPixels(temp, 0, WIDTH, 0, 16, 511, 495);
        mBitmap.eraseColor(0);
        mBitmap.setPixels(temp, 0, WIDTH, 0, 0, 511, 495);
    }

    /**
     * Receive view pixels as a bitmap array.
     *
     * @param x1 Starting X position of box to copy
     * @param y1 Starting Y position of box to copy
     * @param x2 Ending X position of box to copy
     * @param y2 Ending Y position of box to copy
     * @return The resulting bitmap
     */
    public int[] getPixels(int x1, int y1, int x2, int y2) {
        int[] returnedBitmap = new int[x2 * y2 * 4];
        mBitmap.getPixels(returnedBitmap, 0, WIDTH, x1, y1, x2, y2);
        return returnedBitmap;
    }

    /**
     * Plot view-pixels from bitmap array.
     *
     * @param termAreaBitmap Bitmap to copy in.
     * @param x1             Starting X position of box to copy
     * @param y1             Starting Y position of box to copy
     * @param x2             Ending X position of box to copy
     * @param y2             Ending Y position of box to copy
     */
    public void setPixels(int[] termAreaBitmap, int x1, int y1, int x2, int y2) {
        if (termAreaBitmap == null)
            return;

        mBitmap.setPixels(termAreaBitmap, 0, WIDTH, x1, y1, x2, y2);
    }

    public void doPaint(int x, int y, int pat) {
        doPaintWalker(x, y, pat, 0);
        doPaintWalker(x, y, pat, 1);
    }

    public void doPaintWalker(int x, int y, int pat, int pass) {
        int sp;
        int pixels=0;
        int maxsp = 0;
        int w;
        int d;
        int i;
        boolean isCharFill = false;

        sp = -1;
        walkstack[++sp] = 0;

        // If we have a pattern set, grab the character and set up
        // a character paint fill.
        if (pat>0) {
            isCharFill = true;
            if (pat<256) {
                pat += 32;
                if (pat < 128) {
                    d = PLATOFont.asciiM0[pat];
                } else {
                    d = PLATOFont.asciiM1[pat - 128];
                }
            } else {
                d = pat;
            }

            if (d == 0xff) {
                return;
            }

            // i is the set, d is the offset within the set
            i = d >> 7;
            d &= 0x7f;

            if (i==0) {
                currentPaintChar = getFont().getPlato_m0()[d];
            } else if (i==1) {
                currentPaintChar = getFont().getPlato_m1()[d];
            } else {
                // TODO: This is probably completely wrong
                if (d < 0x40)
                    currentPaintChar = getFont().getPlato_m2()[d];
                else
                    currentPaintChar = getFont().getPlato_m3()[d];
            }

        }

        while (sp >= 0) {
            if (sp > maxsp) {
                assert (sp < walkstack.length - 1) : "Stack pointer out of bounds.";
                maxsp=sp;
            }
            currentPaintX = x;
            currentPaintY = y;
            currentPaintPixel = mBitmap.getPixel(currentPaintX, currentPaintY);

            // Each time through the loop, we increment the top of stack
            // value, to reflect progress in the walk.

            w = walkstack[sp];
            walkstack[sp]++;

            switch (w) {
                case 0:
                    // If pixel is already filled, leave this level.
                    // Otherwise, fill the pixel, and explore to the left.
                    if (paintWalkDone(currentPaintPixel, pass)) {
                        walkstack[sp] = 4;
                        break;
                    }
                    if (pass > 0) {
                        if (isCharFill) {
                            // Char fills happen on coarse boundaries
                            int cx = x & 7;
                            int cy = y & 15;

                            // TODO: pick foreground or background pixel based on coarse position
                 /*           if ((cp[cx] & (0x8000 >> cy)) != 0)
                            {
                                // Foreground pixel, paint it
                        *pmap = m_fgpix;
                            }
                            else
                            {
                                // Backround pixel, unmark it
                        *pmap |= m_maxalpha;
                            }
                 */
                        } else {
                            currentPaintPixel = drawingColorFG;
                            mBitmap.setPixel(x, y, currentPaintPixel);
                        }
                    } else {
                        // Mark pixel by setting alpha to zero
                        currentPaintPixel = drawingColorFG & 0xFFFFFF;
                        mBitmap.setPixel(x, y, currentPaintPixel);
                    }
                    pixels++;
                    if (x > 0) {
                        x--;
                        walkstack[++sp] = 0;
                    }
                    break;
                case 1:
                    // Explore to the right
                    if (x < 511) {
                        x++;
                        walkstack[++sp] = 0;
                    }
                    break;
                case 2:
                    // Explore up
                    if (y < 511) {
                        y++;
                        walkstack[++sp] = 0;
                    }
                    break;
                case 3:
                    // Explore down
                    if (y > 0) {
                        y--;
                        walkstack[++sp] = 0;
                    }
                    break;
                case 4:
                    // Done exploring at this pixel. Pop stack. We have
                    // to adjust the coordinate, which is done based on
                    // the next to stop stack entry -- which reflects
                    // where the previous level is in its exploration, i.e.
                    // which coordinate adjustment it made before pushing
                    // this level. Note that the previous level entry reflects
                    // the increment of the stack value, so the case labels
                    // are one higher than the corresponding ones above.
                    --sp;
                    if (sp < 0) {
                        break;
                    }
                    switch (walkstack[sp]) {
                        case 1:
                            x++;
                            break;
                        case 2:
                            x--;
                            break;
                        case 3:
                            y--;
                            break;
                        case 4:
                            y++;
                            break;
                    }
            }
        }

        Log.d(this.getClass().getName(), "doPaintWalker: " + String.format("%d", pixels) + " pixels, " + String.format("%d", maxsp) + " max stack.");

    }

    /**
     * Determine if the paint walk is done by checking for background pixel, or
     * marked pixels (those with an alpha of 0, as drawn pixels have an alpha
     * of 255.
     *
     * @param currentPaintPixel The current bitmap pixel
     * @param pass              Which pass are we in?
     * @return true if we're done, false if we aren't.
     */
    private boolean paintWalkDone(int currentPaintPixel, int pass) {
        return ((pass != 0 && (currentPaintPixel & 0xFF000000) != 0) ||
                (pass == 0 && (currentPaintPixel == drawingColorBG ||
                        (currentPaintPixel & 0xFF000000) == 0)));
    }

    /**
     * A view test case.
     */
    public void testPattern() {
        setDrawingColorFG(0xFFFFFFFF);
        setDrawingColorBG(0x00000000);
        erase();
        setPoint(1, 1, 0xFFFFFFFF, false);
    }

    public boolean isTouchPanel() {
        return touchPanel;
    }

    public void setTouchPanel(boolean touchPanel) {
        this.touchPanel = touchPanel;
    }

    public PLATOFont getFont() {
        return font;
    }

    public void setFont(PLATOFont font) {
        this.font = font;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mRenderRect.contains(event.getX(), event.getY())) {
            float scaleX = contentWidth / 512;
            float scaleY = contentHeight / 512;
            float x = (event.getX() - mRenderRect.left) / scaleX;
            float y = (event.getY() - mRenderRect.top) / scaleY;
            Log.d(this.getClass().getName(), "TOUCH   X: " + x + "  Y: " + y);
        }
        return super.onTouchEvent(event);
    }
}


