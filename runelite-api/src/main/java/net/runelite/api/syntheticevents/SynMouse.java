package net.runelite.api.syntheticevents;

import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;

import java.awt.*;
import java.awt.event.MouseEvent;

public class SynMouse {

    /**
     * Following methods set and get the duration
     * seed used for SynMouse delays.
     */
    private static long duration = 200;

    public static void setDuration(long newDuration) {
        duration = newDuration;
    }

    public static long getDuration() { return duration; }

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min + 1)) + min);
    }

    private static Point getRandomPointInBounds(Rectangle r) {
        int rx = (int) r.getX();
        int ry = (int) r.getY();
        int rw = (int) r.getWidth();
        int rh = (int) r.getHeight();

        int x = getRandomNumber(rx, rx + rw);
        int y = getRandomNumber(ry, ry + rh);

        return new Point(x, y);
    }

    public static Runnable moveMouse(Client client, int x, int y) {
        Canvas canvas = client.getCanvas();

        Runnable r = () -> {
            Point mp = client.getMouseCanvasPosition();
            int mpx = mp.getX();
            int mpy = mp.getY();

            // negative value implies target is to left of mouse
            int dx = x - mpx;
            int xDir = dx < 0 ? -1 : 1;

            // negative value implies target is above mouse
            int dy = y - mpy;
            int yDir = dy < 0 ? -1 : 1;

            long time = System.nanoTime();
            long newTime = System.nanoTime();
            long factor = 10000;
            long nanoToMs = 100;
            long timeToMove = getRandomNumber((int) Math.max(0, duration - 200), (int) (duration + 200));

            int pollRate = 1000;
            int currentPoll = 0;
            double msPerPoll = (double)timeToMove / (double) pollRate;

            while (currentPoll < pollRate) {
                newTime = System.nanoTime();
                long diff = (newTime - time) / factor;

                double percentLeft = (double) currentPoll / (double) pollRate;

                int offsetX = (int) (Math.abs(dx * percentLeft) * xDir);
                int offsetY = (int) (Math.abs(dy * percentLeft) * yDir);
                
                dx = x - mp.getX();
                dy = y - mp.getY();

                if ((diff % (msPerPoll * nanoToMs)) < 3) {
                    currentPoll++;

                    MouseEvent move = new MouseEvent(
                            canvas,
                            MouseEvent.MOUSE_MOVED,
                            newTime,
                            0,
                            mpx + offsetX,
                            mpy + offsetY,
                            0,
                            false
                    );
                    canvas.dispatchEvent(move);
                }
            }
        };

        return r;
    }

    public static Runnable moveMouse(Client client, Rectangle rect) {
        Point random = getRandomPointInBounds(rect);
        return moveMouse(client, random.getX(), random.getY());
    }

    public static Runnable moveMouse(Client client, Actor actor) {
        LocalPoint lp = actor.getLocalLocation();
        Rectangle bounds = actor.getCanvasTilePoly().getBounds();

        return moveMouse(client, bounds);
    }

    public static Runnable click(Client client, int numTimes) {
        Canvas canvas = client.getCanvas();
        Point p = client.getMouseCanvasPosition();

        Runnable r = () -> {
            try {
                for (int i = 0; i < numTimes; i++) {
                    if (i > 0) Thread.sleep(getRandomNumber(150, 250));

                    long time = System.currentTimeMillis();

                    MouseEvent press = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, time, MouseEvent.BUTTON1_DOWN_MASK, p.getX(), p.getY(), 1, false, MouseEvent.BUTTON1);
                    MouseEvent release = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, time, MouseEvent.BUTTON1_DOWN_MASK, p.getX(), p.getY(), 1, false, MouseEvent.BUTTON1);

                    canvas.dispatchEvent(press);
                    canvas.dispatchEvent(release);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        return r;
    }
}
