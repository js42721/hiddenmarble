package com.mygdx.hiddenmarble.utils;

import java.io.Serializable;

import com.badlogic.gdx.math.MathUtils;
import com.mygdx.hiddenmarble.maze.ImmutablePosition;
import com.mygdx.hiddenmarble.maze.Maze;
import com.mygdx.hiddenmarble.maze.Position;
import com.mygdx.hiddenmarble.maze.RecursiveBacktracker;
import com.mygdx.hiddenmarble.maze.TileMaze;

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
        Position start = new ImmutablePosition(MathUtils.random(0, width - 1), 0);
        Position startTile = getTileAt(start);
        Position bottom = new ImmutablePosition(width / 2, height - 1);
        Position bottomTile = getTileAt(bottom);
        Position exitTile = new ImmutablePosition(bottomTile.getX(), bottomTile.getY() + 1);
        Maze maze = new RecursiveBacktracker(width, height);
        maze.generate();
        return new MazeDef(new TileMaze(maze), startTile, exitTile);
    }

    /** Converts a wall-based maze position into a tile-based one. */
    private static Position getTileAt(Position p) {
        return new ImmutablePosition(p.getX() * 2 + 1, p.getY() * 2 + 1);
    }

    /** Contains a tile maze object and start/exit positions. */
    public static class MazeDef implements Serializable {
        private static final long serialVersionUID = -5713646412372779031L;
        
        public final TileMaze maze;
        public final Position start;
        public final Position exit;

        public MazeDef(TileMaze maze, Position start, Position exit) {
            this.maze = maze;
            this.start = start;
            this.exit = exit;
        }
    }
}
