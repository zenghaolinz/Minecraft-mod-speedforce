package com.example.speedforce.client;

public class ClientRewindData {
    public static int phase = 0;
    public static int framesRewound = 0;
    public static int rewindSpeed = 1;
    public static int confirmTimeRemaining = 0;
    public static int totalHistorySize = 0;

    public static final int PHASE_IDLE = 0;
    public static final int PHASE_REWINDING = 1;
    public static final int PHASE_CONFIRMING = 2;

    public static boolean isRewinding() {
        return phase == PHASE_REWINDING;
    }

    public static boolean isConfirming() {
        return phase == PHASE_CONFIRMING;
    }

    public static float getRewindProgress() {
        if (totalHistorySize <= 0) return 0;
        return (float) framesRewound / totalHistorySize;
    }

    public static float getConfirmTimeSeconds() {
        return confirmTimeRemaining / 20.0f;
    }

    public static void reset() {
        phase = PHASE_IDLE;
        framesRewound = 0;
        rewindSpeed = 1;
        confirmTimeRemaining = 0;
        totalHistorySize = 0;
    }
}