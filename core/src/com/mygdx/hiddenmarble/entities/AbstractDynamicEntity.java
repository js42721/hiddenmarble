package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

/** A game entity with a dynamic Box2D body. */
public abstract class AbstractDynamicEntity extends AbstractEntity 
        implements Entity, DynamicEntity {
    
    /** Creates a dynamic entity with the specified Box2D body. */
    protected AbstractDynamicEntity(Body body) {
        super(body);
    }
    
    @Override
    public final Vector2 getLinearVelocity() {
        return getBody().getLinearVelocity();
    }
}
