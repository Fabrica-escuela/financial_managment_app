package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

public class CrearCategoriaTask {

    private static final String ENDPOINT = "/api/categories";

    public static Long crearCategoriaIngreso(String token) {
        return crearCategoria(token, "SALARIO_E2E", "INCOME");
    }

    public static Long crearCategoriaGasto(String token) {
        return crearCategoria(token, "MERCADO_E2E", "EXPENSE");
    }

    private static Long crearCategoria(String token, String nombre, String tipo) {
        String body = String.format("{\"name\":\"%s\",\"type\":\"%s\"}", nombre, tipo);
        Response response = ApiInteraction.postWithAuth(ENDPOINT, body, token);

        if (response.getStatusCode() == 201) {
            return response.jsonPath().getLong("id");
        }
        // Si ya existe (409), la buscamos en el listado
        Response lista = ApiInteraction.getWithAuth(ENDPOINT + "?type=" + tipo, token);
        return lista.jsonPath()
                .getList("findAll { it.name == '" + nombre + "' }.id", Long.class)
                .getFirst();
    }
}