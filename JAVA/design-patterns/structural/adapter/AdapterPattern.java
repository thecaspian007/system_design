package com.systemdesign.designpatterns.structural.adapter;

/**
 * Adapter Pattern Implementation
 * 
 * The Adapter pattern allows objects with incompatible interfaces to work together.
 * It acts as a wrapper between two objects, catching calls for one object and
 * transforming them to format and interface recognizable by the second object.
 */

// Target interface that the client expects
interface MediaPlayer {
    void play(String audioType, String fileName);
}

// Adaptee - class with incompatible interface
class AdvancedMediaPlayer {
    public void playVlc(String fileName) {
        System.out.println("Playing vlc file: " + fileName);
    }
    
    public void playMp4(String fileName) {
        System.out.println("Playing mp4 file: " + fileName);
    }
}

// Adapter class implementing the target interface
class MediaAdapter implements MediaPlayer {
    private AdvancedMediaPlayer advancedPlayer;
    
    public MediaAdapter(String audioType) {
        if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer = new AdvancedMediaPlayer();
        }
    }
    
    @Override
    public void play(String audioType, String fileName) {
        if (audioType.equalsIgnoreCase("vlc")) {
            advancedPlayer.playVlc(fileName);
        } else if (audioType.equalsIgnoreCase("mp4")) {
            advancedPlayer.playMp4(fileName);
        }
    }
}

// Client class
class AudioPlayer implements MediaPlayer {
    private MediaAdapter mediaAdapter;
    
    @Override
    public void play(String audioType, String fileName) {
        // Built-in support for mp3 files
        if (audioType.equalsIgnoreCase("mp3")) {
            System.out.println("Playing mp3 file: " + fileName);
        } 
        // Using adapter for other formats
        else if (audioType.equalsIgnoreCase("vlc") || audioType.equalsIgnoreCase("mp4")) {
            mediaAdapter = new MediaAdapter(audioType);
            mediaAdapter.play(audioType, fileName);
        } else {
            System.out.println("Invalid media. " + audioType + " format not supported");
        }
    }
}

// Another example - Database Connection Adapter
interface DatabaseTarget {
    void connect();
    void executeQuery(String query);
    void disconnect();
}

// Legacy database class
class LegacyDatabase {
    public void establishConnection() {
        System.out.println("Legacy database connection established");
    }
    
    public void runQuery(String sql) {
        System.out.println("Executing legacy query: " + sql);
    }
    
    public void closeConnection() {
        System.out.println("Legacy database connection closed");
    }
}

// Database Adapter
class DatabaseAdapter implements DatabaseTarget {
    private LegacyDatabase legacyDatabase;
    
    public DatabaseAdapter(LegacyDatabase legacyDatabase) {
        this.legacyDatabase = legacyDatabase;
    }
    
    @Override
    public void connect() {
        legacyDatabase.establishConnection();
    }
    
    @Override
    public void executeQuery(String query) {
        legacyDatabase.runQuery(query);
    }
    
    @Override
    public void disconnect() {
        legacyDatabase.closeConnection();
    }
}

// Third example - Shape Adapter
interface Shape {
    void draw();
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Rectangle");
    }
}

class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Drawing Circle");
    }
}

// Legacy shape class with different interface
class LegacyTriangle {
    public void drawTriangle() {
        System.out.println("Drawing Legacy Triangle");
    }
}

class LegacyLine {
    public void drawLine() {
        System.out.println("Drawing Legacy Line");
    }
}

// Shape Adapter
class ShapeAdapter implements Shape {
    private Object legacyShape;
    
    public ShapeAdapter(Object legacyShape) {
        this.legacyShape = legacyShape;
    }
    
    @Override
    public void draw() {
        if (legacyShape instanceof LegacyTriangle) {
            ((LegacyTriangle) legacyShape).drawTriangle();
        } else if (legacyShape instanceof LegacyLine) {
            ((LegacyLine) legacyShape).drawLine();
        }
    }
}

public class AdapterPattern {
    public static void main(String[] args) {
        System.out.println("=== Adapter Pattern Demo ===");
        
        // Media Player Example
        System.out.println("\n--- Media Player Example ---");
        AudioPlayer audioPlayer = new AudioPlayer();
        
        audioPlayer.play("mp3", "beyond_the_horizon.mp3");
        audioPlayer.play("mp4", "alone.mp4");
        audioPlayer.play("vlc", "far_far_away.vlc");
        audioPlayer.play("avi", "mind_me.avi");
        
        // Database Example
        System.out.println("\n--- Database Adapter Example ---");
        LegacyDatabase legacyDb = new LegacyDatabase();
        DatabaseTarget modernDb = new DatabaseAdapter(legacyDb);
        
        modernDb.connect();
        modernDb.executeQuery("SELECT * FROM users");
        modernDb.disconnect();
        
        // Shape Adapter Example
        System.out.println("\n--- Shape Adapter Example ---");
        
        // Regular shapes
        Shape rectangle = new Rectangle();
        Shape circle = new Circle();
        
        // Legacy shapes using adapter
        Shape triangle = new ShapeAdapter(new LegacyTriangle());
        Shape line = new ShapeAdapter(new LegacyLine());
        
        // Draw all shapes using the same interface
        Shape[] shapes = {rectangle, circle, triangle, line};
        
        for (Shape shape : shapes) {
            shape.draw();
        }
    }
} 