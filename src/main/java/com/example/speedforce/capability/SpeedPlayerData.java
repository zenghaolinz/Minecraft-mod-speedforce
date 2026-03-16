package com.example.speedforce.capability;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SpeedPlayerData {
    public boolean hasPower = false;
    public int speedLevel = 0;
    public boolean isBulletTimeActive = false;
    public boolean isPhasing = false;
    public int trailColorR = 255;
    public int trailColorG = 210;
    public int trailColorB = 0;
    public int customTrailColorR = 255;
    public int customTrailColorG = 210;
    public int customTrailColorB = 0;

    public SpeedPlayerData() {}

    public SpeedPlayerData(boolean hasPower, int speedLevel) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, int trailColorR, int trailColorG, int trailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = trailColorR;
        this.customTrailColorG = trailColorG;
        this.customTrailColorB = trailColorB;
    }

    public SpeedPlayerData(boolean hasPower, int speedLevel, boolean isBulletTimeActive, boolean isPhasing, 
                           int trailColorR, int trailColorG, int trailColorB,
                           int customTrailColorR, int customTrailColorG, int customTrailColorB) {
        this.hasPower = hasPower;
        this.speedLevel = speedLevel;
        this.isBulletTimeActive = isBulletTimeActive;
        this.isPhasing = isPhasing;
        this.trailColorR = trailColorR;
        this.trailColorG = trailColorG;
        this.trailColorB = trailColorB;
        this.customTrailColorR = customTrailColorR;
        this.customTrailColorG = customTrailColorG;
        this.customTrailColorB = customTrailColorB;
    }

    public static final Codec<SpeedPlayerData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.BOOL.optionalFieldOf("hasPower", false).forGetter(d -> d.hasPower),
        Codec.INT.optionalFieldOf("speedLevel", 0).forGetter(d -> d.speedLevel),
        Codec.BOOL.optionalFieldOf("isBulletTimeActive", false).forGetter(d -> d.isBulletTimeActive),
        Codec.BOOL.optionalFieldOf("isPhasing", false).forGetter(d -> d.isPhasing),
        Codec.INT.optionalFieldOf("trailColorR", 255).forGetter(d -> d.trailColorR),
        Codec.INT.optionalFieldOf("trailColorG", 210).forGetter(d -> d.trailColorG),
        Codec.INT.optionalFieldOf("trailColorB", 0).forGetter(d -> d.trailColorB),
        Codec.INT.optionalFieldOf("customTrailColorR", 255).forGetter(d -> d.customTrailColorR),
        Codec.INT.optionalFieldOf("customTrailColorG", 210).forGetter(d -> d.customTrailColorG),
        Codec.INT.optionalFieldOf("customTrailColorB", 0).forGetter(d -> d.customTrailColorB)
    ).apply(instance, SpeedPlayerData::new));
}