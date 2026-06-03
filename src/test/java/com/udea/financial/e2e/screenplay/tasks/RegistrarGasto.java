package com.udea.financial.e2e.screenplay.tasks;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.Tasks;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class RegistrarGasto implements Task {

    private final Double monto;
    private final String descripcion;
    private final Long categoriaId;
    private final String token;

    public RegistrarGasto(Double monto, String descripcion, Long categoriaId, String token) {
        this.monto = monto;
        this.descripcion = descripcion;
        this.categoriaId = categoriaId;
        this.token = token;
    }

    public static RegistrarGasto conDatos(Double monto, String descripcion, Long categoriaId, String token) {
        return Tasks.instrumented(RegistrarGasto.class, monto, descripcion, categoriaId, token);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        // Estructura idéntica a tu ExpenseRequestDTO
        Map<String, Object> gastoBody = new HashMap<>();
        gastoBody.put("amount", monto);
        gastoBody.put("description", descripcion);
        gastoBody.put("date", LocalDate.now().toString()); // Formato "YYYY-MM-DD"
        gastoBody.put("categoryId", categoriaId);

        // Si el token es nulo o vacío por un login fallido, evitamos enviar basura
        String bearerToken = (token != null) ? token : "token_invalido_de_control";

        actor.attemptsTo(
                Post.to("/api/expenses") // URL estándar para Expense_Base_Path
                        .with(request -> request
                                .log().all()
                                .header("Authorization", "Bearer " + bearerToken)
                                .contentType(ContentType.JSON)
                                .body(gastoBody)
                        )
        );
    }
}