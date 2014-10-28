package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;

/** A moving game entity. */
public interface DynamicEntity extends Entity {
    /** Returns the linear velocity vector of this entity's center of mass. */
    Vector2 getLinearVelocity();
}
