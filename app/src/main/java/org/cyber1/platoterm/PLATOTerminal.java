/*
 * Copyright (c) 2018. Cyber1.org
 */

package org.cyber1.platoterm;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;

/**
 * PLATOTerminal contains terminal logic that is acted upon by requests from the
 * PLATOProtocol, to ultimately set state here in the terminal, which is then
 * read by PLATOActivity.
 */
public class PLATOTerminal {

    /**
     * PLATO Coordinate representing the leftmost screen coordinate.
     */
    private static final int SCREEN_LEFT = 0;
    /**
     * PLATO Coordinate representing the topmost screen coordinate.
     */
    private static final int SCREEN_TOP = 511;
    /**
     * PLATO terminal width, 512 pixels
     */
    private static final int WIDTH = 512;
    /**
     * PLATO terminal height, 512 pixels
     */
    private static final int HEIGHT = 512;
    /**
     * The bitmap configuration for the PLATOView, matches the PLATOView
     * so that we can copy the bitmap to the view.
     */
    private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
    /**
     * The PLATO Service parent that spawned this terminal.
     */
    private PLATOService service;
    /**
     * The PLATO Protocol decoder
     */
    private PLATOProtocol protocol;
    /**
     * The bitmap that will be updated, and copied to the view when asked.
     */
    private Bitmap mBitmap;

    /**
     * The terminal RAM (mode, character sets, etc.)
     */
    private PLATORam platoRam;

    /**
     * Current PLATO Font instance
     */
    private PLATOFont platoFont;

    /**
     * Current X position (0-511)
     */
    private int currentX;

    /**
     * Current Y position (0-511)
     */
    private int currentY;

    /**
     * Current Drawing Color Foreground (32 bit ARGB)
     */
    private int drawingColorFG;

    /**
     * Current Drawing Color Background (32 bit ARGB)
     */
    private int drawingColorBG;

    /**
     * Is XOR enabled?
     */
    private boolean modeXOR;

    /**
     * Vertical Writing enabled?
     */
    private boolean verticalWritingMode;

    /**
     * Bold Writing enabled?
     */
    private boolean boldWritingMode;

    /**
     * The current font used for text output.
     */
    private int[] currentFont;

    /**
     * The walk stack used for flood fill (paint)
     */
    private int[] walkstack;

    /**
     * The current X delta (character cell spacing value).
     * This value ultimately changes with bold and vertical writing mode setting.
     */
    private int deltaX;

    /**
     * The current Y delta (character cell spacing value).
     * This value ultimately changes with bold and vertical writing mode setting.
     */
    private int deltaY;

    /**
     * The current left margin, set e.g. with "at" commands.
     */
    private int margin;

    /**
     * The current character set used for text output (m0, m1, m2, m3)
     */
    private int currentCharacterSet;

    /**
     * Reverse Writing Mode (Right-to-Left, instead of Left-to-Right)
     */
    private boolean reverseWritingMode;

    /**
     * This is a handler for pulling the latest data from the network service
     */
    private final Handler networkHandler = new Handler();

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Protocol Runnable - Gather data from Network.
    ///////////////////////////////////////////////////////////////////////////////////////////////
    private final Runnable networkRunnable = new Runnable() {
        @Override
        public void run() {
            if (service != null && service.isRunning() && !service.getFromFIFO().isEmpty()) {
                for (int i = 0; i < service.getFromFIFO().size(); i++) {
                    protocol.decodeByte(service.getFromFIFO().poll());
                }
            }
            networkHandler.post(networkRunnable);
        }
    };

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Constructor
     */
    PLATOTerminal(PLATOService platoService) {
        this.service = platoService;
        this.platoFont = new PLATOFont();
        this.platoRam = new PLATORam();
        this.protocol = new PLATOProtocol(this);
        this.currentX = 0;
        this.currentY = 0;
        this.drawingColorFG = -1;
        this.drawingColorBG = 0;
        this.modeXOR = false;
        this.verticalWritingMode = false;
        this.boldWritingMode = false;
        this.currentFont = platoFont.getPlato_m0();
        this.deltaX = 8;
        this.deltaY = 16;
        this.margin = 0;
        this.currentCharacterSet = 0;
        this.reverseWritingMode = false;
        this.mBitmap = Bitmap.createBitmap(WIDTH, HEIGHT, BITMAP_CONFIG);
        this.walkstack = new int[512 * 512 + 2];
        this.platoRam.setMode(0x0F); // Char mode, rewrite
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Start
    ///////////////////////////////////////////////////////////////////////////////////////////////
    public void start() {
        networkHandler.post(networkRunnable);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Accessors
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Get the parent PLATOService
     *
     * @return the parent PLATOService
     */
    public PLATOService getService() {
        return service;
    }

    /**
     * Retrieve the Terminal Bitmap
     *
     * @return The PLATOTerminal's Bitmap
     */
    public Bitmap getBitmap() {
        return mBitmap;
    }

    /**
     * Return Current X position (0-511)
     *
     * @return Current X position (0-511)
     */
    public int getCurrentX() {
        return currentX;
    }

    /**
     * Set current X position (0-511)
     *
     * @param currentX New current X position (0-511)
     */
    public void setCurrentX(int currentX) {
        this.currentX = currentX;
    }

    /**
     * Return Current Y position (0-511)
     *
     * @return Current Y position (0-511)
     */
    public int getCurrentY() {
        return currentY;
    }

    /**
     * Set current Y position (0-511)
     *
     * @param currentY New current Y position (0-511)
     */
    public void setCurrentY(int currentY) {
        this.currentY = currentY;
    }

    /**
     * Get current drawing color foreground (32-bit ARGB)
     *
     * @return Current Drawing color foreground (32-bit ARGB)
     */
    private int getDrawingColorFG() {
        return drawingColorFG;
    }

    /**
     * Set current drawing color foreground (32-bit ARGB)
     *
     * @param drawingColorFG new drawing color foreground (32-bit ARGB)
     */
    public void setDrawingColorFG(int drawingColorFG) {
        this.drawingColorFG = drawingColorFG;
    }

    /**
     * Get current drawing color background (32-bit ARGB)
     *
     * @return Current drawing color background (32-bit ARGB)
     */
    private int getDrawingColorBG() {
        return drawingColorBG;
    }

    /**
     * Set current drawing color background (32-bit ARGB)
     *
     * @param drawingColorBG new drawing color (32-bit ARGB)
     */
    public void setDrawingColorBG(int drawingColorBG) {
        this.drawingColorBG = drawingColorBG;
    }

    /**
     * Is XOR mode enabled/disabled?
     *
     * @return current XOR mode
     */
    private boolean isModeXOR() {
        return modeXOR;
    }

    /**
     * Enable/disable modeXOR
     *
     * @param modeXOR true = XOR enabled, false = XOR disabled.
     */
    void setModeXOR(boolean modeXOR) {
        this.modeXOR = modeXOR;
    }


    /**
     * Return PLATORam instance
     *
     * @return PLATORam instance
     */
    PLATORam getPLATORam() {
        return platoRam;
    }

    /**
     * Return if vertical writing mode is enabled?
     *
     * @return the value of verticalWritingMode
     */
    private boolean isVerticalWritingMode() {
        return verticalWritingMode;
    }

    /**
     * Set new vertical writing mode value.
     *
     * @param verticalWritingMode The new vertical writing mode value.
     */
    public void setVerticalWritingMode(boolean verticalWritingMode) {
        this.verticalWritingMode = verticalWritingMode;
    }

    /**
     * Is bold writing mode enabled?
     *
     * @return the value of boldWritingMode
     */
    private boolean isBoldWritingMode() {
        return boldWritingMode;
    }

    /**
     * Set new bold writing mode value
     *
     * @param boldWritingMode the new value for boldWritingMode
     */
    public void setBoldWritingMode(boolean boldWritingMode) {
        this.boldWritingMode = boldWritingMode;
    }

    /**
     * Get the current active font matrix from PLATOFont
     *
     * @return the current active font matrix from PLATOFont
     */
    private int[] getCurrentFont() {
        return currentFont;
    }

    /**
     * Set the current active font matrix from PLATOFont
     *
     * @param currentFont the new active font matrix from PLATOFont
     */
    private void setCurrentFont(int[] currentFont) {
        this.currentFont = currentFont;
    }

    /**
     * Return the PLATOFont instance
     *
     * @return the PLATOFont instance
     */
    PLATOFont getPlatoFont() {
        return platoFont;
    }

    /**
     * Get the current X delta value (add for next character cell)
     *
     * @return The current X delta value
     */
    private int getDeltaX() {
        return deltaX;
    }

    /**
     * Get the current Y delta value (add for next character cell)
     *
     * @return The current Y delta value
     */
    private int getDeltaY() {
        return deltaY;
    }

    /**
     * Return the current left margin.
     *
     * @return the current left margin.
     */
    public int getMargin() {
        return margin;
    }

    /**
     * Set a new left margin
     *
     * @param margin the new left margin
     */
    public void setMargin(int margin) {
        this.margin = margin;
    }

    /**
     * Return the current character set used for alpha mode
     *
     * @return current character set number (0,1,2,3)
     */
    public int getCurrentCharacterSet() {
        return currentCharacterSet;
    }

    /**
     * Set character set number to use for alpha mode
     *
     * @param currentCharacterSet new character set number (0,1,2,3)
     */
    public void setCurrentCharacterSet(int currentCharacterSet) {
        this.currentCharacterSet = currentCharacterSet;
    }

    /**
     * Set specific reverse writing mode
     *
     * @param reverseWritingMode true = right-to-left, false = left-to-right
     */
    public void setReverseWritingMode(boolean reverseWritingMode) {
        this.reverseWritingMode = reverseWritingMode;
    }

    /**
     * Return the PLATOProtocol spawned by this PLATOTerminal
     *
     * @return the PLATOProtocol instance spawned by this PLATOTerminal
     */
    public PLATOProtocol getProtocol() {
        return protocol;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Terminal Drawing Primitives
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Draw point using current drawing mode
     *
     * @param x X coordinate (0-511)
     * @param y Y coordinate (0-511)
     */
    public void drawPoint(int x, int y) {
        if (isModeXOR() || (getPLATORam().getWeMode() & 1) == 1) {
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
    private void setPoint(int x, int y, int color, boolean bXOR) {
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
        service.updateBitmap();
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
        int sx, sy;

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
        int saveMode = getPLATORam().getMode();

        setModeXOR(false);
        getPLATORam().setMode(2);
        if (isModeXOR() || (getPLATORam().getWeMode() & 1) == 1) {
            mBitmap.eraseColor(drawingColorFG);
        } else {
            mBitmap.eraseColor(drawingColorBG);
        }
        setModeXOR(savexor);
        getPLATORam().setMode(saveMode);
        service.updateBitmap();
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

        if (isModeXOR() || (getPLATORam().getWeMode() & 1) == 1) {
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
        int fgcolor;
        int bgcolor;

        // Drawing indexes
        int i, j, saveY, dx, dy, sdy;
        boolean blankout = false;

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
                setCurrentFont(getPlatoFont().getPlato_m0());
                break;
            case 1:
                setCurrentFont(getPlatoFont().getPlato_m1());
                break;
            case 2:
                setCurrentFont(getPlatoFont().getPlato_m2());
                break;
            case 3:
                setCurrentFont(getPlatoFont().getPlato_m3());
                break;
        }

        if (modeXOR || ((getPLATORam().getWeMode() & 1) == 1))   // write or rewrite
        {
            fgcolor = drawingColorFG;
            bgcolor = drawingColorBG;
        } else {
            // Swap colors if we're asked to do inverse video or erase.
            fgcolor = drawingColorBG;
            bgcolor = drawingColorFG;

            if ((getPLATORam().getMode() & 2) == 0)
                blankout = true;
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
                if ((currentChar & 1) == 0) {
                    // Blank pixel, background do we erase?
                    if ((getPLATORam().getMode() & 2) == 0) {
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

        setCurrentX(getCurrentX() + getDeltaX());

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
        service.updateBitmap();
    }


    private void doPaint(int x, int y, int pat) {
        // doPaintWalker(x, y, pat, 0);
        // doPaintWalker(x, y, pat, 1);
    }

    @SuppressLint("Assert")
    private void doPaintWalker(int x, int y, int pat, int pass) {
        int sp;
        int pixels = 0;
        int maxsp = 0;
        int w;
        // int d;
        // int i;
        // boolean isCharFill = false;

        Log.d(this.getClass().getName(), "Paint Pattern: " + pat);

        sp = -1;
        walkstack[++sp] = 0;

        while (sp >= 0) {
            if (sp > maxsp) {
                assert (sp < walkstack.length - 1) : "Stack pointer out of bounds.";
                maxsp = sp;
            }
            /*
      The current pixel X to be painted, for flood fill
     */
            int currentPaintX = x;
            /*
      The current pixel Y to be painted, for flood fill
     */
            int currentPaintY = y;
            /*
      The current pixel being worked on for painting
     */
            int currentPaintPixel = mBitmap.getPixel(currentPaintX, currentPaintY);

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
                        // Plain flood fill.
                        currentPaintPixel = drawingColorFG;
                        mBitmap.setPixel(x, y, currentPaintPixel);
                        service.updateBitmap();
                    } else {
                        // Mark pixel by setting alpha to zero
                        int r = Color.red(currentPaintPixel);
                        int g = Color.green(currentPaintPixel);
                        int b = Color.blue(currentPaintPixel);
                        currentPaintPixel = Color.argb(0, r, g, b);
                        mBitmap.setPixel(x, y, currentPaintPixel);
                        service.updateBitmap();
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
        return ((pass == 0 && (currentPaintPixel & Color.argb(255, 0, 0, 0)) != 0) ||
                (pass != 0 && (currentPaintPixel == drawingColorBG ||
                        (currentPaintPixel & Color.argb(255, 0, 0, 0)) == 0)));
    }

    /**
     * Perform the requested paint operation based on assembled paint coordinate and pattern
     *
     * @param n The result from AssemblePaint which contains coordinates and requested pattern
     */
    public void paint(int n) {
        doPaint(currentX, currentY, n);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    // Terminal Control Primitives
    ///////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Go to sleep for 8ms.
     */
    public void nullSleep() {
        try {
            Thread.sleep(8); // 8ms in ASCII.
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Back up cursor one character cell.
     */
    public void backspace() {
        setCurrentX(getCurrentX() - getDeltaX());
    }

    /**
     * Move cursor forward one space.
     */
    public void forwardspace() {
        setCurrentX(getCurrentX() + getDeltaX());
    }

    /**
     * Move cursor down one space.
     */
    public void linefeed() {
        setCurrentY(getCurrentY() - getDeltaY());
    }

    /**
     * Move cursor up one space.
     */
    public void verticalTab() {
        setCurrentY(getCurrentY() + getDeltaY());
    }

    /**
     * Form feed - move cursor to the top of page (0,511)
     */
    public void formfeed() {
        setCurrentX(SCREEN_LEFT);
        setCurrentY(SCREEN_TOP);
    }

    /**
     * Carriage Return - move cursor to beginning of line + current margin.
     */
    public void carriageReturn() {
        setCurrentX(SCREEN_LEFT + getMargin());
        setCurrentY(getCurrentY() - getDeltaY() & 511);
    }

}
