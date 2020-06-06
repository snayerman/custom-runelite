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
    private static final int PADDING = 2;

    public static Rectangle getInventoryBounds(Client client) {
        Canvas c = client.getCanvas();
        int cWidth = c.getWidth() - 1;
        int cHeight = c.getHeight() - 1;

        int tabHeight = 36;

        int inventoryWidth = 203;
        int inventoryHeight = 274;

        int inventoryStartX = cWidth - inventoryWidth + borderWidth;
        int inventoryStartY = cHeight - tabHeight - inventoryHeight + borderHeight;

        return new Rectangle(inventoryStartX, inventoryStartY, inventoryWidth, inventoryHeight);
    }

    // item must be 1 - 28
    public static Rectangle getItemBounds(Client client, int slot) {
        if (slot < 1 || slot > 28) return null;

        Rectangle invBounds = getInventoryBounds(client);

        int itemCol = slot % NUM_COLS == 0 ? 4 : slot % NUM_COLS;
        int itemRow = (int) (slot / (NUM_COLS + 0.0001));

        int startX = (int) invBounds.getX() + itemRowBufferMargin + ((itemCol - 1) * (itemGapMarginX + itemWidth));
        int startY = (int) invBounds.getY() + itemColBufferMargin + (itemRow * (itemGapMarginY + itemHeight));

        return new Rectangle(
                startX + PADDING,
                startY + PADDING,
                itemWidth - PADDING,
                itemHeight - PADDING
        );
    }
}
