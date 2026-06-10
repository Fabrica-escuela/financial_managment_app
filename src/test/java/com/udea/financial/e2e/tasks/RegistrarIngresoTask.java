package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.LocalDate;

public class RegistrarIngresoTask {

    private static final String ENDPOINT = "/api/incomes";

    public static Response conDatosValidos(String token, Long categoryId) {
        String body = String.format(
                "{\"amount\":2500.00,\"description\":\"Salario mensual\",\"date\":\"%s\",\"categoryId\":%d}",
                LocalDate.now(), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }

    public static Response conMontoInvalido(String token, Long categoryId) {
        String body = String.format(
                "{\"amount\":0.00,\"description\":\"Salario mensual\",\"date\":\"%s\",\"categoryId\":%d}",
                LocalDate.now(), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }

    public static Response conCategoriaInexistente(String token) {
        String body = String.format(
                "{\"amount\":1500.00,\"description\":\"Ingreso sin categoría\",\"date\":\"%s\",\"categoryId\":999999}",
                LocalDate.now()
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }
}