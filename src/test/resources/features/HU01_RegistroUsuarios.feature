@HU-01 @Registro
Feature: HU-01 Registro de Usuarios
  Como nuevo usuario de la aplicación
  Quiero registrarme con mis datos básicos en la plataforma
  Para acceder a mis finanzas personales de forma segura y personalizada

  @EscenarioExitoso
  Scenario: Registro exitoso de usuario
    Given que soy un usuario sin cuenta en la aplicación
    When me registro con un nombre y correo únicos generados automáticamente y contraseña "securePass123"
    Then el sistema crea mi cuenta exitosamente con código 201

  @EscenarioFallido
  Scenario: Registro con correo ya existente
    Given que ya existe una cuenta registrada con el correo "teste2e_nuevo1@gmail.com"
    When intento registrarme con ese mismo correo y contraseña "securePass123"
    Then el sistema rechaza el registro con código 409
    And muestra el mensaje de error "Ya existe un registro con este correo electrónico"

  @EscenarioFallido
  Scenario: Registro con contraseña débil
    Given que soy un usuario sin cuenta en la aplicación
    When me registro con nombre "Carlos Test", correo "carlos.test.e2e@example.com" y contraseña "abc12"
    Then el sistema rechaza el registro con código 400
    And muestra el mensaje de validación "Password must be at least 8 characters"

  @EscenarioFallido
  Scenario: Registro con campos obligatorios vacíos
    Given que soy un usuario sin cuenta en la aplicación
    When intento registrarme con todos los campos vacíos
    Then el sistema rechaza el registro con código 400
    And muestra el mensaje de validación "Name is required"