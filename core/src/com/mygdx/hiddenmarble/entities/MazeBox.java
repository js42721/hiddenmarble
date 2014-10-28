package com.mygdx.hiddenmarble.entities;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.hiddenmarble.maze.Position;

/** A box with a maze inside. */
public interface MazeBox extends Entity {
    /** Returns the world location of the specified tile. */
    Vector2 getTileLocation(Position position);

    /** Returns an unmodifiable list of maze fixture definitions. */
    List<MazeFixtureDef> getMazeFixtureDefs();
}
