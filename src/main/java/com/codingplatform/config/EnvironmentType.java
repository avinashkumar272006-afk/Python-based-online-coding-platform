package com.codingplatform.config;

/**
 * Enumeration representing the runtime environment of the application.
 * Immutable and dependency-free classification used across the platform.
 */
public enum EnvironmentType {
    TEST("TEST"),
    LOCAL_DEV("LOCAL_DEV"),
    STAGING("STAGING"),
    PRODUCTION("PRODUCTION"),
    UNKNOWN("UNKNOWN");

    private final String canonicalName;

    EnvironmentType(String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    public boolean isTest() {
        return this == TEST;
    }

    public boolean isLocalDev() {
        return this == LOCAL_DEV;
    }

    public boolean isStaging() {
        return this == STAGING;
    }

    public boolean isProduction() {
        return this == PRODUCTION;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}
