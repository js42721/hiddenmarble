package com.mygdx.hiddenmarble.world;

import com.mygdx.hiddenmarble.entities.Material;
import com.mygdx.hiddenmarble.world.GameWorldListener;

/** Adapter class for {@link GameWorldListener}. */
public abstract class GameWorldAdapter implements GameWorldListener {
    @Override
    public void marbleHit(float impulse, Material material) {
    }
    
    @Override
    public void marbleRoll(float len2, Material material) {
    }
    
    @Override
    public void marbleStop() {
    }
    
    @Override
    public void mazeSolved() {
    }
}
