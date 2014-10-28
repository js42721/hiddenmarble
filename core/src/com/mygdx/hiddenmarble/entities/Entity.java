package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Transform;

/** A game entity. */
public interface Entity {
    /** Returns the width of this entity in Box2D meters. */
    float getWidth();

    /** Returns the height of this entity in Box2D meters. */
    float getHeight();

    /** Returns the angle of this entity in radians. */
    float getAngle();
    
    /** Returns the world position of this entity's body origin. */
    Vector2 getPosition();
    
    /** Returns this entity's body origin transform. */
    Transform getTransform();
}
