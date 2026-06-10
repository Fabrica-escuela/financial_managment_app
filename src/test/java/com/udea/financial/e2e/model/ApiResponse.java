package com.udea.financial.e2e.model;

import lombok.Data;
import java.util.List;

@Data
public class ApiResponse {
    private int statusCode;
    private String body;
    private int errorCode;
    private String message;
    private List<String> details;
}