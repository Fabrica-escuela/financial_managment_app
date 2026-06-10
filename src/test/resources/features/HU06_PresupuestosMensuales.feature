@HU-06 @Presupuestos
Feature: HU-06 Presupuestos Mensuales
  Como usuario
  Quiero establecer límites de gasto por categoría
  Para evitar exceder mis planes financieros

  @EscenarioExitoso
  Scenario: Creación exitosa de presupuesto
    Given que el usuario tiene una sesión activa en el sistema de presupuestos
    When crea un presupuesto con monto máximo 500.00 para una categoría de gasto válida
    Then el presupuesto se crea exitosamente con código 201

  @EscenarioExitoso
  Scenario: Alerta de presupuesto al alcanzar 80%
    Given que el usuario tiene una sesión activa en el sistema de presupuestos
    And existe un presupuesto de 100.00 con gastos que representan el 80% del límite
    When consulta el estado de sus presupuestos
    Then el sistema retorna el estado del presupuesto con código 200
    And el estado del presupuesto es "WARNING"

  @EscenarioExitoso
  Scenario: Indicador de presupuesto agotado cuando los gastos igualan o superan el límite
    Given que el usuario tiene una sesión activa en el sistema de presupuestos
    And existe un presupuesto de 100.00 con gastos que superan o igualan el límite
    When consulta el estado de sus presupuestos
    Then el sistema retorna el estado del presupuesto con código 200
    And el estado del presupuesto es "EXCEEDED"

  @EscenarioFallido
  Scenario: Creación de presupuesto duplicado para la misma categoría
    Given que el usuario tiene una sesión activa en el sistema de presupuestos
    And ya existe un presupuesto para la categoría de gasto
    When intenta crear otro presupuesto para la misma categoría
    Then el sistema rechaza el presupuesto con código 409
    And muestra el mensaje de presupuesto "Ya existe un presupuesto para esta categoría"