package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaIngreso;
import com.udea.financial.e2e.tasks.CrearCategoriaTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import com.udea.financial.e2e.tasks.RegistrarIngresoTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import static org.assertj.core.api.Assertions.assertThat;

public class HU03StepDefinitions {

    private String token;
    private Long   categoryId;
    private Response response;

    @Given("que el usuario tiene una sesión activa en el sistema de ingresos")
    public void usuarioConSesionActiva() {
        token      = ObtenerTokenTask.obtenerToken();
        categoryId = CrearCategoriaTask.crearCategoriaIngreso(token);
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();
    }

    @When("registra un ingreso con monto {double}, descripción {string} y una categoría válida")
    public void registraIngresoConDatosValidos(Double monto, String descripcion) {
        response = RegistrarIngresoTask.conDatosValidos(token, categoryId);
    }

    @When("intenta registrar un ingreso con monto {double}")
    public void intentaRegistrarIngresoConMontoInvalido(Double monto) {
        response = RegistrarIngresoTask.conMontoInvalido(token, categoryId);
    }

    @When("intenta registrar un ingreso con una categoría inexistente")
    public void intentaRegistrarIngresoConCategoriaInexistente() {
        response = RegistrarIngresoTask.conCategoriaInexistente(token);
    }

    @Then("el ingreso se registra exitosamente con código {int}")
    public void ingresoRegistradoExitosamente(int codigoEsperado) {
        assertThat(RespuestaIngreso.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el sistema rechaza el ingreso con código {int}")
    public void sistemaRechazaElIngreso(int codigoEsperado) {
        assertThat(RespuestaIngreso.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("muestra el mensaje {string}")
    public void muestraElMensaje(String mensajeEsperado) {
        assertThat(RespuestaIngreso.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }
}