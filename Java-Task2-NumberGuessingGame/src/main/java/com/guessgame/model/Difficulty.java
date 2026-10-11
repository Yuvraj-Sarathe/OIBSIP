package com.guessgame.model;

public enum Difficulty {
    
    EASY(1, 50, 10),
    MEDIUM(1, 100, 7),
    HARD(1, 200, 5);
    
    private final int min;
    private final int max;
    private final int maxAttempts;
    
    Difficulty(int min, int max, int maxAttempts) {
        this.min = min;
        this.max = max;
        this.maxAttempts = maxAttempts;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    @Override
    public String toString() {
        return name().charAt(0) + name().substring(1).toLowerCase()
                + " (" + min + "-" + max + ", " + maxAttempts + " attempts)";
    }
}
