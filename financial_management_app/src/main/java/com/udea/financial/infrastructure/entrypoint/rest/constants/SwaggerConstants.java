package com.udea.financial.infrastructure.entrypoint.rest.constants;

public final class SwaggerConstants {

    private SwaggerConstants() {}

    // API Info
    public static final String API_TITLE = "Financial Management API";
    public static final String API_DESCRIPTION = "API for managing users in the financial management application";
    public static final String API_VERSION = "1.0.0";

    // Tags
    public static final String TAG_USER = "Users";
    public static final String TAG_USER_DESCRIPTION = "Operations related to user management";

    // User Controller
    public static final String USER_CREATE_SUMMARY = "Create a new user";
    public static final String USER_CREATE_DESCRIPTION = "Registers a new user in the system with the provided information";
    public static final String USER_GET_BY_ID_SUMMARY = "Get user by ID";
    public static final String USER_GET_BY_ID_DESCRIPTION = "Retrieves a user by their unique identifier";
    public static final String USER_GET_BY_EMAIL_SUMMARY = "Get user by email";
    public static final String USER_GET_BY_EMAIL_DESCRIPTION = "Retrieves a user by their email address";
    public static final String USER_GET_ALL_SUMMARY = "Get all users";
    public static final String USER_GET_ALL_DESCRIPTION = "Retrieves a list of all registered users";
    public static final String USER_DELETE_SUMMARY = "Delete user by ID";
    public static final String USER_DELETE_DESCRIPTION = "Deletes a user from the system by their unique identifier";

    // Response codes
    public static final String CODE_200 = "200";
    public static final String CODE_201 = "201";
    public static final String CODE_400 = "400";
    public static final String CODE_404 = "404";
    public static final String CODE_409 = "409";
    public static final String CODE_500 = "500";

    // Response descriptions
    public static final String RESP_CREATED = "User created successfully";
    public static final String RESP_OK = "Successful operation";
    public static final String RESP_BAD_REQUEST = "Validation error in request body";
    public static final String RESP_NOT_FOUND = "User not found";
    public static final String RESP_CONFLICT = "Email already registered";
    public static final String RESP_INTERNAL_ERROR = "Internal server error";
}
