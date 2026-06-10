package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

public class GestionarCategoriaTask {

    private static final String ENDPOINT = "/api/categories";

    public static Response crearCategoria(String token, String nombre, String tipo) {
        String body = String.format(
                "{\"name\":\"%s\",\"type\":\"%s\"}",
                nombre, tipo
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }
}