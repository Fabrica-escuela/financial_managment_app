package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaGasto;
import com.udea.financial.e2e.tasks.CrearCategoriaTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import com.udea.financial.e2e.tasks.RegistrarGastoTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class HU04StepDefinitions {

    private String   token;
    private Long     categoryId;
    private Response response;

    @Given("que el usuario tiene una sesión activa en el sistema de gastos")
    public void usuarioConSesionActivaGastos() {
        token      = ObtenerTokenTask.obtenerToken();
        categoryId = CrearCategoriaTask.crearCategoriaGasto(token);
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();
    }

    @When("registra un gasto con monto {double}, descripción {string} y una categoría válida")
    public void registraGastoConDatosValidos(Double monto, String descripcion) {
        response = RegistrarGastoTask.conDatosValidos(token, categoryId);
    }

    @When("intenta registrar un gasto con una fecha posterior a la fecha actual")
    public void intentaRegistrarGastoConFechaFutura() {
        response = RegistrarGastoTask.conFechaFutura(token, categoryId);
    }

    @When("intenta registrar un gasto con monto {double}")
    public void intentaRegistrarGastoConMontoInvalido(Double monto) {
        response = RegistrarGastoTask.conMontoInvalido(token, categoryId);
    }

    @Then("el gasto se registra exitosamente con código {int}")
    public void gastoRegistradoExitosamente(int codigoEsperado) {
        assertThat(RespuestaGasto.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el sistema rechaza el gasto con código {int}")
    public void sistemaRechazaElGasto(int codigoEsperado) {
        assertThat(RespuestaGasto.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("muestra el mensaje de gasto {string}")
    public void muestraElMensajeDeGasto(String mensajeEsperado) {
        assertThat(RespuestaGasto.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }
}