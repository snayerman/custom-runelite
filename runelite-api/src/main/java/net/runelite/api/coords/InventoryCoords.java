package net.runelite.api.coords;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.Point;

import java.awt.*;

public class InventoryCoords {
    private static final int NUM_ROWS = 7;
    private static final int NUM_COLS = 4;
    private static final int itemRowBufferMargin = 17;
    private static final int itemColBufferMargin = 9;
    private static final int borderWidth = 6;
    private static final int borderHeight = 6;
    private static final int itemWidth = 31;
    private static final int itemHeight = 31;
    private static final int itemGapMarginX = 11;
    private static final int itemGapMarginY = 5;

    public static Rectangle getInventoryBounds(Client client) {
        Canvas c = client.getCanvas();
        int cWidth = c.getWidth();
        int cHeight = c.getHeight();

        int tabHeight = 35;

        int inventoryWidth = 204;
        int inventoryHeight = 274;

        int inventoryStartX = cWidth - inventoryWidth + borderWidth;
        int inventoryStartY = cHeight - tabHeight - inventoryHeight + borderHeight;

        return new Rectangle(inventoryStartX, inventoryStartY, inventoryWidth, inventoryHeight);
    }

    // item must be 1 - 28
    public static Rectangle getItemBounds(Client client, int item) {
        Rectangle invBounds = getInventoryBounds(client);

        int startX = (int) invBounds.getX();
        int startY = (int) invBounds.getY();

        int col1StartX = startX + itemRowBufferMargin;
        int col1EndX = col1StartX + itemWidth;

        int row1StartY = startY + itemColBufferMargin;
        int row1EndY = row1StartY + itemHeight;

        int itemCol = item % NUM_COLS == 0 ? 4 : item % NUM_COLS;
        int itemRow = item / NUM_ROWS;

        int itemStartX = col1StartX;
        int itemEndX = col1EndX;
        for(int i = 1; i < itemCol; i++) {
            itemStartX = itemEndX + itemGapMarginX;
            itemEndX = itemStartX + itemWidth;
        }

        int itemStartY = row1StartY;
        int itemEndY = row1EndY;
        for(int i = 1; i < itemRow; i++) {
            itemStartY = itemEndY + itemGapMarginY;
            itemEndY = itemStartY + itemHeight;
        }

        return new Rectangle(itemStartX, itemStartY, itemWidth, itemHeight);
    }
}
