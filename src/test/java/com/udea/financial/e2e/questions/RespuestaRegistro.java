package com.udea.financial.e2e.questions;

import io.restassured.response.Response;

public class RespuestaRegistro {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static String obtenerMensaje(Response response) {
        return response.jsonPath().getString("message");
    }

    public static String obtenerPrimerDetalle(Response response) {
        return response.jsonPath().getString("details[0]");
    }

    public static boolean detallesContienen(Response response, String texto) {
        String detalles = response.getBody().asString();
        return detalles.contains(texto);
    }
}