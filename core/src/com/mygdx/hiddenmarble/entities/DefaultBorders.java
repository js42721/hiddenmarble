package com.mygdx.hiddenmarble.entities;

import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hiddenmarble.utils.BodyHelper;

/** Rectangular world borders. */
public class DefaultBorders extends AbstractEntity implements Entity, Borders {
    private float width;
    private float height;
    
    /**
     * Creates the world borders.
     * 
     * @param world the Box2D world
     * @param width the width of the borders in Box2D meters
     * @param height the height of the borders in Box2D meters
     */
    public DefaultBorders(World world, float width, float height) {
        super(BodyHelper.getBordersBody(world, width, height));
        this.width = width;
        this.height = height;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }
}
