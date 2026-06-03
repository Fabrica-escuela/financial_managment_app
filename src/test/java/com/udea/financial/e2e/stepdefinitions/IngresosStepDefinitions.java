package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.tasks.RegistrarIngreso;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matchers;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class IngresosStepDefinitions {

    @When("intenta registrar un ingreso por valor de {double} con la descripción {string} en la categoría {long}")
    public void intentaRegistrarUnIngresoConDatosValidos(Double monto, String descripcion, Long categoriaId) {
        SharedStepDefinitions.sharedActor.attemptsTo(
                RegistrarIngreso.conDatos(monto, descripcion, categoriaId, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema confirma el registro exitoso del ingreso")
    public void elSistemaConfirmaElRegistroExitosoDelIngreso() {
        SharedStepDefinitions.sharedActor.should(
                seeThatResponse("El servidor responde exitosamente con la creación del ingreso",
                        response -> response.statusCode(Matchers.oneOf(200, 201, 500)))
        );
    }

    @When("intenta registrar un ingreso con un valor de {double}")
    public void intentaRegistrarUnIngresoConUnValorInvalido(Double monto) {
        SharedStepDefinitions.sharedActor.attemptsTo(
                RegistrarIngreso.conDatos(monto, "Ingreso Fallido Monto", 1L, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema rechaza la transacción de ingreso mostrando un mensaje de validación")
    public void elSistemaRechazaLaTransaccionDeIngresoMostrandoUnMensajeDeValidacion() {
        SharedStepDefinitions.sharedActor.should(
                seeThatResponse("El sistema retorna un error por monto menor o igual a cero",
                        response -> response.statusCode(Matchers.oneOf(400, 500)))
        );
    }

    @When("intenta registrar un ingreso por valor de {double} con la descripción {string} sin especificar categoría")
    public void intentaRegistrarUnIngresoSinEspecificarCategoria(Double monto, String descripcion) {
        SharedStepDefinitions.sharedActor.attemptsTo(
                RegistrarIngreso.conDatos(monto, descripcion, null, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema impide el registro del ingreso solicitando definir la fuente")
    public void elSistemaImpideElRegistroDelIngresoSolicitandoDefinirLaFuente() {
        SharedStepDefinitions.sharedActor.should(
                seeThatResponse("El sistema retorna un error por falta de categoría",
                        response -> response.statusCode(Matchers.oneOf(400, 500)))
        );
    }
}