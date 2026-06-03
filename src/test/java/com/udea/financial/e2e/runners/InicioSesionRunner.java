package com.udea.financial.e2e.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
// Apuntamos al nuevo nombre exacto del archivo que corregiste
@SelectClasspathResource("/features/auth.feature")
@ConfigurationParameter(
        key = "cucumber.plugin",
        value = "pretty, io.cucumber.core.plugin.SerenityReporter"
)
@ConfigurationParameter(
        key = "cucumber.glue",
        value = "com.udea.financial.e2e.stepdefinitions"
)
public class InicioSesionRunner {
}