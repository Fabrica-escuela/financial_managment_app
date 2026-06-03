@HU-06 @PresupuestosMensuales
Feature: HU-06 Presupuestos Mensuales
  Como usuario autenticado en el sistema
  Quiero establecer límites de gasto por categoría
  Para evitar exceder mis planes financieros

  Background: Usuario autenticado
    Given que el usuario "userteste2e@email.com" ha iniciado sesión correctamente

  @EscenarioExitoso @EstablecimientoLimite
  Scenario: Establecimiento exitoso de un presupuesto mensual para una categoría
    Given que el usuario tiene disponible la categoría con id 1
    When asigna un tope máximo mensual de 500000.0 a esa categoría
    Then el sistema registra el presupuesto y comienza a monitorear desde cero

  @EscenarioExitoso @AdvertenciaProximidad
  Scenario: Alerta preventiva al alcanzar el 80% del presupuesto
    Given que el usuario tiene un presupuesto configurado para la categoría con id 1
    When se registra un nuevo gasto en esa categoría
    Then el sistema puede emitir una alerta preventiva de proximidad al límite

  @EscenarioExitoso @PresupuestoAgotado
  Scenario: Notificación de presupuesto agotado al consultar el estado
    When consulta el estado de todos sus presupuestos del mes
    Then el sistema muestra el estado actualizado de cada presupuesto

  @EscenarioFallido @PresupuestoCategoriaInexistente
  Scenario: Intento de crear presupuesto para una categoría que no existe
    Given que el usuario referencia una categoría inexistente con id 999999
    When intenta asignar un presupuesto de 200000.0 a esa categoría inexistente
    Then el sistema rechaza el presupuesto indicando que la categoría no fue encontrada

  @EscenarioFallido @PresupuestoMontoInvalido
  Scenario: Intento de crear presupuesto con monto igual a cero
    Given que el usuario tiene disponible la categoría con id 1
    When intenta asignar un presupuesto con monto inválido de 0.0 a esa categoría
    Then el sistema rechaza el presupuesto por monto no permitido

  @EscenarioFallido @PresupuestoSinAutenticacion
  Scenario: Intento de crear presupuesto sin token de autenticación
    Given que el usuario tiene disponible la categoría con id 1
    When intenta crear un presupuesto de 300000.0 sin enviar token de autenticación
    Then el sistema deniega el acceso por falta de autenticación