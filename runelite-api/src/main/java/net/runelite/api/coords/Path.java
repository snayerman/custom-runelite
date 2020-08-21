package net.runelite.api.coords;

// Need to check if destination click will be interering with widget like minimap

import net.runelite.api.*;
import net.runelite.api.Point;

import java.awt.*;
import java.util.ArrayList;

// Create method to select random tile from range of tiles
public class Path {
    public static double getSlope(WorldPoint p1, WorldPoint p2) {
        return ((double) (p2.getY() - p1.getY())) / ((double) (p2.getX() - p1.getX()));
    }

    public static double getDistance(WorldPoint p1, WorldPoint p2) {
        double y1 = p1.getY();
        double y2 = p2.getY();
        double x1 = p1.getX();
        double x2 = p2.getX();

        return Math.sqrt(Math.pow(y2 - y1, 2) + Math.pow(x2 - x1, 2));
    }

    // Skip the rows that are to the right/left of the player/dest
    private static boolean tileNotInPathX(
        WorldPoint destination,
        WorldPoint player,
        LocalPoint playerLocal,
        LocalPoint destinationLocal,
        int row
    ) {
        if (playerLocal == null || destinationLocal == null) return true;

        // If negative, then destination is to the left of player.
        // If positive, then destination is to the right of player.
        double xDir = destination.getX() - player.getX() + 0.001;
        xDir /= Math.abs(xDir);

        int playerSceneX = playerLocal.getSceneX();

        boolean leftOrRightOfPlayer = (xDir > 0 && row < playerSceneX) || (xDir < 0 && row > playerSceneX);
        boolean leftOrRightOfDestination = (xDir > 0 && row > destinationLocal.getSceneX())
                || (xDir < 0 && row < destinationLocal.getSceneX());

        return leftOrRightOfPlayer || leftOrRightOfDestination;
    }

    // Skip the cols above/below player and destination
    private static boolean tileNotInPathY(
            WorldPoint destination,
            WorldPoint player,
            LocalPoint playerLocal,
            LocalPoint destinationLocal,
            int col
    ) {
        if (playerLocal == null || destinationLocal == null) return true;

        // If negative, then destination is below the player.
        // If positive, then destination is above the player.
        double yDir = destination.getY() - player.getY() + 0.001;
        yDir /= Math.abs(yDir);

        int playerSceneY = playerLocal.getSceneY();

        boolean aboveOrBelowOfPlayer = (yDir > 0 && col < playerSceneY) || (yDir < 0 && col > playerSceneY);
        boolean aboveOrBelowOfDestination = (yDir > 0 && col > destinationLocal.getSceneY())
                || (yDir < 0 && col < destinationLocal.getSceneY());

        return aboveOrBelowOfPlayer || aboveOrBelowOfDestination;
    }

    private static boolean tileNotInPath(
            WorldPoint destination,
            WorldPoint player,
            LocalPoint playerLocal,
            LocalPoint destinationLocal,
            Tile tile
    ) {
//        if (playerLocal == null || destinationLocal == null) return true;
        if (playerLocal == null) return true;

        LocalPoint tileLP = tile.getLocalLocation();
        if (tileLP == null) return true;

        int row = tileLP.getSceneX();
        int col = tileLP.getSceneY();

        // If negative, then destination is to the left of player.
        // If positive, then destination is to the right of player.
        double xDir = destination.getX() - player.getX() + 0.001;
        xDir /= Math.abs(xDir);

        // If negative, then destination is below the player.
        // If positive, then destination is above the player.
        double yDir = destination.getY() - player.getY() + 0.001;
        yDir /= Math.abs(yDir);

        int playerSceneX = playerLocal.getSceneX();
        int playerSceneY = playerLocal.getSceneY();

        boolean leftOrRightOfPlayer = (xDir > 0 && row < playerSceneX) || (xDir < 0 && row > playerSceneX);
        boolean leftOrRightOfDestination = destinationLocal != null && ((xDir > 0 && row > destinationLocal.getSceneX())
                || (xDir < 0 && row < destinationLocal.getSceneX()));

        boolean aboveOrBelowOfPlayer = (yDir > 0 && col < playerSceneY) || (yDir < 0 && col > playerSceneY);
        boolean aboveOrBelowOfDestination = destinationLocal != null && ((yDir > 0 && col > destinationLocal.getSceneY())
                || (yDir < 0 && col < destinationLocal.getSceneY()));

        return leftOrRightOfPlayer || leftOrRightOfDestination || aboveOrBelowOfPlayer || aboveOrBelowOfDestination;
    }

    public static ArrayList<Tile> getPossibleTilesToDest(Client client, WorldPoint dest) {
        Player player = client.getLocalPlayer();
        if (player == null) return null;

        WorldPoint wp = player.getWorldLocation();
        LocalPoint playerLP = LocalPoint.fromWorld(client, wp);
        LocalPoint destLP = LocalPoint.fromWorld(client, dest);

        Scene scene = client.getScene();
        Tile[][][] tiles = scene.getTiles();
        int z = client.getPlane();

        // Vector stuff
        double slope = getSlope(wp, dest);

        ArrayList<WorldPoint> pointsOnPath = new ArrayList<>();
        ArrayList<GameObject> gameObjects = new ArrayList<>();

        for (int row = 0; row < Constants.SCENE_SIZE; ++row) {
//            if (tileNotInPathX(dest, wp, playerLP, destLP, row)) {
//                System.out.println("Tile not in path X");
//                continue;
//            }

            for (int col = 0; col < Constants.SCENE_SIZE; ++col) {
//                if (tileNotInPathY(dest, wp, playerLP, destLP, col)) continue;

                Tile tile = tiles[z][row][col];
                if (tile == null || tileNotInPath(dest, wp, playerLP, destLP, tile)) continue;

                // filter out tiles not along slope from loc -> dest
                WorldPoint tWP = tile.getWorldLocation();
                double slopePlayerToTile = getSlope(wp, tWP);

                // Skip tile if not within line of sight (slope)
                if (Math.abs(slopePlayerToTile - slope) > 0.1) continue;

                // TODO: Calc how many tiles should be shown
                if (wp.distanceTo(tWP) < 32 && !pointsOnPath.contains(tWP)) {
                    GameObject object = tile.getGameObjects()[0];

                    if (object == null)
                        pointsOnPath.add(tWP);
                    else if (!gameObjects.contains(object))
                        gameObjects.add(object);
                }
            }
        }

        return pruneObstructedTiles(client, pointsOnPath, gameObjects, tiles[z]);
    }

    private static ArrayList<Tile> pruneObstructedTiles(
        Client client,
        ArrayList<WorldPoint> pointsOnPath,
        ArrayList<GameObject> gameObjects,
        Tile[][] tiles
    ) {
        ArrayList<Tile> goodTiles = new ArrayList<>();
        ArrayList<Tile> intersectingTiles = new ArrayList<>();

        for (WorldPoint pathWP : pointsOnPath) {
            LocalPoint pathLP = LocalPoint.fromWorld(client, pathWP);
            if (pathLP == null) continue;

            // construct rectangle shape to check intersection with game objects
            Polygon tilePoly = Perspective.getCanvasTilePoly(client, pathLP);
            if (tilePoly == null) continue;

            boolean intersects = false;

            for (GameObject o : gameObjects) {
                Shape shape = o.getConvexHull();
                if (shape == null) continue;

                Rectangle objRect = shape.getBounds();

                /*
                  TODO: Add the tiles that get blocked by objects to a new list
                        that will be the fallback in case no other tiles exist
                 */
                if (objRect.intersects(tilePoly.getBounds())) {
//                if (objRect.contains(new java.awt.Point(pathLP.getX(), pathLP.getY()))) {
                    intersects = true;
                    break;
                }
            }

            Tile tile = tiles[pathLP.getSceneX()][pathLP.getSceneY()];
            if (!intersects) goodTiles.add(tile);
            else intersectingTiles.add(tile);
        }
        return goodTiles;
    }
}
