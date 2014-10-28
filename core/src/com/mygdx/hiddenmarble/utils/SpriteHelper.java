package com.mygdx.hiddenmarble.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;

/** Creates and initialize sprites. */
public final class SpriteHelper {
    private SpriteHelper() {
    }
    
    /**
     * Creates a sprite and centers it at (0, 0).
     * 
     * @param  texture the texture for the sprite
     * @param  width the width of the sprite
     * @param  height the height of the sprite
     * @return a new sprite
     */
    public static Sprite getSprite(Texture texture, float width, float height) {
        return getSprite(texture, width, height, 0.0f, 0.0f);
    }
    
    /**
     * Creates a sprite and centers it at the specified location.
     * 
     * @param  texture the texture for the sprite
     * @param  width the width of the sprite
     * @param  height the height of the sprite
     * @param  x x-coordinate of the sprite's position
     * @param  y y-coordinate of the sprite's position
     * @return a new sprite
     */
    public static Sprite getSprite(Texture texture, float width, float height,
            float x, float y) {
        
        Sprite sprite = new Sprite(texture);
        sprite.setSize(width, height);
        sprite.setOriginCenter();
        sprite.setCenter(x, y);
        return sprite;
    }    
}
