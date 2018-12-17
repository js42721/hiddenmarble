package com.mygdx.hiddenmarble.entities;

import java.util.List;

import com.badlogic.gdx.math.Vector2;

import maze.Point;

/** A box with a maze inside. */
public interface MazeBox extends Entity {
    /** Returns the world location of the specified tile. */
    Vector2 getTileLocation(Point position);

    /** Returns an unmodifiable list of maze fixture definitions. */
    List<MazeFixtureDef> getMazeFixtureDefs();
}
