package com.mygdx.hiddenmarble.maze;

import java.io.Serializable;

/** Indicates that a maze position is out of range. */
public class PositionOutOfBoundsException extends RuntimeException
        implements Serializable {
    
    private static final long serialVersionUID = 6118737069416349279L;

    /** Constructs a {@code PositionOutOfBoundsException}. */
    public PositionOutOfBoundsException() {
        super();
    }
    
    /**
     * Constructs a {@code PositionOutOfBoundsException} with the specified
     * message.
     * 
     * @param message the message
     */
    public PositionOutOfBoundsException(String message) {
        super(message);
    }
}
