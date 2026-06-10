@HU-02 @Autenticacion
Feature: HU-02 Inicio de Sesión
  Como usuario registrado
  Quiero identificarme de forma segura
  Para acceder a mi información financiera personal

  @EscenarioExitoso
  Scenario: Autenticación exitosa
    Given que el usuario cuenta con una cuenta activa en el sistema
    When proporciona sus credenciales de acceso válidas
    Then se le permite el ingreso al sistema
    And obtiene la información de su resumen de saldos actualizado

  @EscenarioFallido
  Scenario: Intento de acceso fallido
    Given que el usuario existe en el sistema
    When se suministra una clave de acceso incorrecta
    Then el sistema restringe el ingreso notificando datos erróneos

  @EscenarioFallido
  Scenario: Usuario no registrado intenta iniciar sesión
    Given que el usuario no existe en el sistema
    When intenta autenticarse con credenciales de un usuario inexistente
    Then el sistema restringe el ingreso notificando datos erróneos

  @EscenarioBloqueo
  Scenario: Protección de cuenta por seguridad tras 5 intentos fallidos
    Given que se han registrado 5 intentos fallidos de acceso consecutivos
    When se intenta realizar una nueva validación de identidad
    Then el sistema suspende temporalmente el acceso por seguridad durante 15 minutos