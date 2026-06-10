@HU-04 @Gastos
Feature: HU-04 Registro de Gastos
  Como usuario autenticado
  Quiero registrar mis salidas de dinero
  Para controlar mi flujo de caja mensual

  @EscenarioExitoso
  Scenario: Registro exitoso de gasto
    Given que el usuario tiene una sesión activa en el sistema de gastos
    When registra un gasto con monto 150.00, descripción "Mercado semanal" y una categoría válida
    Then el gasto se registra exitosamente con código 201

  @EscenarioFallido
  Scenario: Registro de gasto con fecha futura
    Given que el usuario tiene una sesión activa en el sistema de gastos
    When intenta registrar un gasto con una fecha posterior a la fecha actual
    Then el sistema rechaza el gasto con código 400
    And muestra el mensaje de gasto "La fecha no puede ser futura"

  @EscenarioFallido
  Scenario: Registro de gasto con monto inválido igual a cero
    Given que el usuario tiene una sesión activa en el sistema de gastos
    When intenta registrar un gasto con monto 0.00
    Then el sistema rechaza el gasto con código 400
    And muestra el mensaje de gasto "El monto debe ser mayor a cero"