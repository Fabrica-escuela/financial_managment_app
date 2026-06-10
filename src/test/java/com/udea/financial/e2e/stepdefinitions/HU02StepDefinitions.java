package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.interactions.ApiInteraction;
import com.udea.financial.e2e.questions.RespuestaLogin;
import com.udea.financial.e2e.tasks.IniciarSesionTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU02StepDefinitions {

    private static final String CORREO_EXISTENTE  = "teste2e_nuevo1@gmail.com";
    private static final String PASSWORD_VALIDO   = "securePass123";
    private static final String PASSWORD_INVALIDO = "passwordMalo999";

    private Response response;
    private String   correoBloqueo;

    // ─── Helpers ───────────────────────────────────────────────────────────────

    /**
     * Crea un usuario nuevo y único exclusivamente para el escenario de bloqueo.
     * De esta forma, el usuario compartido (teste2e_nuevo1) nunca acumula
     * intentos fallidos y los demás tests no se ven afectados.
     */
    private String crearUsuarioParaBloqueo() {
        String sufijo   = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
        String correo   = "e2e.bloqueo." + sufijo + "@example.com";
        String nombre   = "Usuario Bloqueo E2E";
        String password = PASSWORD_VALIDO;

        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                nombre, correo, password
        );
        Response registro = ApiInteraction.post("/api/users", body);
        if (registro.getStatusCode() != 201) {
            throw new RuntimeException("No se pudo crear el usuario de bloqueo para HU-02: "
                    + registro.getStatusCode() + " - " + registro.getBody().asString());
        }
        return correo;
    }

    // ─── Givens ────────────────────────────────────────────────────────────────

    @Given("que el usuario cuenta con una cuenta activa en el sistema")
    public void usuarioConCuentaActiva() {
        // Precondición: teste2e_nuevo1@gmail.com ya existe y está activo en el sistema
    }

    @Given("que el usuario existe en el sistema")
    public void usuarioExisteEnElSistema() {
        // Precondición: teste2e_nuevo1@gmail.com ya existe en el sistema
    }

    @Given("que el usuario no existe en el sistema")
    public void usuarioNoExisteEnElSistema() {
        // Precondición: el correo fantasma no está registrado
    }

    @Given("que se han registrado 5 intentos fallidos de acceso consecutivos")
    public void cincoIntentosFallidosRegistrados() {
        // Creamos un usuario aislado para este escenario y acumulamos
        // los 5 intentos fallidos sobre él, sin tocar el usuario compartido.
        correoBloqueo = crearUsuarioParaBloqueo();
        response = IniciarSesionTask.simularIntentosFallidosYBloquear(
                correoBloqueo,
                PASSWORD_INVALIDO
        );
    }

    // ─── Whens ─────────────────────────────────────────────────────────────────

    @When("proporciona sus credenciales de acceso válidas")
    public void proporcionaCredencialesValidas() {
        response = IniciarSesionTask.conCredencialesValidas(
                CORREO_EXISTENTE,
                PASSWORD_VALIDO
        );
    }

    @When("se suministra una clave de acceso incorrecta")
    public void suministraClaveIncorrecta() {
        response = IniciarSesionTask.conPasswordIncorrecto(
                CORREO_EXISTENTE,
                PASSWORD_INVALIDO
        );
    }

    @When("intenta autenticarse con credenciales de un usuario inexistente")
    public void intentaAutenticarseConUsuarioInexistente() {
        response = IniciarSesionTask.conUsuarioInexistente();
    }

    @When("se intenta realizar una nueva validación de identidad")
    public void intentaNuevaValidacionConCuentaBloqueada() {
        // El Given ya dejó la cuenta bloqueada. Hacemos un intento más
        // sobre el mismo usuario aislado para verificar que sigue bloqueado.
        response = IniciarSesionTask.conPasswordIncorrecto(
                correoBloqueo,
                PASSWORD_INVALIDO
        );
    }

    // ─── Thens ─────────────────────────────────────────────────────────────────

    @Then("se le permite el ingreso al sistema")
    public void seLaPermiteElIngreso() {
        assertThat(RespuestaLogin.obtenerCodigoHttp(response))
                .as("El login exitoso debe retornar HTTP 200")
                .isEqualTo(200);
    }

    @Then("obtiene la información de su resumen de saldos actualizado")
    public void obtieneResumenDeSaldos() {
        assertThat(RespuestaLogin.tieneToken(response))
                .as("La respuesta debe contener un token JWT válido")
                .isTrue();
    }

    @Then("el sistema restringe el ingreso notificando datos erróneos")
    public void sistemaRestrigeElIngreso() {
        assertThat(RespuestaLogin.obtenerCodigoHttp(response))
                .as("El sistema debe retornar HTTP 401 ante credenciales inválidas")
                .isEqualTo(401);
    }

    @Then("el sistema suspende temporalmente el acceso por seguridad durante 15 minutos")
    public void sistemaSuspendeElAcceso() {
        assertThat(RespuestaLogin.obtenerCodigoHttp(response))
                .as("El sistema debe retornar HTTP 423 cuando la cuenta está bloqueada")
                .isEqualTo(423);
        assertThat(RespuestaLogin.obtenerErrorCode(response))
                .as("El errorCode en el cuerpo debe ser 423")
                .isEqualTo(423);
    }
}