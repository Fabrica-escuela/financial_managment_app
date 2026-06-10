@HU-07 @Balance
Feature: HU-07 Balance Financiero Mensual
  Como usuario autenticado de la aplicación
  Quiero visualizar mi balance financiero del mes actual
  Para conocer de forma inmediata mi situación económica real

  @EscenarioExitoso
  Scenario: Consulta de balance positivo
    Given que el usuario tiene una sesión activa en el sistema de balance
    And ha registrado ingresos por 3000000.00 y gastos por 1800000.00 en el mes actual
    When consulta el balance financiero del mes actual
    Then el sistema retorna el balance con código 200
    And el status del balance es "POSITIVE"

  @EscenarioExitoso
  Scenario: Consulta de balance negativo
    Given que el usuario tiene una sesión activa en el sistema de balance
    And ha registrado ingresos por 2000000.00 y gastos por 2500000.00 en el mes actual
    When consulta el balance financiero del mes actual
    Then el sistema retorna el balance con código 200
    And el status del balance es "NEGATIVE"
    And la respuesta contiene el mensaje de alerta "Tus gastos superan tus ingresos este mes"

  @EscenarioExitoso
  Scenario: Consulta de balance neutro
    Given que el usuario tiene una sesión activa en el sistema de balance
    And ha registrado ingresos por 1500000.00 y gastos por 1500000.00 en el mes actual
    When consulta el balance financiero del mes actual
    Then el sistema retorna el balance con código 200
    And el status del balance es "ZERO"