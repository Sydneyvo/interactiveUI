// Name: Sydney Vo
// Date: 2025-11-05
// Course: CSE 123 (AF)
// Assignment: Mondrian Art
// TA: Trien Vuong
//
// Class purpose (client-facing):
//   Provides two public methods that paint a Mondrian-style image directly
//   into a provided Color[][] pixel grid.
//   - paintBasicMondrian: follows the assignment specification exactly.
//   - paintComplexMondrian: a recursive, randomized extension that biases
//     color choice by location while keeping the same overall behavior.
//
// Usage:
//   Construct a Color[][] of at least 300x300. Call one of the public
//   methods; the array is modified in place.

import java.util.Random;
import java.awt.Color;

public class Mondrian {
    // Minimum canvas height/width (assignment requirement).
    private static final int MIN_CANVAS = 300;
    // Thickness of black borders and grid lines.
    private static final int BORDER = 1;
    // Minimum interior width/height required to allow a split.
    private static final int MIN_CELL = 10;

    // Basic palette used by the required version.
    private static final Color[] BASIC_PALETTE = {
        Color.RED, Color.YELLOW, Color.CYAN, Color.WHITE
    };

    private final Random rand;

    /** Constructs with a default Random. */
    public Mondrian() {
        this(new Random());
    }

    /** Constructs with a supplied Random (useful for deterministic tests). */
    public Mondrian(Random rand) {
        this.rand = rand;
    }

    /**
     * Fills pixels with a basic Mondrian-style image.
     * Preconditions:
     *   - pixels is non-null, rectangular, and at least 300x300.
     * Effects:
     *   - Modifies pixels in place.
     * Throws:
     *   - IllegalArgumentException if preconditions are violated.
     */
    public void paintBasicMondrian(Color[][] pixels) {
        checkPixels(pixels);
        int h = pixels.length;
        int w = pixels[0].length;

        // Fill background black, then subdivide/paint within a 1px frame.
        fillSolid(pixels, 0, 0, w, h, Color.BLACK);
        divideAndPaintBasic(pixels, BORDER, BORDER, w - 2 * BORDER, h - 2 * BORDER);
    }

    /**
     * Fills pixels with a slightly more complex Mondrian:
     *   - Same subdivision rules as basic.
     *   - Color choice is slightly biased by region position.
     * Preconditions/Effects/Throws: same as paintBasicMondrian.
     */
    public void paintComplexMondrian(Color[][] pixels) {
        checkPixels(pixels);
        int h = pixels.length;
        int w = pixels[0].length;

        fillSolid(pixels, 0, 0, w, h, Color.BLACK);
        divideAndPaintComplex(pixels, BORDER, BORDER, w - 2 * BORDER, h - 2 * BORDER, w, h);
    }

    // ---------- BASIC ----------

    // Recursive subdivision for the basic version (no early returns).
    private void divideAndPaintBasic(Color[][] px, int x, int y, int w, int h) {
        int fullW = px[0].length;
        int fullH = px.length;

        boolean canSplitW = w >= fullW / 4;
        boolean canSplitH = h >= fullH / 4;

        if (!canSplitW && !canSplitH) {
            fillRegionWithBorder(px, x, y, w, h, pickBasicColor());
        } else if (canSplitW && canSplitH) {
            int vx = chooseSplit(x, w);
            int hy = chooseSplit(y, h);

            drawVLine(px, vx, y, h);
            drawHLine(px, x, hy, w);

            // top-left
            divideAndPaintBasic(px, x, y, vx - x, hy - y);
            // top-right
            divideAndPaintBasic(px, vx + 1, y, x + w - (vx + 1), hy - y);
            // bottom-left
            divideAndPaintBasic(px, x, hy + 1, vx - x, y + h - (hy + 1));
            // bottom-right
            divideAndPaintBasic(px, vx + 1, hy + 1, x + w - (vx + 1), y + h - (hy + 1));
        } else if (canSplitW) {
            int vx = chooseSplit(x, w);
            drawVLine(px, vx, y, h);
            divideAndPaintBasic(px, x, y, vx - x, h);
            divideAndPaintBasic(px, vx + 1, y, x + w - (vx + 1), h);
        } else {
            int hy = chooseSplit(y, h);
            drawHLine(px, x, hy, w);
            divideAndPaintBasic(px, x, y, w, hy - y);
            divideAndPaintBasic(px, x, hy + 1, w, y + h - (hy + 1));
        }
    }

    // ---------- COMPLEX ----------

    // Recursive subdivision for the complex version (no early returns).
    private void divideAndPaintComplex(Color[][] px, int x, int y, int w, int h, int fullW, int fullH) {
        boolean canSplitW = w >= fullW / 4;
        boolean canSplitH = h >= fullH / 4;

        if (!canSplitW && !canSplitH) {
            // Normalized approximate center for simple location bias.
            double cx = (x + w / 2.0) / fullW;
            double cy = (y + h / 2.0) / fullH;
            fillRegionWithBorder(px, x, y, w, h, chooseColor(cx, cy));
        } else if (canSplitW && canSplitH) {
            int vx = chooseSplit(x, w);
            int hy = chooseSplit(y, h);

            drawVLine(px, vx, y, h);
            drawHLine(px, x, hy, w);

            divideAndPaintComplex(px, x, y, vx - x, hy - y, fullW, fullH);
            divideAndPaintComplex(px, vx + 1, y, x + w - (vx + 1), hy - y, fullW, fullH);
            divideAndPaintComplex(px, x, hy + 1, vx - x, y + h - (hy + 1), fullW, fullH);
            divideAndPaintComplex(px, vx + 1, hy + 1, x + w - (vx + 1), y + h - (hy + 1), fullW, fullH);
        } else if (canSplitW) {
            int vx = chooseSplit(x, w);
            drawVLine(px, vx, y, h);
            divideAndPaintComplex(px, x, y, vx - x, h, fullW, fullH);
            divideAndPaintComplex(px, vx + 1, y, x + w - (vx + 1), h, fullW, fullH);
        } else {
            int hy = chooseSplit(y, h);
            drawHLine(px, x, hy, w);
            divideAndPaintComplex(px, x, y, w, hy - y, fullW, fullH);
            divideAndPaintComplex(px, x, hy + 1, w, y + h - (hy + 1), fullW, fullH);
        }
    }

    // ---------- color helpers ----------

    private Color pickBasicColor() {
        int idx = rand.nextInt(BASIC_PALETTE.length);
        return BASIC_PALETTE[idx];
    }

    // Very simple positional bias: upper-left tends warmer, lower-right cooler.
    private Color chooseColor(double nx, double ny) {
        double coolBias = (nx + ny) / 1.2;
        if (coolBias > 1.0) {
            coolBias = 1.0;
        } else if (coolBias < 0.0) {
            coolBias = 0.0;
        }
        double warmBias = 1.0 - coolBias;

        // Weights split between warm (R,Y) and cool (C,W).
        double weightR = warmBias * 0.55;
        double weightY = warmBias * 0.45;
        double weightC = coolBias * 0.55;
        double weightW = coolBias * 0.45;

        double total = weightR + weightY + weightC + weightW;
        double pick = rand.nextDouble() * total;

        if (pick < weightR) {
            return Color.RED;
        }
        pick -= weightR;

        if (pick < weightY) {
            return Color.YELLOW;
        }
        pick -= weightY;

        if (pick < weightC) {
            return Color.CYAN;
        }
        // Remaining range maps to white.
        return Color.WHITE;
    }

    // ---------- drawing utils (plain loops only) ----------

    private void fillSolid(Color[][] px, int x, int y, int w, int h, Color c) {
        int maxY = y + h;
        int maxX = x + w;
        for (int j = y; j < maxY; j++) {
            for (int i = x; i < maxX; i++) {
                px[j][i] = c;
            }
        }
    }

    private void fillRegionWithBorder(Color[][] px, int x, int y, int w, int h, Color c) {
        drawRectBorder(px, x, y, w, h, Color.BLACK);

        int ix = x + BORDER;
        int iy = y + BORDER;
        int iw = w - 2 * BORDER;
        int ih = h - 2 * BORDER;

        if (iw > 0 && ih > 0) {
            fillSolid(px, ix, iy, iw, ih, c);
        }
    }

    private void drawRectBorder(Color[][] px, int x, int y, int w, int h, Color c) {
        int maxX = x + w - 1;
        int maxY = y + h - 1;

        // Top and bottom edges.
        for (int i = x; i <= maxX; i++) {
            px[y][i] = c;
            px[maxY][i] = c;
        }
        // Left and right edges.
        for (int j = y; j <= maxY; j++) {
            px[j][x] = c;
            px[j][maxX] = c;
        }
    }

    private void drawVLine(Color[][] px, int vx, int y, int h) {
        int maxY = y + h;
        for (int j = y; j < maxY; j++) {
            px[j][vx] = Color.BLACK;
        }
    }

    private void drawHLine(Color[][] px, int x, int hy, int w) {
        int maxX = x + w;
        for (int i = x; i < maxX; i++) {
            px[hy][i] = Color.BLACK;
        }
    }

    // Chooses a split index with a simple interior margin to avoid tiny cells.
    private int chooseSplit(int start, int length) {
        int min = start + MIN_CELL;
        int max = start + length - MIN_CELL - 1;

        if (min > max) {
            // Fallback: middle of the segment if it's too small to honor margins.
            return start + (length / 2);
        } else {
            int range = max - min + 1;
            return min + rand.nextInt(range);
        }
    }

    // Validates size and rectangular shape.
    private void checkPixels(Color[][] pixels) {
        if (pixels == null) {
            throw new IllegalArgumentException("pixels must be non-null and at least 300x300");
        }
        if (pixels.length < MIN_CANVAS) {
            throw new IllegalArgumentException("pixels must be non-null and at least 300x300");
        }
        if (pixels[0] == null || pixels[0].length < MIN_CANVAS) {
            throw new IllegalArgumentException("pixels must be non-null and at least 300x300");
        }

        int w = pixels[0].length;
        for (int j = 1; j < pixels.length; j++) {
            if (pixels[j] == null || pixels[j].length != w) {
                throw new IllegalArgumentException("pixels must be rectangular");
            }
        }
    }
}
