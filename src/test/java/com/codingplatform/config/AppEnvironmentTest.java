package com.codingplatform.config;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AppEnvironmentTest {

    private String originalAppEnvSysProp;

    @BeforeEach
    public void setUp() {
        originalAppEnvSysProp = System.getProperty(AppEnvironment.APP_ENV_KEY);
        System.clearProperty(AppEnvironment.APP_ENV_KEY);
    }

    @AfterEach
    public void tearDown() {
        if (originalAppEnvSysProp != null) {
            System.setProperty(AppEnvironment.APP_ENV_KEY, originalAppEnvSysProp);
        } else {
            System.clearProperty(AppEnvironment.APP_ENV_KEY);
        }
    }

    @Test
    public void testCanonicalValues() {
        assertEquals(EnvironmentType.TEST, AppEnvironment.parse("TEST"));
        assertEquals(EnvironmentType.LOCAL_DEV, AppEnvironment.parse("LOCAL_DEV"));
        assertEquals(EnvironmentType.STAGING, AppEnvironment.parse("STAGING"));
        assertEquals(EnvironmentType.PRODUCTION, AppEnvironment.parse("PRODUCTION"));
        assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse("UNKNOWN"));
    }

    @Test
    public void testTestAliasesAndNormalization() {
        List<String> inputs = List.of("TEST", "TESTING", "test", "testing", "  TEST  ", "\tTesting\n");
        for (String input : inputs) {
            assertEquals(EnvironmentType.TEST, AppEnvironment.parse(input), "Failed for input: " + input);
        }
    }

    @Test
    public void testLocalDevAliasesAndNormalization() {
        List<String> inputs = List.of(
                "LOCAL_DEV", "local_dev", "Local_Dev",
                "LOCAL-DEV", "local-dev",
                "LOCAL", "local",
                "DEV", "dev",
                "DEVELOPMENT", "development",
                "  dev  ", "\tLOCAL_DEV\n"
        );
        for (String input : inputs) {
            assertEquals(EnvironmentType.LOCAL_DEV, AppEnvironment.parse(input), "Failed for input: " + input);
        }
    }

    @Test
    public void testStagingAliasesAndNormalization() {
        List<String> inputs = List.of("STAGING", "staging", "STAGE", "stage", "  staging  ", "\tStage\n");
        for (String input : inputs) {
            assertEquals(EnvironmentType.STAGING, AppEnvironment.parse(input), "Failed for input: " + input);
        }
    }

    @Test
    public void testProductionAliasesAndNormalization() {
        List<String> inputs = List.of("PRODUCTION", "production", "PROD", "prod", "  production  ", "\tPROD\n");
        for (String input : inputs) {
            assertEquals(EnvironmentType.PRODUCTION, AppEnvironment.parse(input), "Failed for input: " + input);
        }
    }

    @Test
    public void testMissingBlankAndWhitespaceOnlyValues() {
        assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse(null));
        assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse(""));
        assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse("   "));
        assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse("\t\n"));
    }

    @Test
    public void testUnrecognizedValuesResolveToUnknown() {
        List<String> inputs = List.of("QA", "SANDBOX", "INVALID", "UAT", "preview", "123", "unknown_custom");
        for (String input : inputs) {
            assertEquals(EnvironmentType.UNKNOWN, AppEnvironment.parse(input), "Failed for input: " + input);
        }
    }

    @Test
    public void testSystemPropertyPrecedenceOverEnvironmentVariable() {
        Map<String, String> sysProps = Map.of(AppEnvironment.APP_ENV_KEY, "STAGING");
        Map<String, String> envVars = Map.of(AppEnvironment.APP_ENV_KEY, "PRODUCTION");

        EnvironmentType resolved = AppEnvironment.resolve(sysProps::get, envVars::get);
        assertEquals(EnvironmentType.STAGING, resolved, "System property must take precedence over env var");
    }

    @Test
    public void testEnvironmentVariableUsedWhenSystemPropertyAbsent() {
        Map<String, String> sysProps = Map.of();
        Map<String, String> envVars = Map.of(AppEnvironment.APP_ENV_KEY, "LOCAL_DEV");

        EnvironmentType resolved = AppEnvironment.resolve(sysProps::get, envVars::get);
        assertEquals(EnvironmentType.LOCAL_DEV, resolved);
    }

    @Test
    public void testBlankSystemPropertyDoesNotFallBackToEnvironmentVariable() {
        Map<String, String> sysProps = Map.of(AppEnvironment.APP_ENV_KEY, "   ");
        Map<String, String> envVars = Map.of(AppEnvironment.APP_ENV_KEY, "PRODUCTION");

        EnvironmentType resolved = AppEnvironment.resolve(sysProps::get, envVars::get);
        assertEquals(EnvironmentType.UNKNOWN, resolved, "Blank system property must resolve to UNKNOWN and not fall back to env var");
    }

    @Test
    public void testBlankEnvironmentVariableResolvesToUnknown() {
        Map<String, String> sysProps = Map.of();
        Map<String, String> envVars = Map.of(AppEnvironment.APP_ENV_KEY, "   ");

        EnvironmentType resolved = AppEnvironment.resolve(sysProps::get, envVars::get);
        assertEquals(EnvironmentType.UNKNOWN, resolved);
    }

    @Test
    public void testBothAbsentResolvesToUnknown() {
        Map<String, String> sysProps = Map.of();
        Map<String, String> envVars = Map.of();

        EnvironmentType resolved = AppEnvironment.resolve(sysProps::get, envVars::get);
        assertEquals(EnvironmentType.UNKNOWN, resolved);
    }

    @Test
    public void testNullLookupFunctionsHandledGracefully() {
        EnvironmentType resolved = AppEnvironment.resolve(null, null);
        assertEquals(EnvironmentType.UNKNOWN, resolved);
    }

    @Test
    public void testLiveGetEnvironmentWithSystemProperty() {
        System.setProperty(AppEnvironment.APP_ENV_KEY, "TEST");
        assertEquals(EnvironmentType.TEST, AppEnvironment.getEnvironment());

        System.setProperty(AppEnvironment.APP_ENV_KEY, "  dev  ");
        assertEquals(EnvironmentType.LOCAL_DEV, AppEnvironment.getEnvironment());
    }

    @Test
    public void testEnvironmentTypeHelperMethods() {
        assertTrue(EnvironmentType.TEST.isTest());
        assertFalse(EnvironmentType.TEST.isProduction());
        assertFalse(EnvironmentType.TEST.isLocalDev());
        assertFalse(EnvironmentType.TEST.isStaging());
        assertFalse(EnvironmentType.TEST.isUnknown());
        assertEquals("TEST", EnvironmentType.TEST.getCanonicalName());

        assertTrue(EnvironmentType.LOCAL_DEV.isLocalDev());
        assertFalse(EnvironmentType.LOCAL_DEV.isTest());
        assertEquals("LOCAL_DEV", EnvironmentType.LOCAL_DEV.getCanonicalName());

        assertTrue(EnvironmentType.STAGING.isStaging());
        assertFalse(EnvironmentType.STAGING.isProduction());
        assertEquals("STAGING", EnvironmentType.STAGING.getCanonicalName());

        assertTrue(EnvironmentType.PRODUCTION.isProduction());
        assertFalse(EnvironmentType.PRODUCTION.isLocalDev());
        assertEquals("PRODUCTION", EnvironmentType.PRODUCTION.getCanonicalName());

        assertTrue(EnvironmentType.UNKNOWN.isUnknown());
        assertFalse(EnvironmentType.UNKNOWN.isProduction());
        assertEquals("UNKNOWN", EnvironmentType.UNKNOWN.getCanonicalName());
    }
}
