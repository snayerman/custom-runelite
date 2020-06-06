package net.runelite.api.syntheticevents;

import net.runelite.api.Client;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.HashMap;

public class SynKey {
    private static HashMap<String, Integer> map = new HashMap<String, Integer>() {{
        put("INVENTORY", KeyEvent.VK_F1);
        put("PRAYER", KeyEvent.VK_F2);
        put("SPELLS", KeyEvent.VK_F3);
        put("COMBAT", KeyEvent.VK_F4);
        put("EQUIPMENT", KeyEvent.VK_F5);
        put("SKILLS", KeyEvent.VK_F6);
    }};

    public static Runnable openTab(Client client, String tabName) {
        if (!map.containsKey(tabName)) return null;

        Runnable r = () -> {
            Canvas canvas = client.getCanvas();
            int key = map.get(tabName);

            KeyEvent press = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, (char) key);
            KeyEvent release = new KeyEvent(canvas, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, key, (char) key);

            canvas.dispatchEvent(press);
            canvas.dispatchEvent(release);
        };

        return r;
    }
}
