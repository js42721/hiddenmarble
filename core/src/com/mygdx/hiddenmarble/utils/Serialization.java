package com.mygdx.hiddenmarble.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.badlogic.gdx.utils.Base64Coder;

/** Serialization/deserialization methods. */
public final class Serialization {
    private Serialization() {
    }
    
    /**
     * Deserializes an object from a Base64-encoded string.
     * 
     * @param  s the string from which the object is to be deserialized from
     * @return the deserialized object
     * @throws ClassNotFoundException if the class of the object cannot be found
     * @throws IOException if an I/O error occurs
     */
    public static Object fromString(String s)
            throws ClassNotFoundException, IOException {
        
        byte[] bytes = Base64Coder.decode(s);
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = null; 
        Object o = null;
        try {
            objectInputStream = new ObjectInputStream(byteInputStream);
            o  = objectInputStream.readObject();
        } finally {
            if (objectInputStream != null) {
                objectInputStream.close();
            }
        }
        return o;
    }

    /**
     * Serializes an object and converts the resulting byte array into a
     * Base64-encoded string.
     *  
     * @param  o the object to be serialized
     * @return the serialized object as a string
     * @throws IOException if an I/O error occurs
     */
    public static String toString(Object o) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteOutputStream);
            objectOutputStream.writeObject(o);
        } finally {
            if (objectOutputStream != null) {
                objectOutputStream.close();
            }
        }
        byte[] bytes = byteOutputStream.toByteArray();
        return new String(Base64Coder.encode(bytes));
    }
}
