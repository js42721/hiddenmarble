package com.mygdx.hiddenmarble.utils;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.hiddenmarble.entities.MazeFixtureDef;
import com.mygdx.hiddenmarble.utils.MazeHelper.MazeDef;

import maze.ImmutablePoint;
import maze.Point;

/** Creates Box2D bodies for the game entities. */
public final class BodyHelper {
    private static final float BORDER_FRICTION = 0.8f;
    private static final float BORDER_RESTITUTION = 0.1f;

    private static final float MARBLE_DENSITY = 1.0f;
    private static final float MARBLE_FRICTION = 0.5f;
    private static final float MARBLE_RESTITUTION = 0.0f;

    private static final float MAZE_BOX_FRICTION = 0.5f;
    private static final float MAZE_BOX_RESTITUTION = 0.1f;

    private BodyHelper() {
    }

    /**
     * Creates a body for the screen borders.
     * 
     * @param  world the Box2D world
     * @param  width the scaled width of the screen
     * @param  height the scaled height of the screen
     * @return a new Box2D body
     */
    public static Body getBordersBody(World world, float width, float height) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(new Vector2(width, height).scl(-0.5f));
        Body body = world.createBody(bodyDef);

        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(0.0f, 0.0f);
        vertices[1] = new Vector2(width, 0.0f);
        vertices[2] = new Vector2(width, height);
        vertices[3] = new Vector2(0.0f, height);

        ChainShape chain = new ChainShape();
        chain.createLoop(vertices);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = chain;
        fixtureDef.friction = BORDER_FRICTION;
        fixtureDef.restitution = BORDER_RESTITUTION;
        
        body.createFixture(fixtureDef);

        chain.dispose();

        return body;
    }

    /**
     * Creates a body for the marble.
     * 
     * @param  world the Box2D world
     * @param  position the body position
     * @param  radius the body radius
     * @return a new Box2D body
     */
    public static Body getMarbleBody(World world, Vector2 position, float radius) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.allowSleep = false;
        bodyDef.position.set(position);

        CircleShape circle = new CircleShape();
        circle.setRadius(radius);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = MARBLE_DENSITY;
        fixtureDef.friction = MARBLE_FRICTION;
        fixtureDef.restitution = MARBLE_RESTITUTION;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        circle.dispose();

        return body;
    }

    /**
     * Creates a body for the maze box.
     * 
     * @param  world the Box2D world
     * @param  mazeDef the maze definition
     * @return a new Box2D body
     */
    public static Body getMazeBoxBody(World world, MazeDef mazeDef) {
        int width = mazeDef.maze.getWidth();
        int height = mazeDef.maze.getHeight();
        
        Vector2 offset = new Vector2(width, height);
        offset.scl(0.5f);
        offset.sub(0.5f, 0.5f);
        
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.sub(offset);
        Body body = world.createBody(bodyDef);

        Map<Point, Fixture> fixtureMap =
                new HashMap<Point, Fixture>(height * width);
        
        /* Sets up the maze wall blocks. */
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                if (mazeDef.maze.isWall(x, y)) {
                    PolygonShape square = new PolygonShape();
                    Vector2 center = new Vector2(x, (height - y - 1));
                    square.setAsBox(0.5f, 0.5f, center, 0.0f);
                    
                    FixtureDef fixtureDef = new FixtureDef();
                    fixtureDef.shape = square;
                    fixtureDef.friction = MAZE_BOX_FRICTION;
                    fixtureDef.restitution = MAZE_BOX_RESTITUTION;
                    
                    Fixture fixture = body.createFixture(fixtureDef);
                    fixture.setUserData(new MazeFixtureDef(center, false, false));
                    
                    fixtureMap.put(new ImmutablePoint(x, y), fixture);
                    
                    square.dispose();
                }
            }
        }
        
        /* Removes the fixture at the exit position. */
        Fixture exitFixture = fixtureMap.get(mazeDef.exit);
        body.destroyFixture(exitFixture);
        
        Point[] corners = new Point[4];
        corners[0] = new ImmutablePoint(0, 0);
        corners[1] = new ImmutablePoint(0, height - 1);
        corners[2] = new ImmutablePoint(width - 1, height - 1);
        corners[3] = new ImmutablePoint(width - 1, 0);
        
        /* Sets up the corners. */
        for (int i = 0; i < corners.length; ++i) {
            Point pos = corners[i];
            Fixture toDestroy = fixtureMap.get(pos);
            Vector2 center = ((MazeFixtureDef)toDestroy.getUserData()).center;
            body.destroyFixture(toDestroy);
            
            PolygonShape corner = createCorner(pos, center, 1.0f, width, height);
            
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = corner;
            fixtureDef.friction = MAZE_BOX_FRICTION;
            fixtureDef.restitution = MAZE_BOX_RESTITUTION;
            
            Fixture fixture = body.createFixture(fixtureDef);
            fixture.setUserData(new MazeFixtureDef(center, true, false));
            
            corner.dispose();
        }
        
        PolygonShape rectangle = new PolygonShape();
        rectangle.setAsBox((width - 2) / 2.0f, (height - 1) / 2.0f, offset, 0.0f);
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = rectangle;

        /* Sets up the sensor area. */
        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setSensor(true);
        fixture.setUserData(new MazeFixtureDef(offset, false, true));
        
        rectangle.dispose();
        
        return body;
    }
    
    /**
     * Creates a rounded corner.
     * 
     * @param  pos the position of the corner in the maze
     * @param  center where the corner block is centered
     * @param  radius the radius of the corner
     * @param  mazeWidth the width of the maze
     * @param  mazeHeight the height of the maze
     * @return a new rounded corner shape
     * @throws IllegalArgumentException if the specified position does not
     *         correspond to a corner of the maze
     */
    private static PolygonShape createCorner(Point pos, Vector2 center,
            float radius, int mazeWidth, int mazeHeight) {
        
        Vector2 circOrigin = new Vector2(center);
        Vector2 rotate = new Vector2();
        float halfRadius = radius / 2.0f;
        
        if (pos.getX() == 0 && pos.getY() == 0) {
            /* Upper left. */
            circOrigin.add(halfRadius, -halfRadius);
            rotate.set(-1.0f, 1.0f);
        } else if (pos.getX() == 0 && pos.getY() == mazeHeight - 1) {
            /* Lower left. */
            circOrigin.add(halfRadius, halfRadius);
            rotate.set(-1.0f, -1.0f);
        } else if (pos.getX() == mazeWidth - 1 && pos.getY() == mazeHeight - 1) {
            /* Lower right. */
            circOrigin.add(-halfRadius, halfRadius);
            rotate.set(1.0f, -1.0f);
        } else if (pos.getX() == mazeWidth - 1 && pos.getY() == 0) {
            /* Upper right. */
            circOrigin.add(-halfRadius, -halfRadius);
            rotate.set(1.0f, 1.0f);
        } else {
            throw new IllegalArgumentException("Not a corner");
        }
        
        int vertexCount = 8; // Maximum is 8.
        Vector2[] vertices = new Vector2[vertexCount];
        vertices[0] = circOrigin;
        for (int i = 0; i < vertexCount - 1; ++i) {
            float angle = (float) i / (vertexCount - 2) * 90.0f * MathUtils.degreesToRadians;
            Vector2 next = new Vector2(radius, radius);
            next.scl(MathUtils.cos(angle), MathUtils.sin(angle));
            next.scl(rotate);
            next.add(circOrigin);
            vertices[i + 1] = next;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(vertices);
        return shape;
    }
}
