package com.udea.financial.e2e.screenplay.tasks;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegistrarIngreso implements Task {

    private final Double monto;
    private final String descripcion;
    private final Long categoriaId;
    private final String token;

    public RegistrarIngreso(Double monto, String descripcion, Long categoriaId, String token) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.token = token;
    }

    public static RegistrarIngreso conDatos(Double monto, String descripcion, Long categoriaId, String token) {
        return Tasks.instrumented(RegistrarIngreso.class, monto, descripcion, categoriaId, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Map<String, Object> ingresoBody = new HashMap<>();
        ingresoBody.put("amount", monto);
        ingresoBody.put("description", descripcion);
        ingresoBody.put("date", LocalDate.now().toString()); // Formato "YYYY-MM-DD"

        // Si no se especifica categoría (para el escenario fallido), no agregamos la llave
        if (categoriaId != null) {
            ingresoBody.put("categoryId", categoriaId);
        }

        String bearerToken = (token != null) ? token : "token_invalido_de_control";

        actor.attemptsTo(
                Post.to("/api/incomes") // Endpoint estándar en base a tu Income-service
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + bearerToken)
                                .contentType(ContentType.JSON)
                                .body(ingresoBody)
                        )
        );
    }
}