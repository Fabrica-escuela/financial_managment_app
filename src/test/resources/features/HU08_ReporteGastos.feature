@HU-08 @Reportes
Feature: HU-08 Reporte de Distribución de Gastos por Categoría
  Como usuario autenticado de la aplicación
  Quiero visualizar un reporte gráfico con la distribución de mis gastos por categoría
  Para entender mis hábitos de consumo e identificar las áreas donde gasto más dinero

  @EscenarioExitoso
  Scenario: Generación exitosa del reporte mensual con gastos registrados
    Given que el usuario tiene una sesión activa en el sistema de reportes
    And tiene gastos registrados en distintas categorías en el mes de referencia de reportes
    When solicita el reporte mensual para ese período
    Then el sistema retorna el reporte con código 200
    And el reporte contiene una distribución de gastos no vacía
    And el reporte incluye el top de categorías con mayor consumo

  @EscenarioExitoso
  Scenario: Reporte para un mes sin transacciones
    Given que el usuario tiene una sesión activa en el sistema de reportes
    When solicita el reporte mensual para un período sin transacciones
    Then el sistema retorna el reporte con código 200
    And la respuesta contiene el mensaje "No hay datos disponibles para el período seleccionado"
    And la respuesta contiene la sugerencia "Registra transacciones en el módulo de gastos para generar reportes"

  @EscenarioExitoso
  Scenario: Comparación exitosa de hábitos de gasto entre dos meses
    Given que el usuario tiene una sesión activa en el sistema de reportes
    And tiene gastos registrados en los dos meses a comparar
    When solicita la comparación entre ambos meses
    Then el sistema retorna la comparación con código 200
    And la comparación contiene al menos una categoría con gasto aumentado

  @EscenarioFallido
  Scenario: Reporte con período futuro
    Given que el usuario tiene una sesión activa en el sistema de reportes
    When solicita el reporte mensual para un período futuro
    Then el sistema rechaza la solicitud con código 400
    And la respuesta contiene el mensaje de error "No se puede consultar un período futuro"

  @EscenarioFallido
  Scenario: Comparación con el mismo período dos veces
    Given que el usuario tiene una sesión activa en el sistema de reportes
    When solicita la comparación usando el mismo período en ambos parámetros
    Then el sistema rechaza la solicitud con código 400
    And la respuesta contiene el mensaje de error "Los dos períodos a comparar no pueden ser iguales"

  @EscenarioFallido
  Scenario: Reporte con mes inválido
    Given que el usuario tiene una sesión activa en el sistema de reportes
    When solicita el reporte mensual con un mes fuera del rango permitido
    Then el sistema rechaza la solicitud con código 400
    And la respuesta contiene el mensaje de error "El mes debe estar entre 1 y 12"