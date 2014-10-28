package com.mygdx.hiddenmarble.ui;

import static com.mygdx.hiddenmarble.utils.SpriteHelper.getSprite;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Transform;
import com.mygdx.hiddenmarble.entities.Material;
import com.mygdx.hiddenmarble.entities.MazeFixtureDef;
import com.mygdx.hiddenmarble.utils.Assets;
import com.mygdx.hiddenmarble.world.GameWorld;
import com.mygdx.hiddenmarble.world.GameWorldListener;

/** Renders world objects and plays sounds. */
public class WorldRenderer implements GameWorldListener {
    private final GameWorld world;
    private final Sprite marble;
    private final Sprite wall;
    private final Sprite background;
    private final Sprite corners;
    private final Sprite back;
    private final Sprite cover;
    private final Sprite exitGuide;
    
    private List<Vector2> wallPositions;
    private boolean revealed;
    private boolean drawInnerBox;
    private boolean drawCover;
    private float fadeTimer;
    private float fadeDuration;

    /** Creates a renderer attached to the specified world. */
    public WorldRenderer(GameWorld world) {
        this.world = world;

        float marbleWidth = world.getMarbleWidth();
        float mazeWidth = world.getMazeBoxWidth();
        float mazeHeight = world.getMazeBoxHeight();
        Vector2 exit = world.getMazeExit();

        background = getSprite(Assets.bg, world.getWidth(), world.getHeight());
        marble = getSprite(Assets.marble, marbleWidth, marbleWidth);
        back = getSprite(Assets.back, mazeWidth - 2.0f, mazeHeight);
        corners = getSprite(Assets.corners, mazeWidth, mazeHeight);
        cover = getSprite(Assets.cover, mazeWidth, mazeHeight);
        exitGuide = getSprite(Assets.exit, 1.0f, 1.0f, exit.x, exit.y);
        wall = getSprite(Assets.wall, 1.0f, 1.0f);

        wallPositions = getWallPositions();

        drawInnerBox = false;
        drawCover = true;
    }

    /**
     * Renders sprites.
     * 
     * @param delta time in seconds
     * @param batch the batcher
     */
    public void render(float delta, Batch batch) {
        if (fadeTimer > 0.0f) {
            float progress = fadeTimer / fadeDuration;
            float alpha = revealed ? 1.0f - progress : progress;
            fadeTimer -= delta;
            if (fadeTimer < 0.0f) {
                revealed = !revealed;
                alpha = revealed ? 0.0f : 1.0f;
            }
            setCoverAlpha(alpha);
            drawInnerBox = alpha != 1.0f;
            drawCover = alpha != 0.0f;
        }

        background.draw(batch);

        if (drawInnerBox) {
            renderMazeBox(batch);
            renderMarble(batch);
        }

        if (drawCover) {
            renderCover(batch);
        }
    }

    /**
     * Reveals/conceals the maze by fading the cover in or out. If this is
     * called in the middle of a transition, nothing will happen and this will
     * return false.
     * 
     * @param revealed whether the maze should be revealed
     * @param fadeDuration the duration of the fade animation in seconds
     * @return true if the reveal status will be affected by the call
     * @throws IllegalArgumentException if fadeDuration is negative
     */
    public boolean setRevealed(boolean revealed, float fadeDuration) {
        if (fadeDuration < 0.0f) {
            throw new IllegalArgumentException("Negative fade duration");
        }
        if (fadeTimer > 0.0f || revealed == this.revealed) {
            return false;
        }
        this.fadeDuration = fadeDuration;
        fadeTimer = fadeDuration;
        if (fadeDuration == 0.0f) {
            this.revealed = !this.revealed;
            drawInnerBox = this.revealed;
            drawCover = !this.revealed;
            setCoverAlpha(this.revealed ? 0.0f : 1.0f);
        }
        return true;
    }

    /** Returns true if the maze is revealed. */
    public boolean isRevealed() {
        return revealed;
    }
    
    @Override
    public void marbleHit(float impulse, Material material) {
        Sound toPlay = null;
        switch (material) {
        case GLASS:
            toPlay = Assets.hitGlass;
            break;
        case WOOD:
            toPlay = Assets.hitWood;
            break;
        default:
            return;
        }
        float volume = Math.min(1.0f, impulse * 0.025f);
        toPlay.play(volume);
    }

    @Override
    public void marbleRoll(float len2, Material material) {
        Music toStop = null;
        Music toPlay = null;
        float maxVolume = 0.0f;
        switch (material) {
        case GLASS:
            toStop = Assets.rollWood;
            toPlay = Assets.rollGlass;
            maxVolume = len2 * 0.0005f;
            break;
        case WOOD:
            toStop = Assets.rollGlass;
            toPlay = Assets.rollWood;
            maxVolume = len2 * 0.005f;
            break;
        default:
            return;
        }
        if (toStop.isPlaying()) {
            toStop.pause();
        }
        toPlay.setVolume(Math.min(1.0f, maxVolume));
        toPlay.setLooping(true);
        toPlay.play();
    }
    
    @Override
    public void marbleStop() {
        Assets.rollWood.pause();
        Assets.rollGlass.pause();
    }

    @Override
    public void mazeSolved() {
    }

    private void setCoverAlpha(float alpha) {
        cover.setAlpha(alpha);
        exitGuide.setAlpha(alpha);
    }

    private void renderCover(Batch batch) {
        cover.draw(batch);
        exitGuide.draw(batch);
    }

    private void renderMarble(Batch batch) {
        Vector2 position = world.getMarblePosition();
        marble.setCenter(position.x, position.y);
        marble.draw(batch);
    }

    private void renderMazeBox(Batch batch) {
        corners.draw(batch);
        back.draw(batch);

        for (Vector2 v : wallPositions) {
            wall.setCenter(v.x, v.y);
            wall.draw(batch);
        }
    }

    private List<Vector2> getWallPositions() {
        List<Vector2> ret = new ArrayList<Vector2>();
        Transform transform = world.getMazeTransform();
        for (MazeFixtureDef fd : world.getMazeFixtureDefs()) {
            if (fd.isCorner || fd.isSensor) {
                continue;
            }
            Vector2 v = new Vector2(fd.center);
            transform.mul(v);
            ret.add(v);
        }
        return ret;
    }
}
