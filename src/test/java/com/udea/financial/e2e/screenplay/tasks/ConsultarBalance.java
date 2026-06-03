package com.udea.financial.e2e.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarBalance implements Task {

    private final String token;

    public ConsultarBalance(String token) {
        this.token = token;
    }

    public static ConsultarBalance conToken(String token) {
        return Tasks.instrumented(ConsultarBalance.class, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/api/balance")
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}