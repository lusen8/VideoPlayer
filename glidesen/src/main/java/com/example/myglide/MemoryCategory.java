package com.example.myglide;

/**
 * Created by lusen on 2017/5/3.
 */

public enum MemoryCategory {
    /**
     * Tells MyGlide's memory cache and bitmap pool to use at most half of their initial maximum size.
     */
    LOW(0.5f),
    /**
     * Tells MyGlide's memory cache and bitmap pool to use at most their initial maximum size.
     */
    NORMAL(1f),
    /**
     * Tells MyGlide's memory cache and bitmap pool to use at most one and a half times their initial
     * maximum size.
     */
    HIGH(1.5f);

    private float multiplier;

    MemoryCategory(float multiplier) {
        this.multiplier = multiplier;
    }

    /**
     * Returns the multiplier that should be applied to the initial maximum size of MyGlide's memory
     * cache and bitmap pool.
     */
    public float getMultiplier() {
        return multiplier;
    }
}

