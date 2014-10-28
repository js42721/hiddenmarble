package com.mygdx.hiddenmarble.ui;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.hiddenmarble.utils.Assets;

public class HiddenMarble extends Game {
    /** Horizontal resolution. */
    public static final int WIDTH = 416;
    /** Vertical resolution. */
    public static final int HEIGHT = 624;
    
    /** Box2D meter-to-pixel ratio. */
    public static final float BOX2D_SCALE = 1.0f / 32.0f;
    
    /** Multiply accelerometer reading by this for gravity. */
    public static final float ACCEL_MULTIPLIER = -21.0f;

    /** The location of the save file. */
    public static final String SAVE = ".hidden_marble_save";
    
    Batch batch;
    
    @Override
    public void create() {
        batch = new SpriteBatch();
        Assets.init();
        setScreen(new GameScreen(this));
    }
    
    @Override
    public void dispose() {
        super.dispose();
        getScreen().dispose();
        Assets.dispose();
        batch.dispose();
    }
}
