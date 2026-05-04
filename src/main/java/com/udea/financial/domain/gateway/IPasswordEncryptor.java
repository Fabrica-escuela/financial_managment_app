package com.udea.financial.domain.gateway;

public interface IPasswordEncryptor {
    String encrypt(String password);
    boolean matches(String rawPassword, String encodedPassword);
}
