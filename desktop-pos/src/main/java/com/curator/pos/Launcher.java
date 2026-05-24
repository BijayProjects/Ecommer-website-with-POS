package com.curator.pos;

/**
 * Launcher bootstrap class.
 * Required to start the JavaFX Application from a shaded uber-jar
 * without a module-info.java descriptor.
 */
public class Launcher {
    public static void main(String[] args) {
        Main.main(args);
    }
}
