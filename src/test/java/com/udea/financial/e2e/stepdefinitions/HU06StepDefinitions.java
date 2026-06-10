package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.interactions.ApiInteraction;
import com.udea.financial.e2e.questions.RespuestaPresupuesto;
import com.udea.financial.e2e.tasks.GestionarPresupuestoTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import com.udea.financial.e2e.tasks.RegistrarGastoParaPresupuestoTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU06StepDefinitions {

    private static final String CATEGORIES_ENDPOINT = "/api/categories";

    private String   token;
    private Long     categoryId;
    private Long     budgetId;       // ID del presupuesto creado, no de la categoría
    private Response response;

    @Given("que el usuario tiene una sesión activa en el sistema de presupuestos")
    public void usuarioConSesionActivaPresupuestos() {
        token = ObtenerTokenTask.obtenerToken();
        // Nombre único por ejecución: garantiza cero gastos previos
        String nombreUnico = "BUDGET_E2E_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String body = String.format("{\"name\":\"%s\",\"type\":\"EXPENSE\"}", nombreUnico);
        Response catResponse = ApiInteraction.postWithAuth(CATEGORIES_ENDPOINT, body, token);
        categoryId = catResponse.jsonPath().getLong("id");
        assertThat(token).as("El token JWT no debe ser nulo ni vacío").isNotBlank();
        assertThat(categoryId).as("El ID de categoría debe haberse creado correctamente").isNotNull();
    }

    @Given("ya existe un presupuesto para la categoría de gasto")
    public void yaExisteUnPresupuesto() {
        Response r = GestionarPresupuestoTask.crearPresupuesto(token, categoryId, new BigDecimal("500.00"));
        budgetId = r.jsonPath().getLong("budgetId");
    }

    @Given("existe un presupuesto de {double} con gastos que representan el 80% del límite")
    public void presupuestoConGastosAlOchentaPorciento(Double limitePresupuesto) {
        Response r = GestionarPresupuestoTask.crearPresupuesto(token, categoryId, BigDecimal.valueOf(limitePresupuesto));
        budgetId = Long.valueOf(r.jsonPath().getInt("budgetId"));
        double montoGasto = limitePresupuesto * 0.80;
        RegistrarGastoParaPresupuestoTask.registrarGasto(token, categoryId, montoGasto);
    }

    @Given("existe un presupuesto de {double} con gastos que superan o igualan el límite")
    public void presupuestoConGastosQueIgualanOSuperanElLimite(Double limitePresupuesto) {
        Response r = GestionarPresupuestoTask.crearPresupuesto(token, categoryId, BigDecimal.valueOf(limitePresupuesto));
        budgetId = Long.valueOf(r.jsonPath().getInt("budgetId"));
        RegistrarGastoParaPresupuestoTask.registrarGasto(token, categoryId, limitePresupuesto);
    }

    @When("crea un presupuesto con monto máximo {double} para una categoría de gasto válida")
    public void creaUnPresupuesto(Double monto) {
        response = GestionarPresupuestoTask.crearPresupuesto(token, categoryId, BigDecimal.valueOf(monto));
    }

    @When("consulta el estado de sus presupuestos")
    public void consultaElEstadoDeSusPresupuestos() {
        response = GestionarPresupuestoTask.consultarEstados(token);
    }

    @When("intenta crear otro presupuesto para la misma categoría")
    public void intentaCrearPresupuestoDuplicado() {
        response = GestionarPresupuestoTask.crearPresupuesto(token, categoryId, new BigDecimal("300.00"));
    }

    @Then("el presupuesto se crea exitosamente con código {int}")
    public void presupuestoCreado(int codigoEsperado) {
        assertThat(RespuestaPresupuesto.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el sistema retorna el estado del presupuesto con código {int}")
    public void sistemaRetornaEstado(int codigoEsperado) {
        assertThat(RespuestaPresupuesto.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el estado del presupuesto es {string}")
    public void elEstadoDelPresupuestoEs(String statusEsperado) {
        // Filtramos por budgetId (ID del presupuesto) que sí viene en el DTO de la lista
        String statusObtenido = RespuestaPresupuesto.obtenerStatusPorBudgetId(response, budgetId);
        assertThat(statusObtenido)
                .as("El status del presupuesto debe ser " + statusEsperado)
                .isEqualTo(statusEsperado);
    }

    @Then("el sistema rechaza el presupuesto con código {int}")
    public void sistemaRechazaPresupuesto(int codigoEsperado) {
        assertThat(RespuestaPresupuesto.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("muestra el mensaje de presupuesto {string}")
    public void muestraMensajeDePresupuesto(String mensajeEsperado) {
        assertThat(RespuestaPresupuesto.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }
}