package com.mygdx.hiddenmarble.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidGraphics;
import com.mygdx.hiddenmarble.ui.HiddenMarble;

public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
        config.useCompass = false;
        initialize(new HiddenMarble(), config);
        ((AndroidGraphics)getGraphics()).getView().setKeepScreenOn(true);
    }
}
