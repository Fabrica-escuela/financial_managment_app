@HU-03 @GestionGastos
Feature: HU-03 Gestión y Reporte de Gastos
  Como usuario autenticado en el sistema
  Quiero registrar mis transacciones de egresos
  Para visualizar el resumen de mis movimientos financieros

  Background: Usuario autenticado
    Given que el usuario "userteste2e@email.com" ha iniciado sesión correctamente

  @EscenarioExitoso @RegistroGasto
  Scenario: Registro exitoso de un nuevo gasto
    When intenta registrar un egreso por valor de 50000.0 con la descripción "Renta" en la categoría 2
    Then el sistema confirma el registro exitoso del movimiento

  @EscenarioFallido @ValidacionMonto
  Scenario: Intento de registro de gasto con datos inválidos
    When intenta registrar un egreso con un valor negativo de -10.0
    Then el sistema rechaza la transacción mostrando un mensaje de validación