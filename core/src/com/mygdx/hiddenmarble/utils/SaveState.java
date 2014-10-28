package com.mygdx.hiddenmarble.utils;

import java.io.IOException;
import java.io.Serializable;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.mygdx.hiddenmarble.world.GameWorld;

/** Saves and loads game data. */
public class SaveState {
    private final Preferences preferences;
    private Data data;

    /** Creates a save state from the specified save file. */
    public SaveState(String filename) {
        preferences = Gdx.app.getPreferences(filename);
        data = new Data();
    }
    
    /**
     * Attempts to load game data from the save file.
     * 
     * @return true if the data was loaded
     */
    public boolean load() {
        String dataString = preferences.getString("dataString", null);
        if (dataString == null) {
            return false;
        }
        try {
            data = (Data)Serialization.fromString(dataString);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /** Saves data to the save file. */
    public void save() {
        try {
            preferences.putString("dataString", Serialization.toString(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        preferences.flush();
    }

    /** Clears all loaded data. */
    public void clear() {
        data = new Data();
    }
    
    /** Erases the save file. */
    public void erase() {
        preferences.clear();
        preferences.flush();
    }
    
    public GameWorld getWorld() {
        return data.world;
    }

    public void setWorld(GameWorld world) {
        data.world = world;
    }
    
    public String getUIState() {
        return data.uiState;
    }
    
    public void setUIState(String state) {
        data.uiState = state;
    }
    
    private static class Data implements Serializable {
        private static final long serialVersionUID = -1210225610405870019L;
        
        GameWorld world;
        String uiState;
    }
}
