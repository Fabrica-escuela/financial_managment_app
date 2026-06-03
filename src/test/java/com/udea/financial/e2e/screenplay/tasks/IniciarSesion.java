package com.udea.financial.e2e.screenplay.tasks;

import com.udea.financial.e2e.screenplay.models.LoginCredentials;
import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

public class IniciarSesion implements Task {

    private final String email;
    private final String password;

    public IniciarSesion(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // Método Factoría (Fábrica) al estilo ScreenPlay para una lectura fluida
    public static IniciarSesion conCredenciales(String email, String password) {
        return Tasks.instrumented(IniciarSesion.class, email, password);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Construimos el cuerpo (Body) del JSON usando el modelo que creamos antes
        LoginCredentials credentials = LoginCredentials.builder()
                .email(email)
                .password(password)
                .build();

        // El actor ejecuta la interacción POST apuntando al endpoint de tu Swagger
        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(request -> request
                                .contentType(ContentType.JSON)
                                .body(credentials)
                        )
        );
    }
}