package net.runelite.client.plugins.sandcrabs;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.InventoryCoords;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.sandcrabs.SandcrabsInventoryOverlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;

@PluginDescriptor(
        name = "Sand Crabs",
        description = "You know what it is",
        tags = {"sand", "crabs"}
)
public class SandcrabsPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private ClientThread clientThread;

    @Inject
    private SandcrabsOverlay overlay;

    @Inject
    private SandcrabsInventoryOverlay inventoryOverlay;

    @Inject
    private SandcrabsConfig config;

    @Provides
    SandcrabsConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SandcrabsConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
        overlayManager.add(inventoryOverlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(inventoryOverlay);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        overlay.setPosition(OverlayPosition.valueOf(config.position().toString()));
        overlay.setLayer(OverlayLayer.ABOVE_WIDGETS);
        Rectangle r = InventoryCoords.getItemBounds(client, 4);

//        Point p = SynMouse.moveMouse(client, r);
//        SynMouse.pressMouse(client, p);

//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        Item[] items = inventory.getItems();
//        for (int i = 0; i < items.length; i++) {
//            Item item = items[i];
//            System.out.println("Item id: " + item.getId());
//            System.out.println("Item quantity: " + item.getQuantity());
//        }

        System.out.println(client.getVar(VarClientInt.INVENTORY_TAB) == 3);

//        private boolean isOnMusicTab()
//        {
//            return client.getVar(VarClientInt.INVENTORY_TAB) == 13;
//        }
    }
}
