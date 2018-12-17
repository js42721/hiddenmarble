package com.mygdx.hiddenmarble.utils;

import java.io.Serializable;

import maze.ImmutablePoint;
import maze.Maze;
import maze.Point;
import maze.RecursiveBacktracker;
import maze.TileMaze;

import com.badlogic.gdx.math.MathUtils;

/**
 * Creates mazes. The mazes will have predetermined exit positions but will vary
 * otherwise.
 */
public final class MazeHelper {
    private static final int SMALL_WIDTH = 4;
    private static final int SMALL_HEIGHT = 5;

    private static final int MEDIUM_WIDTH = 5;
    private static final int MEDIUM_HEIGHT = 6;

    private static final int LARGE_WIDTH = 7;
    private static final int LARGE_HEIGHT = 8;

    private MazeHelper() {
    }

    /**
     * Creates a 4 x 5 maze (9 x 11 in tile representation).
     * 
     * @return maze definition (tile maze object and start/exit positions)
     */
    public static MazeDef getSmallMaze() {
        return getMaze(SMALL_WIDTH, SMALL_HEIGHT);
    }

    /**
     * Creates a 5 x 6 maze (11 x 13 in tile representation).
     * 
     * @return maze definition (tile maze object and start/exit positions)
     */
    public static MazeDef getMediumMaze() {
        return getMaze(MEDIUM_WIDTH, MEDIUM_HEIGHT);
    }

    /**
     * Creates a 7 x 8 maze (15 x 17 in tile representation).
     * 
     * @return maze definition (tile maze object and start/exit positions)
     */
    public static MazeDef getLargeMaze() {
        return getMaze(LARGE_WIDTH, LARGE_HEIGHT);
    }

    /**
     * Creates a maze with the specified dimensions.
     * 
     * @param  width the width of the maze
     * @param  height the height of the maze
     * @return maze definition
     */
    private static MazeDef getMaze(int width, int height) {
        Point start = new ImmutablePoint(MathUtils.random(0, width - 1), 0);
        Point startTile = getTileAt(start);
        Point bottom = new ImmutablePoint(width / 2, height - 1);
        Point bottomTile = getTileAt(bottom);
        Point exitTile = new ImmutablePoint(bottomTile.getX(), bottomTile.getY() + 1);
        Maze maze = new RecursiveBacktracker(width, height);
        maze.generate();
        return new MazeDef(new TileMaze(maze), startTile, exitTile);
    }

    /** Converts a wall-based maze position into a tile-based one. */
    private static Point getTileAt(Point p) {
        return new ImmutablePoint(2 * p.getX() + 1, 2 * p.getY() + 1);
    }

    /** Contains a tile maze object and start/exit positions. */
    public static class MazeDef implements Serializable {
        private static final long serialVersionUID = 7280561842114088170L;
        
        public final TileMaze maze;
        public final Point start;
        public final Point exit;

        public MazeDef(TileMaze maze, Point start, Point exit) {
            this.maze = maze;
            this.start = start;
            this.exit = exit;
        }
    }
}
