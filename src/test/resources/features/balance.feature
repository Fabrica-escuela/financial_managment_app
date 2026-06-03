@HU-07 @BalanceFinanciero
Feature: HU-07 Balance Financiero
  Como usuario autenticado en el sistema
  Quiero conocer mi situación económica neta
  Para tomar decisiones financieras informadas

  Background: Usuario autenticado
    Given que el usuario "userteste2e@email.com" ha iniciado sesión correctamente

  @EscenarioExitoso @BalancePositivo
  Scenario: Visualización de saldo positivo cuando los ingresos superan los gastos
    When consulta su balance financiero del mes actual
    Then el sistema retorna el balance con la situación financiera actual

  @EscenarioExitoso @AlertaDeficit
  Scenario: Alerta de déficit cuando los gastos superan los ingresos
    When consulta su resumen financiero del mes actual
    Then si los gastos superan los ingresos el sistema presenta alerta de sobregiro

  @EscenarioExitoso @SincronizacionTiempoReal
  Scenario: El saldo se recalcula después de registrar un nuevo movimiento
    Given que el usuario consulta su balance antes de un nuevo movimiento
    When consulta nuevamente su balance financiero del mes actual
    Then el saldo neto refleja la situación financiera actualizada