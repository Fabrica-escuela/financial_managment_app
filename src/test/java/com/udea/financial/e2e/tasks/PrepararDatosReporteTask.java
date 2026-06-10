package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.UUID;

public class PrepararDatosReporteTask {

    private static final String CATEGORIES_ENDPOINT = "/api/categories";
    private static final String EXPENSES_ENDPOINT   = "/api/expenses";

    /**
     * Mes de referencia para el reporte mensual: mes actual.
     * Aquí ya existen datos reales en la BD.
     */
    public static YearMonth mesReferenciaReporte() {
        return YearMonth.now();
    }

    /**
     * Mes reciente para comparación: mes actual (month2).
     */
    public static YearMonth mesReferenciaComparacionReciente() {
        return YearMonth.now();
    }

    /**
     * Mes anterior para comparación: mes pasado (month1).
     */
    public static YearMonth mesReferenciaComparacionAnterior() {
        return YearMonth.now().minusMonths(1);
    }

    /**
     * Mes vacío garantizado: 6 meses atrás, sin transacciones del usuario de prueba.
     */
    public static YearMonth mesVacio() {
        return YearMonth.of(2025, 1);
    }

    /**
     * Crea una categoría de gasto con nombre único y retorna su ID.
     */
    public static Long crearCategoriaGasto(String token, String sufijo) {
        String nombre = "RPT_EXP_E2E_" + sufijo;
        String body = String.format("{\"name\":\"%s\",\"type\":\"EXPENSE\"}", nombre);
        Response response = ApiInteraction.postWithAuth(CATEGORIES_ENDPOINT, body, token);
        if (response.getStatusCode() == 201) {
            return response.jsonPath().getLong("id");
        }
        Response lista = ApiInteraction.getWithAuth(CATEGORIES_ENDPOINT + "?type=EXPENSE", token);
        return lista.jsonPath()
                .getList("findAll { it.name == '" + nombre + "' }.id", Long.class)
                .getFirst();
    }

    /**
     * Registra un gasto en el día 15 del mes indicado.
     */
    public static void registrarGastoEnMes(String token, Long categoryId, double monto, YearMonth mes) {
        // Si es el mes actual usamos el día de hoy para no caer en fecha futura
        LocalDate fecha = mes.equals(YearMonth.now())
                ? LocalDate.now()
                : mes.atDay(15);
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Gasto reporte E2E\",\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId
        );
        ApiInteraction.postWithAuth(EXPENSES_ENDPOINT, body, token);
    }

    /**
     * Registra tres categorías con gastos distintos en el mes actual.
     * Exclusivo del escenario "reporte mensual con datos".
     */
    public static void registrarGastosDistribuidos(String token) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        YearMonth mes = mesReferenciaReporte();

        Long cat1 = crearCategoriaGasto(token, "A_" + sufijo);
        Long cat2 = crearCategoriaGasto(token, "B_" + sufijo);
        Long cat3 = crearCategoriaGasto(token, "C_" + sufijo);

        registrarGastoEnMes(token, cat1, 500000.00, mes);
        registrarGastoEnMes(token, cat2, 300000.00, mes);
        registrarGastoEnMes(token, cat3, 200000.00, mes);
    }

    /**
     * Registra una categoría en dos meses distintos para el escenario de comparación.
     * month1 = mes anterior con gasto menor.
     * month2 = mes actual con gasto mayor → increased = true.
     */
    public static void registrarGastosParaComparacion(String token) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        YearMonth mesReciente = mesReferenciaComparacionReciente();
        YearMonth mesAnterior = mesReferenciaComparacionAnterior();

        Long cat = crearCategoriaGasto(token, "CMP_" + sufijo);

        registrarGastoEnMes(token, cat, 100000.00, mesAnterior);
        registrarGastoEnMes(token, cat, 400000.00, mesReciente);
    }
}