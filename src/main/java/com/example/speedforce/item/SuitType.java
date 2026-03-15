package com.example.speedforce.item;

public enum SuitType {
    FLASH("flash", 255, 210, 0, 4),
    REVERSE_FLASH("reverse_flash", 255, 0, 0, 5),
    ZOOM("zoom", 0, 150, 255, 6),
    JAY("jay", 255, 200, 50, 4),
    EARTHX("earthx", 180, 0, 50, 4),
    JAY_EARTH90("jay_earth90", 255, 255, 255, 4),
    FLASH_S1("flash_s1", 255, 210, 0, 4);

    private final String name;
    private final int trailColorR;
    private final int trailColorG;
    private final int trailColorB;
    private final int speedBonus;

    SuitType(String name, int trailColorR, int trailColorG, int trailColorB, int speedBonus) {
        this.name = name;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.speedBonus = speedBonus;
    }

    public String getName() {
        return name;
    }

    public int getTrailColorR() {
        return trailColorR;
    }

    public int getTrailColorG() {
        return trailColorG;
    }

    public int getTrailColorB() {
        return trailColorB;
    }

    public int getSpeedBonus() {
        return speedBonus;
    }
}