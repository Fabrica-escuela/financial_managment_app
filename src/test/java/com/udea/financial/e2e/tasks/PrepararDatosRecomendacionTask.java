package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.UUID;

public class PrepararDatosRecomendacionTask {

    private static final String CATEGORIES_ENDPOINT = "/api/categories";
    private static final String INCOMES_ENDPOINT    = "/api/incomes";
    private static final String EXPENSES_ENDPOINT   = "/api/expenses";

    // ─── Meses de referencia ────────────────────────────────────────────────────

    /** Diciembre 2025 — garantizadamente vacío según la BD actual. */
    public static YearMonth mesOverspending() {
        return YearMonth.of(2025, 12);
    }

    /** Noviembre 2025 — garantizadamente vacío según la BD actual. */
    public static YearMonth mesNoSavings() {
        return YearMonth.of(2025, 11);
    }

    /** Mes actual — para HEALTHY_SAVINGS. */
    public static YearMonth mesActual() {
        return YearMonth.now();
    }

    /** Mes anterior — para HEALTHY_SAVINGS (segundo mes consecutivo). */
    public static YearMonth mesAnterior() {
        return YearMonth.now().minusMonths(1);
    }

    /** Octubre 2025 — garantizadamente vacío para el escenario sin datos. */
    public static YearMonth mesVacio() {
        return YearMonth.of(2025, 10);
    }

    // ─── Helpers internos ────────────────────────────────────────────────────────

    private static Long crearCategoria(String token, String nombre, String tipo) {
        String body = String.format("{\"name\":\"%s\",\"type\":\"%s\"}", nombre, tipo);
        Response resp = ApiInteraction.postWithAuth(CATEGORIES_ENDPOINT, body, token);
        if (resp.getStatusCode() == 201) return resp.jsonPath().getLong("id");
        Response lista = ApiInteraction.getWithAuth(CATEGORIES_ENDPOINT + "?type=" + tipo, token);
        return lista.jsonPath()
                .getList("findAll { it.name == '" + nombre + "' }.id", Long.class)
                .getFirst();
    }

    private static void registrarIngreso(String token, Long categoryId,
                                         double monto, YearMonth mes) {
        LocalDate fecha = mes.atDay(15);
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Ingreso REC E2E\","
                        + "\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId);
        Response resp = ApiInteraction.postWithAuth(INCOMES_ENDPOINT, body, token);
        System.out.println("POST INGRESO [" + mes + "] -> Status: "
                + resp.getStatusCode() + " -> Body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 201) {
            throw new RuntimeException("Fallo al registrar ingreso: "
                    + resp.getStatusCode() + " - " + resp.getBody().asString());
        }
    }

    private static void registrarGasto(String token, Long categoryId,
                                       double monto, YearMonth mes) {
        LocalDate fecha = mes.atDay(15);
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Gasto REC E2E\","
                        + "\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId);
        Response resp = ApiInteraction.postWithAuth(EXPENSES_ENDPOINT, body, token);
        System.out.println("POST GASTO [" + mes + "] -> Status: "
                + resp.getStatusCode() + " -> Body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 201) {
            throw new RuntimeException("Fallo al registrar gasto: "
                    + resp.getStatusCode() + " - " + resp.getBody().asString());
        }
    }

    // ─── Escenario 1: CATEGORY_OVERSPENDING ──────────────────────────────────────

    /**
     * Diciembre 2025 — mes vacío garantizado.
     * Ingreso: $1.000.000 | Gasto en una categoría: $400.000 = 40% > umbral 30%.
     * Al ser el único ingreso y gasto del mes, el porcentaje es exacto.
     */
    public static void registrarSobregastoEnCategoria(String token) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        YearMonth mes = mesOverspending();

        Long catIngreso = crearCategoria(token, "REC_INC_OVR_" + sufijo, "INCOME");
        Long catGasto   = crearCategoria(token, "REC_EXP_OVR_" + sufijo, "EXPENSE");

        registrarIngreso(token, catIngreso, 1_000_000.00, mes);
        registrarGasto(token,   catGasto,     400_000.00, mes);
    }

    // ─── Escenario 2: NO_SAVINGS ──────────────────────────────────────────────────

    /**
     * Noviembre 2025 — mes vacío garantizado.
     * Ingreso: $1.000.000 | Gasto: $1.000.000 → balance = 0 → NO_SAVINGS.
     */
    public static void registrarSinAhorro(String token) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        YearMonth mes = mesNoSavings();

        Long catIngreso = crearCategoria(token, "REC_INC_NSV_" + sufijo, "INCOME");
        Long catGasto   = crearCategoria(token, "REC_EXP_NSV_" + sufijo, "EXPENSE");

        registrarIngreso(token, catIngreso, 1_000_000.00, mes);
        registrarGasto(token,   catGasto,   1_000_000.00, mes);
    }

    // ─── Escenario 3: HEALTHY_SAVINGS ────────────────────────────────────────────

    /**
     * Mes actual + mes anterior con ahorro del 50% en ambos.
     * El use case verifica i=0 (mes actual) e i=1 (mes anterior).
     */
    public static void registrarAhorroSaludableConsecutivo(String token) {
        String sufijo = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Long catIngresoActual   = crearCategoria(token, "REC_INC_HLT_A_" + sufijo, "INCOME");
        Long catGastoActual     = crearCategoria(token, "REC_EXP_HLT_A_" + sufijo, "EXPENSE");
        Long catIngresoAnterior = crearCategoria(token, "REC_INC_HLT_B_" + sufijo, "INCOME");
        Long catGastoAnterior   = crearCategoria(token, "REC_EXP_HLT_B_" + sufijo, "EXPENSE");

        // Mes actual: usar LocalDate.now() para evitar "fecha futura"
        registrarIngresoFecha(token, catIngresoActual,   2_000_000.00, LocalDate.now());
        registrarGastoFecha(token,   catGastoActual,     1_000_000.00, LocalDate.now());

        // Mes anterior: día 15 sin problema
        registrarIngreso(token, catIngresoAnterior, 2_000_000.00, mesAnterior());
        registrarGasto(token,   catGastoAnterior,   1_000_000.00, mesAnterior());
    }

    private static void registrarIngresoFecha(String token, Long categoryId,
                                              double monto, LocalDate fecha) {
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Ingreso REC E2E\","
                        + "\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId);
        Response resp = ApiInteraction.postWithAuth(INCOMES_ENDPOINT, body, token);
        System.out.println("POST INGRESO [" + fecha + "] -> Status: "
                + resp.getStatusCode() + " -> Body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 201) {
            throw new RuntimeException("Fallo al registrar ingreso: "
                    + resp.getStatusCode() + " - " + resp.getBody().asString());
        }
    }

    private static void registrarGastoFecha(String token, Long categoryId,
                                            double monto, LocalDate fecha) {
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Gasto REC E2E\","
                        + "\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId);
        Response resp = ApiInteraction.postWithAuth(EXPENSES_ENDPOINT, body, token);
        System.out.println("POST GASTO [" + fecha + "] -> Status: "
                + resp.getStatusCode() + " -> Body: " + resp.getBody().asString());
        if (resp.getStatusCode() != 201) {
            throw new RuntimeException("Fallo al registrar gasto: "
                    + resp.getStatusCode() + " - " + resp.getBody().asString());
        }
    }
}