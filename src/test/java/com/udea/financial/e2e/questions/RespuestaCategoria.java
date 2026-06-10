package com.udea.financial.e2e.questions;

import io.restassured.response.Response;

public class RespuestaCategoria {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static String obtenerNombre(Response response) {
        return response.jsonPath().getString("name");
    }

    public static boolean cuerpoContiene(Response response, String texto) {
        return response.getBody().asString().contains(texto);
    }
}