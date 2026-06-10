@HU-03 @Ingresos
Feature: HU-03 Registro de Ingresos
  Como usuario autenticado
  Quiero registrar mis entradas de dinero
  Para mantener actualizada mi disponibilidad financiera

  @EscenarioExitoso
  Scenario: Registro exitoso de ingreso
    Given que el usuario tiene una sesión activa en el sistema de ingresos
    When registra un ingreso con monto 2500.00, descripción "Salario mensual" y una categoría válida
    Then el ingreso se registra exitosamente con código 201

  @EscenarioFallido
  Scenario: Registro de ingreso con monto inválido igual a cero
    Given que el usuario tiene una sesión activa en el sistema de ingresos
    When intenta registrar un ingreso con monto 0.00
    Then el sistema rechaza el ingreso con código 400
    And muestra el mensaje "El monto debe ser mayor a cero"

  @EscenarioFallido
  Scenario: Registro de ingreso sin categoría válida
    Given que el usuario tiene una sesión activa en el sistema de ingresos
    When intenta registrar un ingreso con una categoría inexistente
    Then el sistema rechaza el ingreso con código 400
    And muestra el mensaje "La categoría seleccionada no existe"