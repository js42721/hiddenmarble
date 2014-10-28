package com.mygdx.hiddenmarble.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.hiddenmarble.ui.HiddenMarble;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        config.title = "Hidden Marble";
        config.width = HiddenMarble.WIDTH;
        config.height = HiddenMarble.HEIGHT;
        new LwjglApplication(new HiddenMarble(), config);
    }
}
