package com.mygdx.hiddenmarble.world;

import com.mygdx.hiddenmarble.entities.Material;

/** World event listener. */
public interface GameWorldListener {
    /**
     * Called when the marble hits an object with meaningful force.
     * 
     * @param impulse the impulse value of the collision
     * @param material the surface type of the other object (not null)
     */
    void marbleHit(float impulse, Material material);

    /**
     * Called repeatedly if the marble is rolling at a discernible pace.
     * 
     * @param len2 length<sup>2</sup> of the marble's velocity vector
     * @param material the material the marble is rolling over (not null)
     */
    void marbleRoll(float len2, Material material);

    /** Called when the marble starts slowing down to a halt. */
    void marbleStop();

    /** Called when the marble exits the maze for the first time. */
    void mazeSolved();
}
