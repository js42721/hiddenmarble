package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hiddenmarble.utils.BodyHelper;

/** The default {@code Marble} implementation. */
public class DefaultMarble extends AbstractDynamicEntity
        implements Entity, DynamicEntity, Marble {
    
    private float width;
    
    /**
     * Creates a marble at the specified position.
     * 
     * @param world the Box2D world
     * @param position the world position of the marble
     * @param radius the radius of the marble in Box2D meters
     */
    public DefaultMarble(World world, Vector2 position, float radius) {
        super(BodyHelper.getMarbleBody(world, position, radius));
        width = 2 * radius;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return width;
    }
}
