package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.tasks.IniciarSesion;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.junit.jupiter.api.Assumptions;

/**
 * Steps compartidos entre todas las suites E2E.
 * El @Given del Background de ingresos y gastos vive aquí para evitar
 * DuplicateStepDefinitionException cuando Cucumber escanea el paquete completo.
 */
public class SharedStepDefinitions {

    // Token estático: persiste mientras la JVM esté activa (entre escenarios de la misma ejecución)
    static String tokenJwt;

    // Actor estático compartido para que GastosStepDefinitions e IngresosStepDefinitions
    // puedan reutilizarlo sin necesidad de re-crear la capacidad CallAnApi
    static Actor sharedActor;

    @Before
    public void setTheStage() {
        boolean runE2E = Boolean.parseBoolean(
            System.getProperty(
                "runE2E",
                System.getenv().getOrDefault("RUN_E2E_TESTS", "false")
            )
        );

        Assumptions.assumeTrue(
            runE2E,
            "E2E tests are disabled by default in CI. Enable them with -DrunE2E=true or RUN_E2E_TESTS=true."
        );

        OnStage.setTheStage(new OnlineCast());
        sharedActor = Actor.named("User")
                .whoCan(CallAnApi.at("https://financial-managment-app.onrender.com"));
    }

    @Given("que el usuario {string} ha iniciado sesión correctamente")
    public void queElUsuarioHaIniciadoSesionCorrectamente(String email) {
        if (tokenJwt == null || tokenJwt.isEmpty()) {
            sharedActor.attemptsTo(
                    IniciarSesion.conCredenciales(email, "securePass123")
            );

            System.out.println("Cuerpo de Respuesta de Autenticación: " + SerenityRest.lastResponse().asString());

            tokenJwt = SerenityRest.lastResponse().jsonPath().getString("token");
            System.out.println("Token Extraído Exitosamente: " + tokenJwt);
        } else {
            System.out.println("Reutilizando Token JWT válido: " + tokenJwt);
        }
    }
}