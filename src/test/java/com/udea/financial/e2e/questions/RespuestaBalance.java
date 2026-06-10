package com.udea.financial.e2e.questions;

import io.restassured.response.Response;
import java.math.BigDecimal;

public class RespuestaBalance {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static String obtenerStatus(Response response) {
        return response.jsonPath().getString("status");
    }

    public static BigDecimal obtenerBalance(Response response) {
        return response.jsonPath().getObject("balance", BigDecimal.class);
    }

    public static BigDecimal obtenerTotalIngresos(Response response) {
        return response.jsonPath().getObject("totalIncomes", BigDecimal.class);
    }

    public static BigDecimal obtenerTotalGastos(Response response) {
        return response.jsonPath().getObject("totalExpenses", BigDecimal.class);
    }

    public static String obtenerAlerta(Response response) {
        return response.jsonPath().getString("alert");
    }

    public static boolean cuerpoContiene(Response response, String texto) {
        return response.getBody().asString().contains(texto);
    }
}