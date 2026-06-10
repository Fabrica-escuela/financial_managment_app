package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

public class ObtenerTokenTask {

    private static final String LOGIN_ENDPOINT    = "/api/auth/login";
    private static final String CORREO            = "teste2e_nuevo1@gmail.com";
    private static final String PASSWORD          = "securePass123";

    public static String obtenerToken() {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                CORREO, PASSWORD
        );
        Response response = ApiInteraction.post(LOGIN_ENDPOINT, body);
        return response.jsonPath().getString("token");
    }
}