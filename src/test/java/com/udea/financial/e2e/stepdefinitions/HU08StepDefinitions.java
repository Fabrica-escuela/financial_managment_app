package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaReporte;
import com.udea.financial.e2e.tasks.ConsultarReporteTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import com.udea.financial.e2e.tasks.PrepararDatosReporteTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;

public class HU08StepDefinitions {

    private String   token;
    private Response response;

    // ─── Givens ─────────────────────────────────────────────────────────────────

    @Given("que el usuario tiene una sesión activa en el sistema de reportes")
    public void usuarioConSesionActivaReportes() {
        token = ObtenerTokenTask.obtenerToken();
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();
    }

    @Given("tiene gastos registrados en distintas categorías en el mes de referencia de reportes")
    public void tieneGastosEnDistintasCategorias() {
        PrepararDatosReporteTask.registrarGastosDistribuidos(token);
    }

    @Given("tiene gastos registrados en los dos meses a comparar")
    public void tieneGastosEnDosMeses() {
        PrepararDatosReporteTask.registrarGastosParaComparacion(token);
    }

    // ─── Whens ──────────────────────────────────────────────────────────────────

    @When("solicita el reporte mensual para ese período")
    public void solicitaReporteMensualConDatos() {
        response = ConsultarReporteTask.reporteMensualConDatos(token);
    }

    @When("solicita el reporte mensual para un período sin transacciones")
    public void solicitaReporteMensualSinDatos() {
        response = ConsultarReporteTask.reporteMensualMesVacio(token);
    }


    @When("solicita la comparación entre ambos meses")
    public void solicitaComparacionDosMeses() {
        response = ConsultarReporteTask.comparacionDosMeses(token);
        System.out.println("JSON COMPARACIÓN: " + response.getBody().asString());
    }

    @When("solicita el reporte mensual para un período futuro")
    public void solicitaReportePeriodoFuturo() {
        response = ConsultarReporteTask.reporteMensualPeriodoFuturo(token);
    }

    @When("solicita la comparación usando el mismo período en ambos parámetros")
    public void solicitaComparacionMismoMes() {
        response = ConsultarReporteTask.comparacionMismoMes(token);
    }

    @When("solicita el reporte mensual con un mes fuera del rango permitido")
    public void solicitaReporteMesInvalido() {
        response = ConsultarReporteTask.reporteMensualMesInvalido(token);
    }

    // ─── Thens ──────────────────────────────────────────────────────────────────

    @Then("el sistema retorna el reporte con código {int}")
    public void sistemaRetornaReporteConCodigo(int codigoEsperado) {
        assertThat(RespuestaReporte.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el reporte contiene una distribución de gastos no vacía")
    public void reporteContieneDistribucionNoVacia() {
        assertThat(RespuestaReporte.distribucionNoEstaVacia(response))
                .as("El campo 'distribution' no debe estar vacío")
                .isTrue();
    }

    @Then("el reporte incluye el top de categorías con mayor consumo")
    public void reporteIncluyeTopCategorias() {
        assertThat(RespuestaReporte.topCategoriasNoEstaVacio(response))
                .as("El campo 'topCategories' no debe estar vacío")
                .isTrue();
        assertThat(RespuestaReporte.cantidadTopCategorias(response))
                .as("El top de categorías no debe superar 3")
                .isLessThanOrEqualTo(3);
    }

    @Then("la respuesta contiene el mensaje {string}")
    public void respuestaContieneElMensaje(String mensajeEsperado) {
        assertThat(RespuestaReporte.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }

    @Then("la respuesta contiene la sugerencia {string}")
    public void respuestaContieneLaSugerencia(String sugerenciaEsperada) {
        assertThat(RespuestaReporte.cuerpoContiene(response, sugerenciaEsperada))
                .as("La respuesta debe contener la sugerencia: " + sugerenciaEsperada)
                .isTrue();
    }

    @Then("el sistema retorna la comparación con código {int}")
    public void sistemaRetornaComparacionConCodigo(int codigoEsperado) {
        assertThat(RespuestaReporte.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("la comparación contiene al menos una categoría con gasto aumentado")
    public void comparacionContieneCategoriasConGastoAumentado() {
        assertThat(RespuestaReporte.comparacionNoEstaVacia(response))
                .as("El campo 'comparisons' no debe estar vacío")
                .isTrue();
        assertThat(RespuestaReporte.existeCategoriaConGastoAumentado(response))
                .as("Debe existir al menos una categoría con 'increased = true'")
                .isTrue();
    }

    @Then("el sistema rechaza la solicitud con código {int}")
    public void sistemaRechazaSolicitudConCodigo(int codigoEsperado) {
        assertThat(RespuestaReporte.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("la respuesta contiene el mensaje de error {string}")
    public void respuestaContieneElMensajeDeError(String mensajeEsperado) {
        assertThat(RespuestaReporte.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener el error: " + mensajeEsperado)
                .isTrue();
    }
}