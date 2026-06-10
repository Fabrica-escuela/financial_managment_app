package com.udea.financial.e2e.questions;

import io.restassured.response.Response;

import java.util.List;

public class RespuestaRecomendacion {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static boolean cuerpoContiene(Response response, String texto) {
        return response.getBody().asString().contains(texto);
    }

    public static boolean listaEstaVacia(Response response) {
        List<?> lista = response.jsonPath().getList("$");
        return lista != null && lista.isEmpty();
    }

    public static boolean contieneRecomendacionDeTipo(Response response, String tipo) {
        List<String> tipos = response.jsonPath().getList("type");
        return tipos != null && tipos.contains(tipo);
    }

    private static int indiceDelTipo(Response response, String tipo) {
        List<String> tipos = response.jsonPath().getList("type");
        if (tipos == null) return -1;
        return tipos.indexOf(tipo);
    }

    public static boolean tienePortentajesDefinidos(Response response, String tipo) {
        int idx = indiceDelTipo(response, tipo);
        if (idx < 0) return false;
        String current     = response.jsonPath().getString("[" + idx + "].currentPercentage");
        String recommended = response.jsonPath().getString("[" + idx + "].recommendedPercentage");
        return current != null && !current.isBlank()
                && recommended != null && !recommended.isBlank();
    }

    public static boolean tieneCategoriaDefinida(Response response, String tipo) {
        int idx = indiceDelTipo(response, tipo);
        if (idx < 0) return false;
        String category = response.jsonPath().getString("[" + idx + "].category");
        return category != null && !category.isBlank();
    }

    public static int cantidadSugerencias(Response response, String tipo) {
        int idx = indiceDelTipo(response, tipo);
        if (idx < 0) return 0;
        List<?> sugerencias = response.jsonPath().getList("[" + idx + "].suggestions");
        return sugerencias == null ? 0 : sugerencias.size();
    }

    public static String obtenerMensajeError(Response response) {
        return response.jsonPath().getString("message");
    }
}