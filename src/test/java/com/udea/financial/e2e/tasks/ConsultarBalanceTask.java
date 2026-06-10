package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.YearMonth;

public class ConsultarBalanceTask {

    private static final String ENDPOINT         = "/api/balance";
    private static final String ENDPOINT_MONTHLY = "/api/balance/monthly";

    public static Response consultarBalanceMesActual(String token) {
        return ApiInteraction.getWithAuth(ENDPOINT, token);
    }

    /**
     * Consulta el balance de un año y mes específicos.
     * Se usa en los escenarios E2E para consultar el mes de referencia controlado.
     */
    public static Response consultarBalanceMes(String token, int year, int month) {
        String url = ENDPOINT_MONTHLY + "?year=" + year + "&month=" + month;
        return ApiInteraction.getWithAuth(url, token);
    }
}