package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.math.BigDecimal;

public class GestionarPresupuestoTask {

    private static final String ENDPOINT = "/api/budgets";

    public static Response crearPresupuesto(String token, Long categoryId, BigDecimal maxAmount) {
        String body = String.format(
                "{\"categoryId\":%d,\"maxAmount\":%s}",
                categoryId, maxAmount.toPlainString()
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }

    public static Response consultarEstados(String token) {
        return ApiInteraction.getWithAuth(ENDPOINT, token);
    }
}