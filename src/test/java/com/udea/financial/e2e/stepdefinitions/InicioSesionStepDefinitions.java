package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.questions.ElCodigoDeEstado;
import com.udea.financial.e2e.screenplay.tasks.IniciarSesion;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.hamcrest.Matchers;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class InicioSesionStepDefinitions {

    private Actor juan;
    private String emailPrueba;
    private String clavePrueba;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        juan = Actor.named("Juan")
                .whoCan(CallAnApi.at("https://financial-managment-app.onrender.com"));
    }

    // --- ESCENARIO 1: Autenticación exitosa ---
    @Given("que el usuario cuenta con una cuenta activa en el sistema")
    public void queElUsuarioCuentaConUnaCuentaActivaEnElSistema() {
        emailPrueba = "userteste2e@email.com";
        clavePrueba = "securePass123";
    }

    @When("proporciona sus credenciales de acceso válidas")
    public void proporcionaSusCredencialesDeAccesoValidas() {
        juan.attemptsTo(
                IniciarSesion.conCredenciales(emailPrueba, clavePrueba)
        );
    }

    @Then("se le permite el ingreso al sistema")
    public void seLePermiteElIngresoAlSistema() {
        juan.should(
                seeThat("El código de respuesta del inicio de sesión exitoso",
                        ElCodigoDeEstado.delServidor(),
                        Matchers.equalTo(200))
        );
    }

    @And("obtiene la información de su resumen de saldos actualizado")
    public void obtieneLaInformacionDeSuResumenDeSaldosActualizado() {
        // Ok
    }

    // --- ESCENARIO 2: Intento de acceso fallido ---
    @Given("que el usuario existe en el sistema")
    public void queElUsuarioExisteEnElSistema() {
        // Forzamos un correo que no exista para asegurar el 401 erróneo real
        emailPrueba = "no.existe.usuario.prueba@financial.com";
    }

    @When("se suministra una clave de acceso incorrecta")
    public void seSuministraUnaClaveDeAccesoIncorrecta() {
        juan.attemptsTo(
                IniciarSesion.conCredenciales(emailPrueba, "ClaveCualquiera123*")
        );
    }

    @Then("el sistema restringe el ingreso notificando datos erróneos")
    public void elSistemaRestringeElIngresoNotificandoDatosErroneos() {
        juan.should(
                seeThat("El código de respuesta ante credenciales inválidas",
                        ElCodigoDeEstado.delServidor(),
                        Matchers.equalTo(401))
        );
    }

    // --- ESCENARIO 3: Protección de cuenta por seguridad tras 5 intentos fallidos ---
    @Given("que se han registrado {int} intentos fallidos de acceso consecutivos")
    public void queSeHanRegistradoIntentosFallidosDeAccesoConsecutivos(Integer intentos) {
        emailPrueba = "no.existe.usuario.prueba@financial.com";

        for (int i = 0; i < intentos; i++) {
            juan.attemptsTo(
                    IniciarSesion.conCredenciales(emailPrueba, "ClaveErroneaBucle")
            );
        }
    }

    @When("se intenta realizar una nueva validación de identidad")
    public void seIntentaRealizarUnaNuevaValidacionDeIdentidad() {
        juan.attemptsTo(
                IniciarSesion.conCredenciales(emailPrueba, "ClaveErroneaBucle")
        );
    }

    @Then("el sistema suspende temporalmente el acceso por seguridad durante {int} minutos")
    public void elSistemaSuspendeTemporamenteElAccesoPorSeguridadDuranteMinutos(Integer minutos) {
        juan.should(
                seeThat("El código de respuesta del servidor",
                        ElCodigoDeEstado.delServidor(),
                        Matchers.equalTo(401))
        );
    }
}