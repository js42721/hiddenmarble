package com.mygdx.hiddenmarble.entities;

import java.util.HashMap;
import java.util.Map;

/** Material types for game entities. */
public enum Material {
    GLASS, METAL, WOOD;
    
    private static Map<Class<? extends Entity>, Material> table =
            new HashMap<Class<? extends Entity>, Material>();
    
    static {
        table.put(DefaultBorders.class, GLASS);
        table.put(DefaultMarble.class, METAL);
        table.put(DefaultMazeBox.class, WOOD);
    }

    /** Returns the material type for the specified entity. */
    public static Material getType(Entity entity) {
        return table.get(entity.getClass());
    }
}
