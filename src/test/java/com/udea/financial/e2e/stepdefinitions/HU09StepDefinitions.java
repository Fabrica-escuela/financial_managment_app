package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.interactions.ApiInteraction;
import com.udea.financial.e2e.questions.RespuestaRecomendacion;
import com.udea.financial.e2e.tasks.ConsultarRecomendacionTask;
import com.udea.financial.e2e.tasks.PrepararDatosRecomendacionTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU09StepDefinitions {

    private String   token;
    private Response response;

    // ─── Helpers ─────────────────────────────────────────────────────────────────

    /**
     * Crea un usuario nuevo y único para esta ejecución y retorna su token.
     * Al ser un usuario recién creado, no tiene ninguna transacción previa,
     * lo que garantiza que cada escenario parte de un estado limpio y aislado,
     * eliminando la necesidad del script manual de limpieza en base de datos.
     */
    private String crearUsuarioAisladoYObtenerToken() {
        String sufijo  = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String correo  = "e2e.recomendacion." + sufijo + "@example.com";
        String nombre  = "Usuario Rec E2E";
        String password = "securePass123";

        // 1. Registrar usuario nuevo
        String bodyRegistro = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                nombre, correo, password
        );
        Response registro = ApiInteraction.post("/api/users", bodyRegistro);
        if (registro.getStatusCode() != 201) {
            throw new RuntimeException("No se pudo crear el usuario aislado para HU-09: "
                    + registro.getStatusCode() + " - " + registro.getBody().asString());
        }

        // 2. Hacer login y retornar token
        String bodyLogin = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}", correo, password
        );
        Response login = ApiInteraction.post("/api/auth/login", bodyLogin);
        String token = login.jsonPath().getString("token");
        if (token == null || token.isBlank()) {
            throw new RuntimeException("No se pudo obtener token para el usuario aislado de HU-09");
        }
        return token;
    }

    // ─── Givens ─────────────────────────────────────────────────────────────────

    @Given("que el usuario tiene una sesión activa en el sistema de recomendaciones")
    public void usuarioConSesionActivaRecomendaciones() {
        token = crearUsuarioAisladoYObtenerToken();
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();
    }

    @Given("tiene un ingreso registrado y un gasto en una categoría que supera el 30% de ese ingreso en el mes actual")
    public void tieneGastoQueSupera30Porciento() {
        PrepararDatosRecomendacionTask.registrarSobregastoEnCategoria(token);
    }

    @Given("tiene registrado un gasto mayor o igual a su ingreso en el mes actual")
    public void tieneGastoMayorOIgualAIngreso() {
        PrepararDatosRecomendacionTask.registrarSinAhorro(token);
    }

    @Given("ha ahorrado al menos el 20% de sus ingresos en el mes actual y en el mes anterior")
    public void haAhorradoAlMenos20PorCientoConsecutivo() {
        PrepararDatosRecomendacionTask.registrarAhorroSaludableConsecutivo(token);
    }

    // ─── Whens ──────────────────────────────────────────────────────────────────

    @When("solicita las recomendaciones del mes de sobregasto")
    public void solicitaRecomendacionesMesSobregasto() {
        response = ConsultarRecomendacionTask.recomendacionesOverspending(token);
    }

    @When("solicita las recomendaciones del mes sin ahorro")
    public void solicitaRecomendacionesMesSinAhorro() {
        response = ConsultarRecomendacionTask.recomendacionesNoSavings(token);
    }

    @When("solicita las recomendaciones del mes actual")
    public void solicitaRecomendacionesMesActual() {
        response = ConsultarRecomendacionTask.recomendacionesMesActual(token);
    }

    @When("solicita las recomendaciones para un período sin transacciones")
    public void solicitaRecomendacionesMesVacio() {
        response = ConsultarRecomendacionTask.recomendacionesMesVacio(token);
    }

    @When("solicita las recomendaciones para un período futuro")
    public void solicitaRecomendacionesPeriodoFuturo() {
        response = ConsultarRecomendacionTask.recomendacionesPeriodoFuturo(token);
    }

    @When("solicita las recomendaciones con un mes fuera del rango permitido")
    public void solicitaRecomendacionesMesInvalido() {
        response = ConsultarRecomendacionTask.recomendacionesMesInvalido(token);
    }

    @When("solicita las recomendaciones sin enviar el parámetro de mes")
    public void solicitaRecomendacionesSinParametroMes() {
        response = ConsultarRecomendacionTask.recomendacionesSinParametroMes(token);
    }

    @When("solicita las recomendaciones con un valor de mes no numérico")
    public void solicitaRecomendacionesParametroNoNumerico() {
        response = ConsultarRecomendacionTask.recomendacionesParametroNoNumerico(token);
    }

    // ─── Thens ──────────────────────────────────────────────────────────────────

    @Then("el sistema retorna las recomendaciones con código {int}")
    public void sistemaRetornaRecomendacionesConCodigo(int codigoEsperado) {
        assertThat(RespuestaRecomendacion.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("la lista contiene una recomendación de tipo {string}")
    public void listaContieneRecomendacionDeTipo(String tipo) {
        assertThat(RespuestaRecomendacion.contieneRecomendacionDeTipo(response, tipo))
                .as("La lista debe contener una recomendación de tipo " + tipo)
                .isTrue();
    }

    @Then("la recomendación incluye el porcentaje actual y el porcentaje recomendado")
    public void recomendacionIncluyePorcentajes() {
        assertThat(RespuestaRecomendacion.tienePortentajesDefinidos(response, "CATEGORY_OVERSPENDING"))
                .as("La recomendación CATEGORY_OVERSPENDING debe incluir currentPercentage y recommendedPercentage")
                .isTrue();
    }

    @Then("la recomendación sugiere al menos una categoría específica")
    public void recomendacionSugiereCategoria() {
        assertThat(RespuestaRecomendacion.tieneCategoriaDefinida(response, "NO_SAVINGS"))
                .as("La recomendación NO_SAVINGS debe incluir una categoría específica")
                .isTrue();
    }

    @Then("la recomendación incluye exactamente 3 sugerencias de inversión ordenadas por riesgo")
    public void recomendacionIncluyeTresSugerencias() {
        assertThat(RespuestaRecomendacion.cantidadSugerencias(response, "HEALTHY_SAVINGS"))
                .as("La recomendación HEALTHY_SAVINGS debe incluir exactamente 3 sugerencias")
                .isEqualTo(3);
    }

    @Then("la lista de recomendaciones está vacía")
    public void listaDeRecomendacionesEstaVacia() {
        assertThat(RespuestaRecomendacion.listaEstaVacia(response))
                .as("La lista de recomendaciones debe estar vacía para un período sin datos")
                .isTrue();
    }

    @Then("el sistema rechaza la solicitud de recomendaciones con código {int}")
    public void sistemaRechazaSolicitudRecomendacionesConCodigo(int codigoEsperado) {
        assertThat(RespuestaRecomendacion.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("la respuesta contiene el error {string}")
    public void respuestaContieneElError(String mensajeEsperado) {
        assertThat(RespuestaRecomendacion.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener el error: " + mensajeEsperado)
                .isTrue();
    }
}