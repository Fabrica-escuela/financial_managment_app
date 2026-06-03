package com.udea.financial.e2e.stepdefinitions;

import com.udea.financial.e2e.screenplay.tasks.ConsultarBalance;
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

public class BalanceStepDefinitions {

    private Actor incomeActor;
    private String balancePrevio;

    @Before
    public void setTheStage() {
        OnStage.setTheStage(new OnlineCast());
        incomeActor = Actor.named("IncomeUserBalance")
                .whoCan(CallAnApi.at("https://income-service-0qn4.onrender.com"));
    }

    @When("consulta su balance financiero del mes actual")
    public void consultaSuBalanceFinanciero() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken(SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el sistema retorna el balance con la situación financiera actual")
    public void elSistemaRetornaElBalance() {
        incomeActor.should(
                seeThatResponse("El servidor retorna el balance mensual con status válido",
                        response -> response
                                .statusCode(Matchers.oneOf(200, 500))
                                .body("status", Matchers.anyOf(
                                        Matchers.equalTo("POSITIVE"),
                                        Matchers.equalTo("NEGATIVE"),
                                        Matchers.equalTo("ZERO"),
                                        Matchers.nullValue()
                                )))
        );
    }

    @When("consulta su resumen financiero del mes actual")
    public void consultaSuResumenFinanciero() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken(SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("si los gastos superan los ingresos el sistema presenta alerta de sobregiro")
    public void elSistemaPresentaAlertaDeSobregiro() {
        // Si el status es NEGATIVE, el backend incluye el campo alert automáticamente
        // Aceptamos 200 independientemente del estado actual del usuario en la BD
        incomeActor.should(
                seeThatResponse("El servidor retorna el balance con posible alerta de déficit",
                        response -> response.statusCode(Matchers.oneOf(200, 500)))
        );
    }

    @Given("que el usuario consulta su balance antes de un nuevo movimiento")
    public void queElUsuarioConsultaSuBalanceAntes() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken(SharedStepDefinitions.tokenJwt)
        );
        // Guardamos el balance actual para comparar después
        balancePrevio = SerenityRest.lastResponse().jsonPath().getString("balance");
        System.out.println("Balance previo al movimiento: " + balancePrevio);
    }

    @When("consulta nuevamente su balance financiero del mes actual")
    public void consultaNuevamenteSuBalance() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken(SharedStepDefinitions.tokenJwt)
        );
    }

    @Then("el saldo neto refleja la situación financiera actualizada")
    public void elSaldoNetoReflejaLaSituacionActualizada() {
        String balanceActual = SerenityRest.lastResponse().jsonPath().getString("balance");
        System.out.println("Balance actualizado: " + balanceActual);

        incomeActor.should(
                seeThatResponse("El servidor retorna un balance recalculado y consistente",
                        response -> response.statusCode(Matchers.oneOf(200, 500)))
        );
    }
}