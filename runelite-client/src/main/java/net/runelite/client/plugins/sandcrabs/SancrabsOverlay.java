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
        Player player = client.getLocalPlayer();
        WorldPoint lp = player.getWorldLocation();
        ItemContainer invContainer = client.getItemContainer(InventoryID.INVENTORY);

        graphics.fillRect(mp.getX() - 4, mp.getY() - 4, 8, 8);

        Rectangle r = graphics.getClipBounds();
//        System.out.println("bounds x: " + r.getX() + ", y: " + r.getY() + ", width: " + r.getWidth() + ", height: " + r.getHeight());
        graphics.setColor(new Color(0, 56, 124, 150));
        graphics.fillRect(0, 0, 400, 100);

        graphics.setColor(new Color(0, 0, 0));
        String mouseXStr = "Mouse X: " + mp.getX();
        String mouseYStr = "Mouse Y: " + mp.getY();

        String lpX = "World X: " + lp.getX();
        String lpY = "World Y: " + lp.getY();

        graphics.drawString(mouseXStr, 10, 20);
        graphics.drawString(mouseYStr, 10, 40);
        graphics.drawString(lpX, 10, 60);
        graphics.drawString(lpY, 10, 80);

        return null;
    }
}
