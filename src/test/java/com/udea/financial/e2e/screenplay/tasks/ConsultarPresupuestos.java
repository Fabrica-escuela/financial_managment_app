package com.udea.financial.e2e.screenplay.tasks;

import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Get;

public class ConsultarPresupuestos implements Task {

    private final String token;

    public ConsultarPresupuestos(String token) {
        this.token = token;
    }

    public static ConsultarPresupuestos conToken(String token) {
        return Tasks.instrumented(ConsultarPresupuestos.class, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Get.resource("/api/budgets")
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + token)
                        )
        );
    }
}