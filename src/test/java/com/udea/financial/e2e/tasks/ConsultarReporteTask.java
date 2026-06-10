package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.YearMonth;

public class ConsultarReporteTask {

    private static final String MONTHLY_ENDPOINT    = "/api/reports/monthly";
    private static final String COMPARISON_ENDPOINT = "/api/reports/comparison";

    /**
     * Reporte mensual para el mes actual (donde hay datos reales y del escenario).
     */
    public static Response reporteMensualConDatos(String token) {
        YearMonth mes = PrepararDatosReporteTask.mesReferenciaReporte();
        String url = MONTHLY_ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    /**
     * Reporte mensual para un mes vacío garantizado: 6 meses atrás.
     */
    public static Response reporteMensualMesVacio(String token) {
        YearMonth mes = PrepararDatosReporteTask.mesVacio();
        String url = MONTHLY_ENDPOINT + "?year=" + mes.getYear() + "&month=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    /**
     * Reporte mensual apuntando a un mes futuro → debe retornar 400.
     */
    public static Response reporteMensualPeriodoFuturo(String token) {
        YearMonth futuro = YearMonth.now().plusMonths(1);
        String url = MONTHLY_ENDPOINT + "?year=" + futuro.getYear() + "&month=" + futuro.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    /**
     * Reporte mensual con mes fuera del rango 1-12 → debe retornar 400.
     */
    public static Response reporteMensualMesInvalido(String token) {
        YearMonth ref = PrepararDatosReporteTask.mesReferenciaReporte();
        String url = MONTHLY_ENDPOINT + "?year=" + ref.getYear() + "&month=13";
        return ApiInteraction.getWithReport(url, token);
    }

    /**
     * Comparación: month1 = mes anterior, month2 = mes actual.
     * Ambos meses tienen datos registrados por el escenario de preparación.
     */
    public static Response comparacionDosMeses(String token) {
        YearMonth mesReciente = PrepararDatosReporteTask.mesReferenciaComparacionReciente();
        YearMonth mesAnterior = PrepararDatosReporteTask.mesReferenciaComparacionAnterior();
        String url = COMPARISON_ENDPOINT
                + "?year1=" + mesAnterior.getYear()  + "&month1=" + mesAnterior.getMonthValue()
                + "&year2=" + mesReciente.getYear()   + "&month2=" + mesReciente.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }

    /**
     * Comparación enviando el mismo período dos veces → debe retornar 400.
     */
    public static Response comparacionMismoMes(String token) {
        YearMonth mes = PrepararDatosReporteTask.mesReferenciaReporte();
        String url = COMPARISON_ENDPOINT
                + "?year1=" + mes.getYear() + "&month1=" + mes.getMonthValue()
                + "&year2=" + mes.getYear() + "&month2=" + mes.getMonthValue();
        return ApiInteraction.getWithReport(url, token);
    }
}