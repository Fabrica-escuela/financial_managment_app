package com.udea.financial.domain.exception;

public final class ExceptionMessages {

    private ExceptionMessages() {}

    public static final String USER_NOT_FOUND_BY_ID = "User not found with id: ";
    public static final String USER_NOT_FOUND_BY_EMAIL = "User not found with email: ";
    public static final String DUPLICATE_EMAIL = "Email already registered";
    public static final String INVALID_CREDENTIALS = "Correo o contraseña incorrectos";
    public static final String ACCOUNT_LOCKED = "Cuenta bloqueada temporalmente por seguridad";
}
