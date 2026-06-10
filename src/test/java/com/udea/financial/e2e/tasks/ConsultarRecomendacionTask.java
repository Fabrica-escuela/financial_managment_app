package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.YearMonth;

public class ConsultarRecomendacionTask {

    private static final String ENDPOINT = "/api/recommendations";

    public static Response recomendacionesOverspending(String token) {
        YearMonth mes = PrepararDatosRecomendacionTask.mesOverspending();
        String url = ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesNoSavings(String token) {
        YearMonth mes = PrepararDatosRecomendacionTask.mesNoSavings();
        String url = ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesMesActual(String token) {
        YearMonth mes = PrepararDatosRecomendacionTask.mesActual();
        String url = ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesMesVacio(String token) {
        YearMonth mes = PrepararDatosRecomendacionTask.mesVacio();
        String url = ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesPeriodoFuturo(String token) {
        YearMonth futuro = YearMonth.now().plusMonths(1);
        String url = ENDPOINT + "?year=" + futuro.getYear() + "&month=" + futuro.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesMesInvalido(String token) {
        YearMonth ref = PrepararDatosRecomendacionTask.mesActual();
        String url = ENDPOINT + "?year=" + ref.getYear() + "&month=13";
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesSinParametroMes(String token) {
        YearMonth ref = PrepararDatosRecomendacionTask.mesActual();
        String url = ENDPOINT + "?year=" + ref.getYear();
        return ApiInteraction.getWithReport(url, token);
    }

    public static Response recomendacionesParametroNoNumerico(String token) {
        YearMonth ref = PrepararDatosRecomendacionTask.mesActual();
        String url = ENDPOINT + "?year=" + ref.getYear() + "&month=abc";
        return ApiInteraction.getWithReport(url, token);
    }
}