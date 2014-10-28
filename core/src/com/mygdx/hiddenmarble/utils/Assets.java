package com.mygdx.hiddenmarble.utils;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** Manages disposable assets. */
public final class Assets {
    public static Texture bg;
    public static Texture cover;
    public static Texture marble;
    public static Texture wall;
    public static Texture back;
    public static Texture corners;
    public static Texture exit;
    public static Music rollWood;
    public static Music rollGlass;
    public static Sound hitWood;
    public static Sound hitGlass;
    public static Sound magic;
    public static Skin uiSkin;
    
    private static AssetManager manager;
    
    private Assets() {
    }
    
    /** Loads all assets. */
    public static void init() {
        manager = new AssetManager();
        load();
        manager.finishLoading();
        initAssets();
    }
    
    /** Disposes all assets. */
    public static void dispose() {
        if (manager != null) {
            manager.dispose();
        }
    }
    
    /** Stops all sound/music. */
    public static void stopSound() {
        rollWood.stop();
        rollGlass.stop();
        hitWood.stop();
        hitGlass.stop();
        magic.stop();
    }
    
    private static void load() {
        manager.load("data/background.png", Texture.class);
        manager.load("data/cover.png", Texture.class);
        manager.load("data/marble.png", Texture.class);
        manager.load("data/wall.png", Texture.class);
        manager.load("data/back.png", Texture.class);
        manager.load("data/corners.png", Texture.class);
        manager.load("data/exit.png", Texture.class);
        manager.load("data/roll_wood.ogg", Music.class);
        manager.load("data/roll_glass.ogg", Music.class);
        manager.load("data/hit_wood.ogg", Sound.class);
        manager.load("data/hit_glass.ogg", Sound.class);
        manager.load("data/magic.ogg", Sound.class);
        manager.load("data/uiskin.json", Skin.class);
    }
    
    private static void initAssets() {
        bg = manager.get("data/background.png");
        cover = manager.get("data/cover.png");
        marble = manager.get("data/marble.png");
        back = manager.get("data/back.png");
        corners = manager.get("data/corners.png");
        wall = manager.get("data/wall.png");
        exit = manager.get("data/exit.png");
        rollWood = manager.get("data/roll_wood.ogg");
        rollGlass = manager.get("data/roll_glass.ogg");
        hitWood = manager.get("data/hit_wood.ogg");
        hitGlass = manager.get("data/hit_glass.ogg");
        magic = manager.get("data/magic.ogg");
        uiSkin = manager.get("data/uiskin.json");
    }
}
