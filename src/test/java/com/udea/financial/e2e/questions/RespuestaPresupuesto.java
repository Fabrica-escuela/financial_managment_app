package com.udea.financial.e2e.questions;

import io.restassured.response.Response;

public class RespuestaPresupuesto {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static String obtenerStatus(Response response) {
        return response.jsonPath().getString("status");
    }

    public static boolean cuerpoContiene(Response response, String texto) {
        return response.getBody().asString().contains(texto);
    }

    /**
     * Busca de manera robusta usando expresiones JsonPath el status del budgetId dado.
     */
    public static String obtenerStatusPorBudgetId(Response response, Long budgetId) {
        if (budgetId == null) return null;
        // La expresión "find { it.budgetId == %d }.status" busca dinámicamente en el array del JSON
        String path = String.format("find { it.budgetId == %d }.status", budgetId);
        return response.jsonPath().getString(path);
    }

    public static String obtenerPrimerStatus(Response response) {
        return response.jsonPath().getString("[0].status");
    }
}