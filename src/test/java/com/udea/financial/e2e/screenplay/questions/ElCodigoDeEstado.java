package com.udea.financial.e2e.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class ElCodigoDeEstado implements Question<Integer> {

    public static ElCodigoDeEstado delServidor() {
        return new ElCodigoDeEstado();
    }

    @Override
    public Integer answeredBy(Actor actor) {
        // Captura el status code de la última petición ejecutada por el actor
        return SerenityRest.lastResponse().getStatusCode();
    }
}