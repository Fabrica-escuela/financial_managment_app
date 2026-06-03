package com.udea.financial.e2e.screenplay.tasks;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.HashMap;
import java.util.Map;

public class RegistrarCategoria implements Task {

    private final String nombre;
    private final String tipo;
    private final String token;

    public RegistrarCategoria(String nombre, String tipo, String token) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.token = token;
    }

    public static RegistrarCategoria conDatos(String nombre, String tipo, String token) {
        return Tasks.instrumented(RegistrarCategoria.class, nombre, tipo, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", nombre);
        body.put("type", tipo);

        actor.attemptsTo(
                Post.to("/api/categories")
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + token)
                                .contentType(ContentType.JSON)
                                .body(body)
                        )
        );
    }
}