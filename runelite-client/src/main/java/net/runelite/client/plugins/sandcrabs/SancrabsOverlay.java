package net.runelite.client.plugins.sandcrabs;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.Path;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.PanelComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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

    private String formatPointStr(String header, Object point) {
        if (point == null) return header+": N/A";

        if (point instanceof LocalPoint) {
            LocalPoint p = (LocalPoint) point;
            return header+": (" + p.getX() + ", " + p.getY() + ")";
        } else if (point instanceof WorldPoint) {
            WorldPoint p = (WorldPoint) point;
            return header+": (" + p.getX() + ", " + p.getY() + ")";
        } else if (point instanceof Point) {
            Point p = (Point) point;
            return header+": (" + p.getX() + ", " + p.getY() + ")";
        }

        return header+": N/A";
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        Player player = client.getLocalPlayer();
        if (player == null) return null;

        Point mp = client.getMouseCanvasPosition();
        WorldPoint wp = player.getWorldLocation();
        LocalPoint playerLP = LocalPoint.fromWorld(client, wp);
        Point playerCanvasPoint = Perspective.localToCanvas(client, playerLP, client.getPlane());
//        WorldPoint dest = new WorldPoint(3189, 3491, 0);
        WorldPoint dest = new WorldPoint(1778, 3497, 0);
        LocalPoint destLP = LocalPoint.fromWorld(client, dest);
        double distance = Path.getDistance(wp, dest);

        int x = mp.getX();
        int y = mp.getY();
        int yOffset = 20;

        graphics.setColor(new Color(0, 56, 124, 150));
        graphics.fillRect(0, yOffset += 20, 300, 180);
        graphics.setFont(client.getCanvas().getFont().deriveFont(16.0f));
        graphics.setColor(new Color(0, 0, 0));

        String distanceStr = "Distance from dest: " + distance;

        graphics.drawString(formatPointStr("Mouse", mp), 10, yOffset += 20);
        graphics.drawString(formatPointStr("Dest LP", destLP), 10, yOffset += 20);
        graphics.drawString(formatPointStr("Player Screen", playerCanvasPoint), 10, yOffset += 20);
        graphics.drawString(formatPointStr("destWP", dest), 10, yOffset += 20);
        graphics.drawString(distanceStr, 10, yOffset += 20);
        if (destLP != null) {
            graphics.drawString(formatPointStr("Dest Screen", Perspective.localToCanvas(client, destLP, client.getPlane())), 10, yOffset += 20);
            graphics.drawString(formatPointStr("Minimap", Perspective.localToMinimap(client, destLP)), 10, yOffset += 20);
        }

        // highlight dest tile
        if (destLP != null) {
            graphics.setColor(Color.RED);
            graphics.fillPolygon(Perspective.getCanvasTilePoly(client, destLP));
        }

        graphics.setColor(Color.WHITE);

        // show mouse
        graphics.drawLine(x, 0, x, client.getCanvasHeight());
        graphics.drawLine(0, y, client.getCanvasWidth(), y);

        ArrayList<Tile> tiles = Path.getPossibleTilesToDest(client, dest);
        if (tiles == null) return null;

        for (Tile tile : tiles) {
            LocalPoint tileLP = tile.getLocalLocation();
            if (tileLP == null) continue;

            Polygon tilePoly = Perspective.getCanvasTilePoly(client, tileLP);
            graphics.drawPolygon(tilePoly);
        }

        return null;
    }
}
