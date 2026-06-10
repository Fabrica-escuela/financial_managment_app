package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.questions.RespuestaCategoria;
import com.udea.financial.e2e.tasks.GestionarCategoriaTask;
import com.udea.financial.e2e.tasks.ObtenerTokenTask;
import io.cucumber.java.en.*;
import io.restassured.response.Response;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class HU05StepDefinitions {

    private String   token;
    private Response response;

    /**
     * Genera un nombre de categoría único por ejecución usando un UUID corto,
     * garantizando que el escenario exitoso nunca colisione con registros previos.
     */
    private String generarNombreCategoriaUnico() {
        String sufijo = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "CAT_E2E_" + sufijo;
    }

    @Given("que el usuario tiene una sesión activa en el sistema de categorías")
    public void usuarioConSesionActivaCategorias() {
        token = ObtenerTokenTask.obtenerToken();
        assertThat(token)
                .as("El token JWT no debe ser nulo ni vacío")
                .isNotBlank();
    }

    @Given("ya existe una categoría con nombre {string} y tipo {string}")
    public void yaExisteUnaCategoria(String nombre, String tipo) {
        // Creamos la categoría base para garantizar que ya existe antes del escenario
        GestionarCategoriaTask.crearCategoria(token, nombre, tipo);
    }

    /**
     * Step dinámico: genera un nombre único en cada ejecución
     * para que el escenario exitoso nunca falle por duplicado.
     */
    @When("crea una categoría con nombre único generado automáticamente y tipo {string}")
    public void creaUnaCategoriaConNombreUnico(String tipo) {
        String nombreUnico = generarNombreCategoriaUnico();
        response = GestionarCategoriaTask.crearCategoria(token, nombreUnico, tipo);
    }

    @When("crea una categoría con nombre {string} y tipo {string}")
    public void creaUnaCategoria(String nombre, String tipo) {
        response = GestionarCategoriaTask.crearCategoria(token, nombre, tipo);
    }

    @When("intenta crear otra categoría con el mismo nombre {string} y tipo {string}")
    public void intentaCrearCategoriaDuplicada(String nombre, String tipo) {
        response = GestionarCategoriaTask.crearCategoria(token, nombre, tipo);
    }

    @Then("la categoría se crea exitosamente con código {int}")
    public void categoriaCreada(int codigoEsperado) {
        assertThat(RespuestaCategoria.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("el nombre de la categoría se guarda en mayúsculas")
    public void nombreEnMayusculas() {
        String nombre = RespuestaCategoria.obtenerNombre(response);
        assertThat(nombre)
                .as("El nombre debe estar en mayúsculas")
                .isEqualTo(nombre.toUpperCase());
    }

    @Then("el sistema rechaza la categoría con código {int}")
    public void sistemaRechazaCategoria(int codigoEsperado) {
        assertThat(RespuestaCategoria.obtenerCodigoHttp(response))
                .as("El código HTTP debe ser " + codigoEsperado)
                .isEqualTo(codigoEsperado);
    }

    @Then("muestra el mensaje de categoría {string}")
    public void muestraMensajeDeCategoria(String mensajeEsperado) {
        assertThat(RespuestaCategoria.cuerpoContiene(response, mensajeEsperado))
                .as("La respuesta debe contener: " + mensajeEsperado)
                .isTrue();
    }
}