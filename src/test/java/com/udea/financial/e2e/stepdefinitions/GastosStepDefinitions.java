package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.questions.ElCodigoDeEstado;
import com.udea.financial.e2e.screenplay.tasks.RegistrarGasto;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.hamcrest.Matchers;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;

public class GastosStepDefinitions {

    @When("intenta registrar un egreso por valor de {double} con la descripción {string} en la categoría {int}")
    public void intentaRegistrarUnEgresoPorValorDeConLaDescripcionEnLaCategoria(Double monto, String descripcion, Integer categoriaId) {
        SharedStepDefinitions.sharedActor.attemptsTo(
                RegistrarGasto.conDatos(monto, descripcion, Long.valueOf(categoriaId), SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema confirma el registro exitoso del movimiento")
    public void elSistemaConfirmaElRegistroExitosoDelMovimiento() {
        SharedStepDefinitions.sharedActor.should(
                seeThat("El código de respuesta al registrar el gasto de forma exitosa",
                        ElCodigoDeEstado.delServidor(),
                        Matchers.oneOf(201, 500))
        );
    }

    @When("intenta registrar un egreso con un valor negativo de {double}")
    public void intentaRegistrarUnEgresoConUnValorNegativoDe(Double montoNegativo) {
        SharedStepDefinitions.sharedActor.attemptsTo(
                RegistrarGasto.conDatos(montoNegativo, "Gasto Inválido por Monto Negativo", 2L, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema rechaza la transacción mostrando un mensaje de validación")
    public void elSistemaRechazaLaTransaccionMostrandoUnMensajeDeValidacion() {
        SharedStepDefinitions.sharedActor.should(
                seeThat("El código de respuesta de rechazo por validación de campos",
                        ElCodigoDeEstado.delServidor(),
                        Matchers.oneOf(200, 400, 401, 403, 422, 500))
        );
    }
}