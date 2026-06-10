package com.udea.financial.e2e.runners;

import io.cucumber.junit.CucumberOptions;
import net.serenitybdd.cucumber.CucumberWithSerenity;
import org.junit.runner.RunWith;

@RunWith(CucumberWithSerenity.class)
@CucumberOptions(
        features = "src/test/resources/features/HU03_RegistroIngresos.feature",
        glue = "com.udea.financial.e2e.stepdefinitions",
        plugin = {
                "pretty",
                "json:target/cucumber-reports/HU03_RegistroIngresos.json"
        }
)
public class HU03Runner {
}