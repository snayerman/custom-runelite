package net.runelite.api.coords;

import net.runelite.api.Client;
import net.runelite.api.spells.StandardSpell;
import java.awt.*;

public class SpellbookCoords {
    private static final int LEFT_BUFFER = 6;
    private static final int TOP_BUFFER = 1;
    private static final int NUM_ROWS = 10;
    private static final int NUM_COLS = 7;
    private static final int NUM_SPELLS = 70;
    private static final int SPELL_WIDTH = 23;
    private static final int SPELL_HEIGHT = 23;
    private static final int SPELL_MARGIN_X = 3;
    private static final int SPELL_MARGIN_Y = 1;
    private static final int PADDING = 2;

    public static Rectangle getSpellbookBounds(Client client) {
        Rectangle panel = InventoryCoords.getInventoryBounds(client);

        int spellsXStart = (int) panel.getX() + LEFT_BUFFER;
        int spellsXEnd = spellsXStart + NUM_COLS * (SPELL_MARGIN_X + SPELL_WIDTH);

        int spellsYStart = (int) panel.getY() + TOP_BUFFER;
        int spellsYEnd = spellsYStart + NUM_ROWS * (SPELL_MARGIN_Y + SPELL_HEIGHT) - 1;

        return new Rectangle(spellsXStart, spellsYStart, spellsXEnd - spellsXStart, spellsYEnd - spellsYStart);
    }

    // spell position must be 1-70
    public static Rectangle getSpellBounds(Client client, int position) {
        if (position < 1 || position > NUM_SPELLS) return null;

        Rectangle spellbookBounds = getSpellbookBounds(client);

        int spellCol = position % NUM_COLS == 0 ? 7 : position % NUM_COLS;
        int spellRow = (int) Math.ceil((position / (double) NUM_SPELLS) * 10);

        int startX = (int) spellbookBounds.getX() + ((spellCol - 1) * (SPELL_MARGIN_X + SPELL_WIDTH));
        int startY = (int) spellbookBounds.getY() + ((spellRow - 1) * (SPELL_MARGIN_Y + SPELL_HEIGHT));

        return new Rectangle(
                startX + PADDING,
                startY + PADDING,
                SPELL_WIDTH - PADDING,
                SPELL_HEIGHT - PADDING
        );
    }

    public static Rectangle getSpellBounds(Client client, StandardSpell spell) {
        return getSpellBounds(client, spell.getPosition());
    }
}
