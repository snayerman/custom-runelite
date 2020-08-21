package net.runelite.client.plugins.sandcrabs;

import com.google.inject.Provides;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.InventoryCoords;
import net.runelite.api.coords.SpellbookCoords;
import net.runelite.api.events.GameTick;
import net.runelite.api.spells.StandardSpell;
import net.runelite.api.syntheticevents.SynEventExecutor;
import net.runelite.api.syntheticevents.SynKey;
import net.runelite.api.syntheticevents.SynMouse;
import net.runelite.api.tabs.Inventory;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.http.api.RuneLiteAPI;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;

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

    @Getter(AccessLevel.PACKAGE)
    private SynEventExecutor executor;

    @Getter(AccessLevel.PACKAGE)
    private int idleTicks = 0;

    @Getter(AccessLevel.PACKAGE)
    private boolean scriptStarted;

    @Getter(AccessLevel.PACKAGE)
    private int kills;

    @Getter(AccessLevel.PACKAGE)
    private ArrayList<NPC> npcsInteractingWithMe;

    private ScheduledFuture<?> moveMouseSpellTask;
    private ScheduledFuture<?> moveMouseItemTask;
    private ScheduledFuture<?> clickSpellTask;
    private ScheduledFuture<?> clickItemTask;

    @Override
    protected void startUp() throws Exception
    {
        executor = new SynEventExecutor();
        overlayManager.add(overlay);
        overlayManager.add(inventoryOverlay);

//        moveMouseSpellTask = null;
//        moveMouseItemTask = null;
//        clickItemTask = null;
//        clickSpellTask = null;

        scriptStarted = false;
        kills = 0;
        npcsInteractingWithMe = new ArrayList<>();

        /**
         * Open up proper tab and do logic checking
         * so that onGameTick can assume ready for work
         */

    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
        overlayManager.remove(inventoryOverlay);
        executor.shutdown();
    }

    private void moveToSpell(int position) {
        Rectangle r = SpellbookCoords.getSpellBounds(client, position);
        moveMouseSpellTask = executor.scheduleTask(SynMouse.moveMouse(client, r), 0);
    }

    private void moveToItem(int position) {
        Rectangle r = InventoryCoords.getItemBounds(client, position);
        moveMouseItemTask = executor.scheduleTask(SynMouse.moveMouse(client, r), 0);
    }

    private void clickSpell() {
        clickSpellTask = executor.scheduleTask(SynMouse.click(client, 1), 0);
    }

    private void clickItem() {
        clickItemTask = executor.scheduleTask(SynMouse.click(client, 1), 0);
    }

    private void updateListOfNpcs(Player player, NPC npc) {
        boolean npcInList = false;

        for (NPC sandCrab : npcsInteractingWithMe) {
            // The actor should be the LocalPlayer
            Actor sandCrabEnemy = sandCrab.getInteracting();

            // If the current NPC we're fighting is already in the list,
            // set npcInList to true so we don't re-add him
            if (npc != null && npc.getIndex() == sandCrab.getIndex())
                npcInList = true;

            // If sandCrab is dead, increment kill count and remove from list of
            // currently interacting NPCs and break loop
            if (sandCrab.isDead()) {
                kills++;
                npcsInteractingWithMe.remove(sandCrab);
                break;
            }

            // If the current sandCrabEnemy is null (either dead or something happened) or
            // the sandCrab interacting actor is not us, remove from list
            if (sandCrabEnemy == null || (!sandCrabEnemy.getName().equals(player.getName()))) {
                npcsInteractingWithMe.remove(sandCrab);
            }
        }

        // Add npc to list if its a sand crab
        if (!npcInList && npc != null && npc.getName().equals("Sand Crab")) npcsInteractingWithMe.add(npc);
    }

    private void clearNpcs() {
        for (NPC npc : npcsInteractingWithMe) {
            npcsInteractingWithMe.remove(npc);
        }
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Player player = client.getLocalPlayer();

        if (player == null) return;

        NPC interactingNpc = (NPC) player.getInteracting();
        int animation = player.getAnimation();

        if (scriptStarted) {
            updateListOfNpcs(player, interactingNpc);

            if (animation == AnimationID.IDLE) {
                idleTicks++;

//            if (idleTicks > 10) resetAggro();
            } else if (npcsInteractingWithMe.size() == 0) {
//                resetAggro();
            }
        } else {
            clearNpcs();
        }

        /*// Moving mouse to spell
        if (moveMouseSpellTask != null) {
//            System.out.println("Waiting to move mouse...");
            if (moveMouseSpellTask.isDone()) {
                moveMouseSpellTask = null;
                clickSpell();
            }
        }

        // Waiting for spell to be clicked
        if (moveMouseSpellTask == null && clickSpellTask != null) {
//            System.out.println("Waiting to click spell...");
            if (clickSpellTask.isDone() && inventoryOpen) {
                clickSpellTask = null;
                moveToItem(12);
            }
        }

        // Waiting for mouse to move to item
        if (moveMouseItemTask != null) {
            if (moveMouseItemTask.isDone()) {
                moveMouseItemTask = null;
                clickItem();
            }
        }

        // Wait for item to be clicked and then schedule next spell
        if (clickItemTask != null && clickItemTask.isDone() && !inventoryOpen) {
            clickItemTask = null;
            moveToSpell(StandardSpell.HIGH_LEVEL_ALCHEMY.getPosition());
        }*/
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged event) {
        overlay.setPosition(OverlayPosition.valueOf(config.position().toString()));
        overlay.setLayer(OverlayLayer.ABOVE_WIDGETS);
        scriptStarted = config.enabled();

        Player player = client.getLocalPlayer();
//        Actor a = player.getInteracting();
//        a.
//        System.out.println(a.getName() + "\n");

//        Widget inv = client.getWidget(WidgetInfo.TO_GROUP(9764864), WidgetInfo.TO_CHILD(9764864));

//        if (moveMouseSpellTask == null) moveToSpell(StandardSpell.HIGH_LEVEL_ALCHEMY.getPosition());

//        System.out.println(InventoryCoords.getItemBounds(client, 4));
//        Rectangle r = SpellbookCoords.getSpellBounds(client, 1);
//        System.out.println(r);
//        Rectangle r = InventoryCoords.getItemBounds(client, 4);
//        Point p = SynMouse.moveMouse(client, r);

//        executor.scheduleTask(SynMouse.moveMouse(client, r), 1000);
//        executor.scheduleTask(SynMouse.moveMouse(client, r), 5000);
//        executor.scheduleTask(SynMouse.moveMouse(client, r), 0);


//        ItemContainer inventory = client.getItemContainer(InventoryID.INVENTORY);
//        Item[] items = inventory.getItems();
//        for (int i = 0; i < items.length; i++) {
//            Item item = items[i];
//            System.out.println("Item id: " + item.getId());
//            System.out.println("Item quantity: " + item.getQuantity());
//        }
    }
}
