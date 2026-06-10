package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaBalance;
import com.udea.financial.e2e.tasks.ConsultarBalanceTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import com.udea.financial.e2e.tasks.RegistrarTransaccionesParaBalanceTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.time.YearMonth;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU07StepDefinitions {

    private String    token;
    private Response  response;
    private YearMonth mesDelEscenario; // Variable para mantener el control temporal del escenario actual

    @Given("que el usuario tiene una sesión activa en el sistema de balance")
    public void usuarioConSesionActivaBalance() {
        token = ObtenerTokenTask.obtenerToken();
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();

        // Inicializamos un mes único y aleatorio exclusivo para esta ejecución del escenario
        mesDelEscenario = RegistrarTransaccionesParaBalanceTask.generarMesAleatorioLimpio();
    }

    @Given("ha registrado ingresos por {double} y gastos por {double} en el mes actual")
    public void haRegistradoIngresosYGastos(Double montoIngreso, Double montoGasto) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Long categoryIngresoId = RegistrarTransaccionesParaBalanceTask.crearCategoriaIngreso(token, sufijo);
        Long categoryGastoId   = RegistrarTransaccionesParaBalanceTask.crearCategoriaGasto(token, sufijo);

        // Pasamos por parámetro el mes libre asignado a este escenario
        RegistrarTransaccionesParaBalanceTask.registrarIngreso(token, categoryIngresoId, montoIngreso, mesDelEscenario);
        RegistrarTransaccionesParaBalanceTask.registrarGasto(token, categoryGastoId, montoGasto, mesDelEscenario);
    }

    @When("consulta el balance financiero del mes actual")
    public void consultaElBalanceDelMesActual() {
        // Consultamos exactamente el mismo año y mes donde guardamos los datos limpios
        response = ConsultarBalanceTask.consultarBalanceMes(token, mesDelEscenario.getYear(), mesDelEscenario.getMonthValue());

        // Opcional para depurar en consola
        System.out.println("RESPUESTA REAL MES CONTROLADO (" + mesDelEscenario + "): " + response.getBody().asString());
    }

    @Then("el sistema retorna el balance con código {int}")
    public void elSistemaRetornaElBalanceConCodigo(int codigoEsperado) {
        assertThat(RespuestaBalance.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el status del balance es {string}")
    public void elStatusDelBalanceEs(String statusEsperado) {
        assertThat(RespuestaBalance.obtenerStatus(response))
                .as("El status del balance debe ser " + statusEsperado)
                .isEqualTo(statusEsperado);
    }

    @Then("la respuesta contiene el mensaje de alerta {string}")
    public void laRespuestaContieneElMensajeDeAlerta(String mensajeEsperado) {
        assertThat(RespuestaBalance.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener la alerta: " + mensajeEsperado)
                .isTrue();
    }
}