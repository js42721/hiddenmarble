package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;

/** 
 * Maze fixture definitions. Fixture dimensions are omitted since all maze
 * fixtures other than the sensor are always 1 x 1 in Box2D meters. The
 * dimensions of the sensor can be deduced from the dimensions of the maze.
 */
public class MazeFixtureDef {
    public final Vector2 center;
    public final boolean isCorner;
    public final boolean isSensor;
    
    public MazeFixtureDef(Vector2 center, boolean isCorner, boolean isSensor) {
        this.center = center;
        this.isCorner = isCorner;
        this.isSensor = isSensor;
    }
}
