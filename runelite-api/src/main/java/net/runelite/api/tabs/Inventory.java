package net.runelite.api.tabs;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.syntheticevents.SynMouse;

import java.awt.*;

import static net.runelite.api.coords.InventoryCoords.getItemBounds;

public class Inventory {

    /**
     * Finds and returns the first inventory slot with
     * the id matching the argument id.
     *
     * @return inventory slot 1 - 28
     */
    public static int getFirstSlotWithItem(Item[] items, int id) {
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];

            if (item.getId() == id) {
                return i + 1;
            }
        }

        return -1;
    }

    /**
     * Gets the item at the specified slot
     *
     * @return itemID or -1 if empty
     */
    public static int getItemAtSlot(Item[] items, int slot) {
        if (slot < 1 || slot > 28 || slot > items.length) return -1;

        return items[slot - 1].getId();
    }

    /**
     * @param client client
     * @return true if inventory tab open otherwise false
     */
    public static boolean isOpen(Client client) {
        return client.getVar(VarClientInt.INVENTORY_TAB) == 3;
    }

//    /**
//     * Left clicks on a slot given an item
//     *
//     * @param client
//     * @param item
//     */
//    public static void primaryClickSlot(Client client, Item item) {
//        int slot = getFirstSlotWithItem(client.getItemContainer(InventoryID.INVENTORY).getItems(), item.getId());
//        boolean inventoryOpen = isOpen(client);
//        if (slot == -1 || !inventoryOpen) return;
//
//        primaryClickSlot(client, slot);
//    }
//
//    /**
//     * Left clicks on a slot given a slot #
//     *
//     * @param client
//     * @param slot
//     */
//    public static void primaryClickSlot(Client client, int slot) {
//        boolean inventoryOpen = isOpen(client);
//        if (slot == -1 || !inventoryOpen) return;
//
//        Rectangle bounds = getItemBounds(client, slot);
////        Point itemPoint = SynMouse.moveMouse(client, bounds);
////        SynMouse.click(client, itemPoint, 1);
//    }
}
