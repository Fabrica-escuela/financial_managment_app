package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

public class RegistrarUsuarioTask {

    private static final String ENDPOINT = "/api/users";

    public static Response conDatosValidos(String nombre, String correo, String password) {
        String body = String.format(
                "{\"name\":\"%s\",\"email\":\"%s\",\"password\":\"%s\"}",
                nombre, correo, password
        );
        return ApiInteraction.post(ENDPOINT, body);
    }

    public static Response conCamposVacios() {
        String body = "{\"name\":\"\",\"email\":\"\",\"password\":\"\"}";
        return ApiInteraction.post(ENDPOINT, body);
    }
}