package com.udea.financial.e2e.questions;

import io.restassured.response.Response;

public class RespuestaLogin {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static boolean tieneToken(Response response) {
        String token = response.jsonPath().getString("token");
        return token != null && !token.isBlank();
    }

    public static int obtenerErrorCode(Response response) {
        return response.jsonPath().getInt("errorCode");
    }

    public static String obtenerMensaje(Response response) {
        return response.jsonPath().getString("message");
    }
}