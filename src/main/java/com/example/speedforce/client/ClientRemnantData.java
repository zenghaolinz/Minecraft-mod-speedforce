package com.example.speedforce.client;

public class ClientRemnantData {
    public static boolean hasRemnant = false;
    public static int remainingSeconds = 0;
    
    public static void setRemnantActive(boolean active, int seconds) {
        hasRemnant = active;
        remainingSeconds = seconds;
    }
    
    public static void reset() {
        hasRemnant = false;
        remainingSeconds = 0;
    }
}