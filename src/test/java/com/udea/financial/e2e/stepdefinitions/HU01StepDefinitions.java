package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaRegistro;
import com.udea.financial.e2e.tasks.RegistrarUsuarioTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU01StepDefinitions {

    private Response response;

    /**
     * Genera un correo único por ejecución usando un UUID corto,
     * garantizando que el escenario exitoso nunca colisione con registros previos.
     */
    private String generarCorreoUnico() {
        String sufijo = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return "e2e.registro." + sufijo + "@example.com";
    }

    @Given("que soy un usuario sin cuenta en la aplicación")
    public void usuarioSinCuenta() {
        // Precondición implícita: el usuario no existe en el sistema
    }

    @Given("que ya existe una cuenta registrada con el correo {string}")
    public void existeCuentaConCorreo(String correo) {
        // La cuenta teste2e_nuevo1@gmail.com ya existe en el sistema
        // No se hace ninguna acción, es una precondición del ambiente
    }

    @When("me registro con un nombre y correo únicos generados automáticamente y contraseña {string}")
    public void meRegistroConDatosUnicos(String password) {
        String correoUnico = generarCorreoUnico();
        String nombreUnico = "Usuario E2E " + correoUnico.substring(0, 12);
        response = RegistrarUsuarioTask.conDatosValidos(nombreUnico, correoUnico, password);
    }

    @When("me registro con nombre {string}, correo {string} y contraseña {string}")
    public void meRegistroConDatos(String nombre, String correo, String password) {
        response = RegistrarUsuarioTask.conDatosValidos(nombre, correo, password);
    }

    @When("intento registrarme con ese mismo correo y contraseña {string}")
    public void intentoRegistrarmeConCorreoExistente(String password) {
        response = RegistrarUsuarioTask.conDatosValidos(
                "Usuario Duplicado",
                "teste2e_nuevo1@gmail.com",
                password
        );
    }

    @When("intento registrarme con todos los campos vacíos")
    public void intentoRegistrarmeConCamposVacios() {
        response = RegistrarUsuarioTask.conCamposVacios();
    }

    @Then("el sistema crea mi cuenta exitosamente con código {int}")
    public void sistemaCreaLaCuenta(int codigoEsperado) {
        assertThat(RespuestaRegistro.obtenerCodigoHttp(response))
                .as("El código HTTP de respuesta debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el sistema rechaza el registro con código {int}")
    public void sistemaRechazaElRegistro(int codigoEsperado) {
        assertThat(RespuestaRegistro.obtenerCodigoHttp(response))
                .as("El código HTTP de respuesta debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("muestra el mensaje de error {string}")
    public void muestraMensajeDeError(String mensajeEsperado) {
        assertThat(RespuestaRegistro.detallesContienen(response, mensajeEsperado))
                .as("El cuerpo de la respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }

    @Then("muestra el mensaje de validación {string}")
    public void muestraMensajeDeValidacion(String textoEsperado) {
        assertThat(RespuestaRegistro.detallesContienen(response, textoEsperado))
                .as("El cuerpo de la respuesta debe contener: " + textoEsperado)
                .isTrue();
    }
}