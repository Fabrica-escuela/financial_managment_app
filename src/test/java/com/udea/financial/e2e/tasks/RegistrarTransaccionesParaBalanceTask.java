package com.udea.financial.e2e.tasks;

import com.udea.financial.e2e.interactions.ApiInteraction;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom; // Para aleatoriedad

public class RegistrarTransaccionesParaBalanceTask {

    private static final String INCOMES_ENDPOINT    = "/api/incomes";
    private static final String EXPENSES_ENDPOINT   = "/api/expenses";
    private static final String CATEGORIES_ENDPOINT = "/api/categories";

    /**
     * Retorna un mes aleatorio en un rango de hace 2 a 10 años en el pasado.
     * Esto garantiza con total certeza que ninguna ejecución previa usó este período.
     */
    public static YearMonth generarMesAleatorioLimpio() {
        int mesesAtras = ThreadLocalRandom.current().nextInt(24, 120); // Entre 2 y 10 años atrás
        return YearMonth.now().minusMonths(mesesAtras);
    }

    public static Long crearCategoriaIngreso(String token, String sufijo) {
        String nombre = "BAL_INC_E2E_" + sufijo;
        String body = String.format("{\"name\":\"%s\",\"type\":\"INCOME\"}", nombre);
        Response response = ApiInteraction.postWithAuth(CATEGORIES_ENDPOINT, body, token);
        if (response.getStatusCode() == 201) {
            return response.jsonPath().getLong("id");
        }
        Response lista = ApiInteraction.getWithAuth(CATEGORIES_ENDPOINT + "?type=INCOME", token);
        return lista.jsonPath()
                .getList("findAll { it.name == '" + nombre + "' }.id", Long.class)
                .getFirst();
    }

    public static Long crearCategoriaGasto(String token, String sufijo) {
        String nombre = "BAL_EXP_E2E_" + sufijo;
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
     * Registra un ingreso en el mes controlado del escenario.
     */
    public static void registrarIngreso(String token, Long categoryId, double monto, YearMonth mesEscenario) {
        LocalDate fecha = mesEscenario.atDay(15);
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Ingreso balance E2E\",\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId
        );
        ApiInteraction.postWithAuth(INCOMES_ENDPOINT, body, token);
    }

    /**
     * Registra un gasto en el mes controlado del escenario.
     */
    public static void registrarGasto(String token, Long categoryId, double monto, YearMonth mesEscenario) {
        LocalDate fecha = mesEscenario.atDay(15);
        String body = String.format(Locale.US,
                "{\"amount\":%.2f,\"description\":\"Gasto balance E2E\",\"date\":\"%s\",\"categoryId\":%d}",
                monto, fecha, categoryId
        );
        ApiInteraction.postWithAuth(EXPENSES_ENDPOINT, body, token);
    }
}