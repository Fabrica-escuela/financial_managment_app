package com.udea.financial.e2e.screenplay.tasks;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.HashMap;
import java.util.Map;

public class CrearPresupuesto implements Task {

    private final Long categoriaId;
    private final Double montoMaximo;
    private final String token;

    public CrearPresupuesto(Long categoriaId, Double montoMaximo, String token) {
        this.categoriaId = categoriaId;
        this.montoMaximo = montoMaximo;
        this.token = token;
    }

    public static CrearPresupuesto conDatos(Long categoriaId, Double montoMaximo, String token) {
        return Tasks.instrumented(CrearPresupuesto.class, categoriaId, montoMaximo, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Map<String, Object> body = new HashMap<>();
        body.put("categoryId", categoriaId);
        body.put("maxAmount", montoMaximo);

        actor.attemptsTo(
                Post.to("/api/budgets")
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + token)
                                .contentType(ContentType.JSON)
                                .body(body)
                        )
        );
    }
}