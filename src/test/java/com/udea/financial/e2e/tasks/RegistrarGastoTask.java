package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.LocalDate;

public class RegistrarGastoTask {

    private static final String ENDPOINT = "/api/expenses";

    public static Response conDatosValidos(String token, Long categoryId) {
        String body = String.format(
                "{\"amount\":150.00,\"description\":\"Mercado semanal\",\"date\":\"%s\",\"categoryId\":%d}",
                LocalDate.now(), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }

    public static Response conFechaFutura(String token, Long categoryId) {
        String body = String.format(
                "{\"amount\":150.00,\"description\":\"Gasto futuro\",\"date\":\"%s\",\"categoryId\":%d}",
                LocalDate.now().plusDays(5), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }

    public static Response conMontoInvalido(String token, Long categoryId) {
        String body = String.format(
                "{\"amount\":0.00,\"description\":\"Gasto sin monto\",\"date\":\"%s\",\"categoryId\":%d}",
                LocalDate.now(), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }
}