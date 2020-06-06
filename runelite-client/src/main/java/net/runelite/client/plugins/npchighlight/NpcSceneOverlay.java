/*
 * Copyright (c) 2018, James Swindle <wilingua@gmail.com>
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.npchighlight;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import javax.inject.Inject;
import javax.swing.*;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.util.Text;

public class NpcSceneOverlay extends Overlay
{
	private boolean clicked = false;
	// Anything but white text is quite hard to see since it is drawn on
	// a dark background
	private static final Color TEXT_COLOR = Color.WHITE;

	private static final NumberFormat TIME_LEFT_FORMATTER = DecimalFormat.getInstance(Locale.US);

	static
	{
		((DecimalFormat)TIME_LEFT_FORMATTER).applyPattern("#0.0");
	}

	private final Client client;
//	private final Canvas canvas;
	private int timer = 0;
	private int mouseSpot = 0;
	private boolean mouseAtDest = false;
	private final NpcIndicatorsConfig config;
	private final NpcIndicatorsPlugin plugin;

	@Inject
	NpcSceneOverlay(Client client, NpcIndicatorsConfig config, NpcIndicatorsPlugin plugin)
	{
		this.client = client;
//		this.canvas = client.getCanvas();
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	ArrayList<Point> points = new ArrayList<>();

	@Override
	public Dimension render(Graphics2D graphics)
	{
		Point mp = client.getMouseCanvasPosition();
		points.add(mp);
		points.clear();

		graphics.fillRect(mp.getX() - 4, mp.getY() - 4, 8, 8);

		if (points.size() > 1) {
			for (int i = 0; i < points.size()-1; i++) {
				Point p1 = points.get(i);
				Point p2 = points.get(i+1);

				graphics.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
			}
		}

		if (config.showRespawnTimer())
		{
			plugin.getDeadNpcsToDisplay().forEach((id, npc) -> renderNpcRespawn(npc, graphics));
		}

		for (NPC npc : plugin.getHighlightedNpcs())
		{
			renderNpcOverlay(graphics, npc, config.getHighlightColor());
		}

		return null;
	}

	private void renderNpcRespawn(final MemorizedNpc npc, final Graphics2D graphics)
	{
		if (npc.getPossibleRespawnLocations().isEmpty())
		{
			return;
		}

		final WorldPoint respawnLocation = npc.getPossibleRespawnLocations().get(0);
		final LocalPoint lp = LocalPoint.fromWorld(client, respawnLocation.getX(), respawnLocation.getY());

		if (lp == null)
		{
			return;
		}

		final Color color = config.getHighlightColor();

		final LocalPoint centerLp = new LocalPoint(
			lp.getX() + Perspective.LOCAL_TILE_SIZE * (npc.getNpcSize() - 1) / 2,
			lp.getY() + Perspective.LOCAL_TILE_SIZE * (npc.getNpcSize() - 1) / 2);

		final Polygon poly = Perspective.getCanvasTileAreaPoly(client, centerLp, npc.getNpcSize());

		if (poly != null)
		{
			OverlayUtil.renderPolygon(graphics, poly, color);
		}

		final Instant now = Instant.now();
		final double baseTick = ((npc.getDiedOnTick() + npc.getRespawnTime()) - client.getTickCount()) * (Constants.GAME_TICK_LENGTH / 1000.0);
		final double sinceLast = (now.toEpochMilli() - plugin.getLastTickUpdate().toEpochMilli()) / 1000.0;
		final double timeLeft = Math.max(0.0, baseTick - sinceLast);
		final String timeLeftStr = TIME_LEFT_FORMATTER.format(timeLeft);

		final int textWidth = graphics.getFontMetrics().stringWidth(timeLeftStr);
		final int textHeight = graphics.getFontMetrics().getAscent();

		final Point canvasPoint = Perspective
			.localToCanvas(client, centerLp, respawnLocation.getPlane());

		if (canvasPoint != null)
		{
			final Point canvasCenterPoint = new Point(
				canvasPoint.getX() - textWidth / 2,
				canvasPoint.getY() + textHeight / 2);

			OverlayUtil.renderTextLocation(graphics, canvasCenterPoint, timeLeftStr, TEXT_COLOR);
		}
	}

	private void renderNpcOverlay(Graphics2D graphics, NPC actor, Color color)
	{
		NPCComposition npcComposition = actor.getTransformedComposition();
		if (npcComposition == null || !npcComposition.isInteractible()
			|| (actor.isDead() && !config.highlightDeadNpcs()))
		{
			return;
		}

		switch (config.renderStyle())
		{
			case SOUTH_WEST_TILE:
			{
				int size = npcComposition.getSize();
				LocalPoint localPoint = actor.getLocalLocation();

				int x = localPoint.getX() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);
				int y = localPoint.getY() - ((size - 1) * Perspective.LOCAL_TILE_SIZE / 2);

				Polygon tilePoly = Perspective.getCanvasTilePoly(client, new LocalPoint(x, y));

				renderPoly(graphics, color, tilePoly, false);
				break;
			}
			case TILE:
				int size = npcComposition.getSize();
				LocalPoint lp = actor.getLocalLocation();
				Polygon tilePoly = Perspective.getCanvasTileAreaPoly(client, lp, size);

				renderPoly(graphics, color, tilePoly, false);
				break;

			case HULL:
				Shape objectClickbox = actor.getConvexHull();

				renderPoly(graphics, color, objectClickbox, false);
				break;

			case DOT:
				LocalPoint lp2 = actor.getLocalLocation();
				Polygon tilePoly2 = Perspective.getCanvasTileAreaPoly(client, lp2, npcComposition.getSize());
				Rectangle bounds = tilePoly2.getBounds();
				int centerX = (int)bounds.getCenterX();
				int centerY = (int)bounds.getCenterY();
				Rectangle centroid = new Rectangle(centerX - 2, centerY - 2, 4, 4);

				renderPoly(graphics, color, centroid, true);

//				Canvas canvas = client.getCanvas();
//				if (mouseAtDest) {
//					timer++;
//
//					if (timer >= 100) {
//						timer = 0;
//						mouseAtDest = false;
//					}
//				}
//
//				if (!mouseAtDest && mouseSpot == 0) {
//					SynMouse.moveMouse(client, actor);
//					mouseAtDest = true;
//					mouseSpot = 1;
//				} else if (!mouseAtDest && mouseSpot == 1) {
//					SynMouse.moveMouse(client, 50, 50);
//					mouseAtDest = true;
//					mouseSpot = 2;
//				} else if (!mouseAtDest && mouseSpot == 2) {
//					SynMouse.moveMouse(client, 800, 700);
//					mouseAtDest = true;
//					mouseSpot = 0;
//				}
//				clicked = !clicked;

				break;
		}

		if (config.drawNames() && actor.getName() != null)
		{
			String npcName = Text.removeTags(actor.getName());
			Point textLocation = actor.getCanvasTextLocation(graphics, npcName, actor.getLogicalHeight() + 40);

			if (textLocation != null)
			{
				OverlayUtil.renderTextLocation(graphics, textLocation, npcName, color);
			}
		}
	}

	private void renderPoly(Graphics2D graphics, Color color, Shape polygon, boolean fill)
	{
		if (polygon != null)
		{
			graphics.setColor(color);
			graphics.setStroke(new BasicStroke(2));
			graphics.draw(polygon);

			if (!fill) {
				graphics.setColor(new Color(color.getRed(), color.getGreen(), color.getBlue(), 20));
			}

			graphics.fill(polygon);
		}
	}
}
