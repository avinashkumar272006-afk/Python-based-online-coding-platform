package com.codingplatform.config;

import java.util.Locale;
import java.util.function.Function;

/**
 * Utility for resolving and classifying the application's runtime environment.
 * Evaluates the APP_ENV setting from JVM system properties and OS environment variables
 * with strict normalization and fail-closed resolution to UNKNOWN.
 */
public final class AppEnvironment {

    public static final String APP_ENV_KEY = "APP_ENV";

    private AppEnvironment() {
        // Utility class
    }

    /**
     * Resolves the current EnvironmentType using ambient JVM system properties and OS environment variables.
     *
     * @return the detected EnvironmentType, or UNKNOWN if missing, blank, or unrecognized
     */
    public static EnvironmentType getEnvironment() {
        return resolve(System::getProperty, System::getenv);
    }

    /**
     * Package-private deterministic resolution seam taking explicit lookup functions.
     * Avoids mutable global state across concurrent or sequential test executions.
     *
     * @param propertyLookup function providing system property values
     * @param envLookup function providing environment variable values
     * @return the resolved EnvironmentType
     */
    static EnvironmentType resolve(Function<String, String> propertyLookup, Function<String, String> envLookup) {
        Function<String, String> safePropLookup = propertyLookup != null ? propertyLookup : k -> null;
        Function<String, String> safeEnvLookup = envLookup != null ? envLookup : k -> null;

        String sysProp = safePropLookup.apply(APP_ENV_KEY);
        if (sysProp != null) {
            String trimmed = sysProp.trim();
            if (trimmed.isEmpty()) {
                // If system property exists but is blank, return UNKNOWN without falling back to env var
                return EnvironmentType.UNKNOWN;
            }
            return parse(trimmed);
        }

        String envVal = safeEnvLookup.apply(APP_ENV_KEY);
        if (envVal != null) {
            String trimmed = envVal.trim();
            if (trimmed.isEmpty()) {
                return EnvironmentType.UNKNOWN;
            }
            return parse(trimmed);
        }

        return EnvironmentType.UNKNOWN;
    }

    /**
     * Parses and normalizes a raw environment configuration string.
     * Trims whitespace, normalizes case using Locale.ROOT, and matches canonical names and aliases.
     *
     * @param rawValue raw input string
     * @return matching EnvironmentType, or UNKNOWN if null, blank, or unrecognized
     */
    public static EnvironmentType parse(String rawValue) {
        if (rawValue == null) {
            return EnvironmentType.UNKNOWN;
        }

        String normalized = rawValue.trim().toUpperCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            return EnvironmentType.UNKNOWN;
        }

        return switch (normalized) {
            case "TEST", "TESTING" -> EnvironmentType.TEST;
            case "LOCAL_DEV", "LOCAL-DEV", "LOCAL", "DEV", "DEVELOPMENT" -> EnvironmentType.LOCAL_DEV;
            case "STAGING", "STAGE" -> EnvironmentType.STAGING;
            case "PRODUCTION", "PROD" -> EnvironmentType.PRODUCTION;
            default -> EnvironmentType.UNKNOWN;
        };
    }
}
