package net.runelite.api;

import net.runelite.api.coords.LocalPoint;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class SynMouse {
    /*
    TODO:
    1. Have a x% chance to overshoot mouse on target and quickly readjust back to target
        a. If x% chance met, alter target x and y to overshoot
    2. Add noise to each mouse event
    3.

     */

    private static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
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

    public static void moveMouse(Client client, int x, int y) {
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
            long diff = (newTime - time) / factor;
            long timeToMove = 500;


            int pollRate = 1000;
            int currentPoll = 0;
            double msPerPoll = (double)timeToMove / (double) pollRate;

            while (currentPoll < pollRate) {
                newTime = System.nanoTime();
                diff = (newTime - time) / factor;

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

        Thread t = new Thread(r);
        t.start();
    }

    public static Point moveMouse(Client client, Rectangle rect) {
        Point random = getRandomPointInBounds(rect);
        moveMouse(client, random.getX(), random.getY());
        return random;
    }

    public static Point moveMouse(Client client, Actor actor) {
        LocalPoint lp = actor.getLocalLocation();
        Rectangle bounds = actor.getCanvasTilePoly().getBounds();
//        Point canvasPoint = Perspective.localToCanvas(client, lp, client.getPlane());

//        int x = canvasPoint.getX();
//        int y = canvasPoint.getY();
//
//        moveMouse(client, canvas, x, y);
        return moveMouse(client, bounds);
    }

    public static void pressMouse(Client client, Point p) {
        Canvas canvas = client.getCanvas();

        Runnable r = () -> {
            try {
                Thread.sleep((long) getRandomNumber(100, 350));
                long time = System.currentTimeMillis();

                MouseEvent press = new MouseEvent(canvas, MouseEvent.MOUSE_PRESSED, time, MouseEvent.BUTTON1_DOWN_MASK, p.getX(), p.getY(), 1, false, MouseEvent.BUTTON1);
                MouseEvent release = new MouseEvent(canvas, MouseEvent.MOUSE_RELEASED, time, MouseEvent.BUTTON1_DOWN_MASK, p.getX(), p.getY(), 1, false, MouseEvent.BUTTON1);

                canvas.dispatchEvent(press);
                canvas.dispatchEvent(release);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread t = new Thread(r);
        t.start();
    }
}
