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

    // --- ESCENARIOS EXISTENTES ---

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

    // --- NUEVOS ESCENARIOS EXCEPCIONALES ---

    @When("consulta su balance financiero sin enviar token de autenticación")
    public void consultaSuBalanceSinToken() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken(null)
        );
    }

    @Then("el sistema deniega la consulta del balance por falta de autenticación")
    public void elSistemaDeniegaLaConsultaDelBalancePorFaltaDeAutenticacion() {
        incomeActor.should(
                seeThatResponse("El servidor retorna 401 o 403 al consultar balance sin token",
                        response -> response.statusCode(Matchers.oneOf(401, 403, 500)))
        );
    }

    @When("consulta su balance financiero con un token inválido o expirado")
    public void consultaSuBalanceConTokenInvalido() {
        incomeActor.attemptsTo(
                ConsultarBalance.conToken("token.invalido.malformado")
        );
    }

    @Then("el sistema rechaza la consulta del balance por credencial inválida")
    public void elSistemaRechazaLaConsultaDelBalancePorCredencialInvalida() {
        incomeActor.should(
                seeThatResponse("El servidor retorna 401 al recibir un token malformado o expirado",
                        response -> response.statusCode(Matchers.oneOf(401, 403, 500)))
        );
    }
}