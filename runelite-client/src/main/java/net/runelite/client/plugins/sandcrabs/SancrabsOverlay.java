package net.runelite.client.plugins.sandcrabs;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;

class SandcrabsOverlay extends Overlay {
    private final Client client;
    private final SandcrabsConfig config;
    private final SandcrabsPlugin plugin;
    private final PanelComponent panelComponent = new PanelComponent();

    @Inject
    private SandcrabsOverlay(Client client, SandcrabsConfig config, SandcrabsPlugin plugin, ItemManager itemManager)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.client = client;
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Point mp = client.getMouseCanvasPosition();
//        Player player = client.getLocalPlayer();
//        WorldPoint lp = player.getWorldLocation();
//        ItemContainer invContainer = client.getItemContainer(InventoryID.INVENTORY);

        int x = mp.getX();
        int y = mp.getY();

        Rectangle r = graphics.getClipBounds();
        graphics.setColor(new Color(0, 56, 124, 150));
        graphics.fillRect(0, 20, 400, 100);

        graphics.setColor(new Color(0, 0, 0));
        String mouseXStr = "Mouse (RL) X: " + x;
        String mouseYStr = "Mouse (RL) Y: " + y;

//        String lpX = "Mouse (AWT) X: " + mp2.getX();
//        String lpY = "Mouse (AWT) Y: " + mp2.getY();

        graphics.drawString(mouseXStr, 10, 40);
        graphics.drawString(mouseYStr, 10, 60);
//        graphics.drawString(lpX, 10, 80);
//        graphics.drawString(lpY, 10, 100);

        // show mouse
        graphics.setColor(Color.WHITE);
        graphics.drawLine(x, 0, x, client.getCanvasHeight());
        graphics.drawLine(0, y, client.getCanvasWidth(), y);

        return null;
    }
}
