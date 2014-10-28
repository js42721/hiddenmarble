package com.mygdx.hiddenmarble.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import maze.Position;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.hiddenmarble.utils.BodyHelper;
import com.mygdx.hiddenmarble.utils.MazeHelper.MazeDef;

/** The default {@link MazeBox} implementation. */
public class DefaultMazeBox extends AbstractEntity implements Entity, MazeBox {
    private List<MazeFixtureDef> data;
    private float width;
    private float height;
    
    /**
     * Creates a maze box from the specified tile maze model.
     * 
     * @param world the Box2D world
     * @param mazeDef the maze definition
     * @param exit the position of the maze's exit tile
     */
    public DefaultMazeBox(World world, MazeDef mazeDef) {
        super(BodyHelper.getMazeBoxBody(world, mazeDef));
        width = mazeDef.maze.getWidth();
        height = mazeDef.maze.getHeight();
        data = initData();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public Vector2 getTileLocation(Position position) {
        Vector2 ret = new Vector2(position.getX(), getHeight() - position.getY() - 1);
        Transform transform = getTransform();
        transform.mul(ret);
        return ret;
    }
    
    @Override
    public List<MazeFixtureDef> getMazeFixtureDefs() {
        return Collections.unmodifiableList(data);
    }
    
    private List<MazeFixtureDef> initData() {
        Array<Fixture> fixtures = getBody().getFixtureList();
        List<MazeFixtureDef> ret = new ArrayList<MazeFixtureDef>(fixtures.size);
        for (Fixture f : fixtures) {
            MazeFixtureDef d = (MazeFixtureDef)f.getUserData();
            ret.add(d);
        }
        return ret;
    }
}
