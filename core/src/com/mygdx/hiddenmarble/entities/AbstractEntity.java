package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Transform;

/** A game entity with a Box2D body. */
public abstract class AbstractEntity implements Entity {
    private Body body;
    
    /**
     * Creates an entity with the specified Box2D body and gives the body a
     * reference to the entity (stored in the user data field).
     */
    protected AbstractEntity(Body body) {
        this.body = body;
        body.setUserData(this);
    }

    @Override
    public final float getAngle() {
        return body.getAngle();
    }
    
    @Override
    public final Vector2 getPosition() {
        return body.getPosition();
    }
    
    @Override
    public final Transform getTransform() {
        return body.getTransform();
    }
    
    protected final void setPosition(Vector2 position) {
        body.setTransform(position, body.getAngle());
    }
    
    protected final Body getBody() {
        return body;
    }
} 
