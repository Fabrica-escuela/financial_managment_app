package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

public class IniciarSesionTask {

    private static final String ENDPOINT = "/api/auth/login";

    public static Response conCredencialesValidas(String correo, String password) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                correo, password
        );
        return ApiInteraction.post(ENDPOINT, body);
    }

    public static Response conPasswordIncorrecto(String correo, String passwordIncorrecto) {
        String body = String.format(
                "{\"email\":\"%s\",\"password\":\"%s\"}",
                correo, passwordIncorrecto
        );
        return ApiInteraction.post(ENDPOINT, body);
    }

    public static Response conUsuarioInexistente() {
        String body = "{\"email\":\"usuario.fantasma.e2e@noexiste.com\",\"password\":\"cualquierPass123\"}";
        return ApiInteraction.post(ENDPOINT, body);
    }

    public static Response simularIntentosFallidosYBloquear(String correo, String passwordIncorrecto) {
        Response ultimaRespuesta = null;
        for (int i = 0; i < 5; i++) {
            ultimaRespuesta = conPasswordIncorrecto(correo, passwordIncorrecto);
        }
        return ultimaRespuesta;
    }
}