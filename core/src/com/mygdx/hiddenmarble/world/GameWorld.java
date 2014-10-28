package com.mygdx.hiddenmarble.world;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.hiddenmarble.entities.DefaultBorders;
import com.mygdx.hiddenmarble.entities.DefaultMarble;
import com.mygdx.hiddenmarble.entities.DefaultMazeBox;
import com.mygdx.hiddenmarble.entities.Entity;
import com.mygdx.hiddenmarble.entities.Marble;
import com.mygdx.hiddenmarble.entities.Material;
import com.mygdx.hiddenmarble.entities.MazeBox;
import com.mygdx.hiddenmarble.entities.MazeFixtureDef;
import com.mygdx.hiddenmarble.utils.MazeHelper;
import com.mygdx.hiddenmarble.utils.MazeHelper.MazeDef;

/** The game model. */
public class GameWorld implements Disposable, Serializable {
    private static final long serialVersionUID = 6734275040761749306L;

    /** Marble radius in Box2D meters. */
    private static final float MARBLE_RADIUS = 0.42f;
    
    /** Box2D velocity threshold for elastic collisions. */
    private static final float VELOCITY_THRESHOLD = 8.0f;
    /** Velocity vector length<sup>2</sup> threshold for the marble roll event. */
    private static final float LEN2_THRESHOLD = 2.1f;
    /** Collision impulse threshold for the marble hit event. */
    private static final float IMPULSE_THRESHOLD = 8.0f;
    
    /** Box2D time step in seconds. */
    private static final float TIME_STEP = 1.0f / 45.0f;
    /** Maximum Box2D time steps between frames. */
    private static final int MAX_STEPS_PER_FRAME = 5;
    /** Box2D velocity constraint solver iterations. */
    private static final int POSITION_ITERATIONS = 3;
    /** Box2D position constraint solver iterations. */
    private static final int VELOCITY_ITERATIONS = 8;

    private Vector2 marblePos;
    private MazeDef mazeDef;
    private float width;
    private float height;
    private boolean inMaze;
    private boolean rolling;
    private boolean solved;
    
    private transient ContactListener contactListener;
    private transient List<GameWorldListener> eventListeners;
    private transient World world;
    private transient Marble marble;
    private transient MazeBox mazeBox;
    private transient float accumulator;

    static {
        /* Stops the marble from shaking when resting against a wall. */
        World.setVelocityThreshold(VELOCITY_THRESHOLD);
    }
    
    /**
     * Constructs a world with the specified dimensions.
     * 
     * @param width the width of the world in Box2D meters
     * @param height the height of the world in Box2D meters
     */
    public GameWorld(float width, float height) {
        init(width, height);
    }
    
    /**
     * Updates the world dimensions.
     * 
     * @param width the width of the world in Box2D meters
     * @param height the height of the world in Box2D meters
     */
    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Registers a world event listener.
     * 
     * @throws NullPointerException if the listener is null
     */
    public void addListener(GameWorldListener listener) {
        if (listener == null) {
            throw new NullPointerException();
        }
        eventListeners.add(listener);
    }
    
    /** Unregisters a world event listener. */
    public void removeListener(GameWorldListener listener) {
        eventListeners.remove(listener);
    }
    
    /**
     * Updates the world.
     * 
     * @param delta time in seconds
     * @param gravity the world's gravity
     */
    public void update(float delta, Vector2 gravity) {
        float len2 = marble.getLinearVelocity().len2();
        if (len2 > LEN2_THRESHOLD) {
            Material material = inMaze ? Material.WOOD : Material.GLASS;
            for (GameWorldListener listener : eventListeners) {
                listener.marbleRoll(len2, material);
            }
            rolling = true;
        } else if (rolling) {
            for (GameWorldListener listener : eventListeners) {
                listener.marbleStop();
            }
            rolling = false;
        }
        
        world.setGravity(gravity);
        step(delta);
    }
    
    /** Returns the width of the world in Box2D meters. */
    public float getWidth() {
        return width;
    }

    /** Returns the height of the world in Box2D meters. */
    public float getHeight() {
        return height;
    }
    
    /** Returns true if the marble is in the maze. */
    public boolean isMarbleInMaze() {
        return inMaze;
    }
    
    /** Returns the marble's start position in world coordinates. */
    public Vector2 getMarbleStart() {
        return mazeBox.getTileLocation(mazeDef.start);
    }
    
    /** Returns the maze's exit position in world coordinates. */
    public Vector2 getMazeExit() {
        return mazeBox.getTileLocation(mazeDef.exit);
    }
    
    /** Returns the marble's current position in world coordinates. */
    public Vector2 getMarblePosition() {
        return marble.getPosition();
    }
    
    /** Returns the width of the marble in Box2D meters. */
    public float getMarbleWidth() {
        return marble.getWidth();
    }
    
    /** Returns the width of the maze box in Box2D meters. */
    public float getMazeBoxWidth() {
        return mazeBox.getWidth();
    }
    
    /** Returns the height of the maze box in Box2D meters. */
    public float getMazeBoxHeight() {
        return mazeBox.getHeight();
    }
    
    /** Returns the body transform of the maze box. */
    public Transform getMazeTransform() {
        return mazeBox.getTransform();
    }
    
    /** Returns an unmodifiable list of maze box fixture definitions. */
    public List<MazeFixtureDef> getMazeFixtureDefs() {
        return mazeBox.getMazeFixtureDefs();
    }
    
    @Override
    public void dispose() {
        world.dispose();
    }
    
    /** Returns true if the specified contact involves a sensor. */
    private boolean checkSensor(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        return a.isSensor() || b.isSensor();
    }
    
    /** Updates the physics engine. */
    private void step(float delta) {
        accumulator += delta;
        
        /* 
         * Slows the physics engine down if the game is suffering from excessive
         * delays. This is done to prevent the physics engine from exacerbating
         * the situation.
         */
        if (accumulator > MAX_STEPS_PER_FRAME * TIME_STEP) {
            accumulator = TIME_STEP;
        }
        
        while (accumulator >= TIME_STEP) {
            world.step(TIME_STEP, VELOCITY_ITERATIONS, POSITION_ITERATIONS);
            accumulator -= TIME_STEP;
        }
    }
    
    private void init(float width, float height) {
        this.width = width;
        this.height = height;
        contactListener = new GameWorldContactListener();
        eventListeners = new ArrayList<GameWorldListener>();
        world = new World(new Vector2(), true);
        world.setContactListener(contactListener);
        new DefaultBorders(world, width, height);
        
        if (mazeDef == null) {
            mazeDef = MazeHelper.getMediumMaze();
        }
        mazeBox = new DefaultMazeBox(world, mazeDef);
        
        if (marblePos == null) {
            marblePos = mazeBox.getTileLocation(mazeDef.start);
        }
        marble = new DefaultMarble(world, marblePos, MARBLE_RADIUS);
    }
    
    /** Custom serialization routine. */
    private void writeObject(ObjectOutputStream out) throws IOException {
        marblePos.set(marble.getPosition()); // Grab the most recent position.
        out.defaultWriteObject(); // Then, write it out with the other data.
    }

    /** Custom deserialization routine. */
    private void readObject(ObjectInputStream in) 
            throws ClassNotFoundException, IOException {
        
        in.defaultReadObject();
        init(width, height);
    }
    
    private class GameWorldContactListener implements ContactListener {
        @Override
        public void beginContact(Contact contact) {
            if (checkSensor(contact)) {
                inMaze = true;
            }
        }

        @Override
        public void endContact(Contact contact) {
            if (checkSensor(contact)) {
                if (!solved) {
                    solved = true;
                    for (GameWorldListener listener : eventListeners) {
                        listener.mazeSolved();
                    }
                }
                
                inMaze = false;
            }
        }

        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {
        }

        @Override
        public void postSolve(Contact contact, ContactImpulse contactImpulse) {
            float impulse = contactImpulse.getNormalImpulses()[0];
            if (impulse > IMPULSE_THRESHOLD) {
                /* 
                 * The marble is the only moving object so one of these
                 * fixtures must belong to the marble.
                 */
                Fixture a = contact.getFixtureA();
                Fixture b = contact.getFixtureB();
                
                /* 
                 * A ClassCastException could be thrown here if the user data
                 * of a body doesn't point to an entity for some reason.
                 */
                Entity entityA = (Entity)a.getBody().getUserData();
                Fixture otherFixture = entityA instanceof DefaultMarble ? b : a;
                Entity otherEntity = (Entity)otherFixture.getBody().getUserData();
                Material material = Material.getType(otherEntity);
                
                /* Marble-hitting-wall event. */
                for (GameWorldListener listener : eventListeners) {
                    listener.marbleHit(impulse, material);
                }
            }
        }
    }
}
