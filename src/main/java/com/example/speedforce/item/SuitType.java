package com.example.speedforce.item;

public enum SuitType {
    FLASH("flash", 255, 210, 0, 4),
    REVERSE_FLASH("reverse_flash", 255, 0, 0, 5),
    ZOOM("zoom", 0, 150, 255, 6),
    FLASH_S4("flash_s4", 255, 210, 0, 4),
    FLASH_S5("flash_s5", 255, 210, 0, 4),
    KID_FLASH("kid_flash", 255, 200, 0, 4),
    GREEN_ARROW("green_arrow", 0, 180, 0, 0);

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