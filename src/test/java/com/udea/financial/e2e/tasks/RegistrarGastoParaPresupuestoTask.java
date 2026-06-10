package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;
import java.time.LocalDate;
import java.util.Locale; // Importante

public class RegistrarGastoParaPresupuestoTask {

    private static final String ENDPOINT = "/api/expenses";

    public static Response registrarGasto(String token, Long categoryId, double monto) {
        // Usamos Locale.US para obligar a que el separador sea punto "."
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Gasto para test presupuesto\",\"date\":\"%s\",\"categoryId\":%d}",
                monto, LocalDate.now(), categoryId
        );
        return ApiInteraction.postWithAuth(ENDPOINT, body, token);
    }
}