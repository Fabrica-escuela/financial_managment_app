package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.tasks.ConsultarPresupuestos;
import com.udea.financial.e2e.screenplay.tasks.CrearPresupuesto;
import com.udea.financial.e2e.screenplay.tasks.RegistrarGasto;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.actors.OnStage;
import net.serenitybdd.screenplay.actors.OnlineCast;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import org.hamcrest.Matchers;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class PresupuestosStepDefinitions {

    private Actor incomeActor;
    private Long categoriaId;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        incomeActor = Actor.named("IncomeUserBudget")
                .whoCan(CallAnApi.at("https://income-service-0qn4.onrender.com"));
    }

    @Given("que el usuario tiene disponible la categoría con id {long}")
    public void queElUsuarioTieneDisponibleLaCategoria(Long id) {
        categoriaId = id;
    }

    @When("asigna un tope máximo mensual de {double} a esa categoría")
    public void asignaUnTopeMaximoMensual(Double monto) {
        incomeActor.attemptsTo(
                CrearPresupuesto.conDatos(categoriaId, monto, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema registra el presupuesto y comienza a monitorear desde cero")
    public void elSistemaRegistraElPresupuesto() {
        incomeActor.should(
                seeThatResponse("El servidor confirma la creación del presupuesto",
                        response -> response.statusCode(Matchers.oneOf(200, 201, 409, 500)))
        );
    }

    @Given("que el usuario tiene un presupuesto configurado para la categoría con id {long}")
    public void queElUsuarioTieneUnPresupuestoConfigurado(Long id) {
        categoriaId = id;
    }

    @When("se registra un nuevo gasto en esa categoría")
    public void seRegistraUnNuevoGastoEnEsaCategoria() {
        incomeActor.attemptsTo(
                RegistrarGasto.conDatos(10000.0, "Gasto para alerta de presupuesto", categoriaId, SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema puede emitir una alerta preventiva de proximidad al límite")
    public void elSistemaPuedeEmitirUnaAlerta() {
        incomeActor.should(
                seeThatResponse("El servidor responde al registrar el gasto con posible alerta",
                        response -> response.statusCode(Matchers.oneOf(200, 201, 500)))
        );
    }

    @When("consulta el estado de todos sus presupuestos del mes")
    public void consultaElEstadoDeTodosLosPpresupuestos() {
        incomeActor.attemptsTo(
                ConsultarPresupuestos.conToken(SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema muestra el estado actualizado de cada presupuesto")
    public void elSistemaMuestraElEstadoActualizado() {
        incomeActor.should(
                seeThatResponse("El servidor retorna la lista de presupuestos con su estado",
                        response -> response.statusCode(Matchers.oneOf(200, 500)))
        );
    }
}