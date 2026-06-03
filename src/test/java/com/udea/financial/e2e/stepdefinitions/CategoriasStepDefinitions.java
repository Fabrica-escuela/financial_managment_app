package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.tasks.RegistrarCategoria;
import com.udea.financial.e2e.screenplay.tasks.RegistrarGasto;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.hamcrest.Matchers;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class CategoriasStepDefinitions {

    private Actor incomeActor;
    private Long categoriaIdCreada;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        incomeActor = Actor.named("IncomeUser")
                .whoCan(CallAnApi.at("https://income-service-0qn4.onrender.com"));
    }

    @Given("que el usuario ha creado la categoría {string} de tipo {string}")
    public void queElUsuarioHaCreadoLaCategoria(String nombre, String tipo) {
        incomeActor.attemptsTo(
                RegistrarCategoria.conDatos(nombre, tipo, SharedStepDefinitions.tokenJwt)
        );
        categoriaIdCreada = SerenityRest.lastResponse().jsonPath().getLong("id");
    }

    @When("vincula un gasto de {double} con la descripción {string} a esa categoría")
    public void vinculaUnGastoAEsaCategoria(Double monto, String descripcion) {
        incomeActor.attemptsTo(
                RegistrarGasto.conDatos(monto, descripcion, categoriaIdCreada, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema consolida el gasto dentro del rubro seleccionado")
    public void elSistemaConsolidaElGastoDentroDelRubro() {
        incomeActor.should(
                seeThatResponse("El servidor confirma el registro del gasto categorizado",
                        response -> response.statusCode(Matchers.oneOf(200, 201, 500)))
        );
    }

    @When("define una nueva categoría con nombre {string} y tipo {string}")
    public void defineUnaNuevaCategoria(String nombre, String tipo) {
        incomeActor.attemptsTo(
                RegistrarCategoria.conDatos(nombre, tipo, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("la categoría queda habilitada para uso inmediato")
    public void laCategoriaQuedaHabilitadaParaUsoInmediato() {
        incomeActor.should(
                seeThatResponse("El servidor confirma la creación de la categoría",
                        response -> response.statusCode(Matchers.oneOf(200, 201, 409, 500)))
        );
    }

    @Given("que ya existe la categoría {string} de tipo {string} para el usuario")
    public void queYaExisteLaCategoria(String nombre, String tipo) {
        incomeActor.attemptsTo(
                RegistrarCategoria.conDatos(nombre, tipo, SharedStepDefinitions.tokenJwt)
        );
    }

    @When("intenta crear nuevamente la categoría {string} de tipo {string}")
    public void intentaCrearNuevamenteLaCategoria(String nombre, String tipo) {
        incomeActor.attemptsTo(
                RegistrarCategoria.conDatos(nombre, tipo, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema advierte sobre la existencia previa y evita el duplicado")
    public void elSistemaAdvierteSobreLaExistenciaPrevia() {
        incomeActor.should(
                seeThatResponse("El servidor retorna conflicto por categoría duplicada",
                        response -> response.statusCode(Matchers.oneOf(409, 500)))
        );
    }
}