@HU-03 @GestionIngresos
Feature: HU-03 Gestión y Registro de Ingresos
  Como usuario autenticado en el sistema
  Quiero registrar mis transacciones de ingresos
  Para mantener actualizada mi disponibilidad financiera

  Background: Usuario autenticado
    Given que el usuario "userteste2e@email.com" ha iniciado sesión correctamente

  @EscenarioExitoso @RegistroIngreso
  Scenario: Registro exitoso de un nuevo ingreso
    When intenta registrar un ingreso por valor de 150000.0 con la descripción "Salario" en la categoría 1
    Then el sistema confirma el registro exitoso del ingreso

  @EscenarioFallido @ValidacionMontoIngreso
  Scenario: Intento de registro de ingreso con monto inválido igual o menor a cero
    When intenta registrar un ingreso con un valor de 0.0
    Then el sistema rechaza la transacción de ingreso mostrando un mensaje de validación

  @EscenarioFallido @RegistroIngresoSinCategoria
  Scenario: Intento de registro de ingreso sin asociar categoría
    When intenta registrar un ingreso por valor de 50000.0 con la descripción "Regalo" sin especificar categoría
    Then el sistema impide el registro del ingreso solicitando definir la fuente