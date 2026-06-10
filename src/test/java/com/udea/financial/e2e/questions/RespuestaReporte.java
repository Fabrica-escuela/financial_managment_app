package com.udea.financial.e2e.questions;

import io.restassured.response.Response;
import java.util.List;

public class RespuestaReporte {

    public static int obtenerCodigoHttp(Response response) {
        return response.getStatusCode();
    }

    public static boolean cuerpoContiene(Response response, String texto) {
        return response.getBody().asString().contains(texto);
    }

    // ─── Reporte mensual ────────────────────────────────────────────────────────

    public static boolean distribucionNoEstaVacia(Response response) {
        List<?> distribution = response.jsonPath().getList("distribution");
        return distribution != null && !distribution.isEmpty();
    }

    public static boolean topCategoriasNoEstaVacio(Response response) {
        List<?> top = response.jsonPath().getList("topCategories");
        return top != null && !top.isEmpty();
    }

    public static int cantidadTopCategorias(Response response) {
        List<?> top = response.jsonPath().getList("topCategories");
        return top == null ? 0 : top.size();
    }

    // ─── Comparación ────────────────────────────────────────────────────────────

    public static boolean comparacionNoEstaVacia(Response response) {
        List<?> comparisons = response.jsonPath().getList("comparisons");
        return comparisons != null && !comparisons.isEmpty();
    }

    public static boolean existeCategoriaConGastoAumentado(Response response) {
        List<Boolean> increased = response.jsonPath().getList("comparisons.increased");
        return increased != null && increased.stream().anyMatch(Boolean.TRUE::equals);
    }

    // ─── Errores ─────────────────────────────────────────────────────────────────

    public static String obtenerMensajeError(Response response) {
        return response.jsonPath().getString("message");
    }
}