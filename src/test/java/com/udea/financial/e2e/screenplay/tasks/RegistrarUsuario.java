package com.udea.financial.e2e.screenplay.tasks;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.HashMap;
import java.util.Map;

public class RegistrarUsuario implements Task {

    private final String email;
    private final String password;

    public RegistrarUsuario(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static RegistrarUsuario conDatos(String email, String password) {
        return Tasks.instrumented(RegistrarUsuario.class, email, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        Map<String, String> bodyRequest = new HashMap<>();
        bodyRequest.put("email", email);
        bodyRequest.put("password", password);
        // Nota: Si tu endpoint de registro en Swagger te pide más campos (como "name" o "username"),
        // agrégalos aquí usando bodyRequest.put("campo", valor);

        actor.attemptsTo(
                Post.to("/api/auth/register")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(bodyRequest)
                        )
        );
    }
}