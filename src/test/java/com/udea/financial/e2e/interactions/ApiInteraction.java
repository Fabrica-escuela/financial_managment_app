package com.udea.financial.e2e.interactions;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.http.ContentType;

public class ApiInteraction {

    private static final String BASE_URL_AUTH    = "http://localhost:8080";
    private static final String BASE_URL_INCOME  = "http://localhost:8081";
    private static final String BASE_URL_REPORT  = "http://localhost:8082";

    // ─── Sin autenticación (microservicio de usuarios) ─────────────────────────

    public static Response post(String endpoint, String body) {
        return RestAssured
                .given()
                .baseUri(BASE_URL_AUTH)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response get(String endpoint, String token) {
        return RestAssured
                .given()
                .baseUri(BASE_URL_AUTH)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint);
    }

    // ─── Con autenticación (microservicio de ingresos - puerto 8081) ────────────

    public static Response postWithAuth(String endpoint, String body, String token) {
        return RestAssured
                .given()
                .baseUri(BASE_URL_INCOME)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .body(body)
                .when()
                .post(endpoint);
    }

    public static Response getWithAuth(String endpoint, String token) {
        return RestAssured
                .given()
                .baseUri(BASE_URL_INCOME)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint);
    }

    public static Response getWithReport(String endpoint, String token) {
        return RestAssured
                .given()
                .baseUri(BASE_URL_REPORT)
                .contentType(ContentType.JSON)
                .header("Authorization", "Bearer " + token)
                .when()
                .get(endpoint);
    }
}