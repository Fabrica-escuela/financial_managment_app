@HU-09 @Recomendaciones
Feature: HU-09 Motor de Recomendaciones Financieras
  Como usuario autenticado de la aplicación
  Quiero recibir recomendaciones automáticas basadas en mis hábitos de gasto
  Para mejorar mis decisiones financieras con orientación personalizada

  @EscenarioExitoso
  Scenario: Recomendación al superar el gasto en una categoría
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    And tiene un ingreso registrado y un gasto en una categoría que supera el 30% de ese ingreso en el mes actual
    When solicita las recomendaciones del mes de sobregasto
    Then el sistema retorna las recomendaciones con código 200
    And la lista contiene una recomendación de tipo "CATEGORY_OVERSPENDING"
    And la recomendación incluye el porcentaje actual y el porcentaje recomendado

  @EscenarioExitoso
  Scenario: Recomendación al detectar ausencia de ahorro mensual
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    And tiene registrado un gasto mayor o igual a su ingreso en el mes actual
    When solicita las recomendaciones del mes sin ahorro
    Then el sistema retorna las recomendaciones con código 200
    And la lista contiene una recomendación de tipo "NO_SAVINGS"
    And la recomendación sugiere al menos una categoría específica

  @EscenarioExitoso
  Scenario: Recomendación positiva por hábitos saludables sostenidos
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    And ha ahorrado al menos el 20% de sus ingresos en el mes actual y en el mes anterior
    When solicita las recomendaciones del mes actual
    Then el sistema retorna las recomendaciones con código 200
    And la lista contiene una recomendación de tipo "HEALTHY_SAVINGS"
    And la recomendación incluye exactamente 3 sugerencias de inversión ordenadas por riesgo

  @EscenarioExitoso
  Scenario: Sin recomendaciones para un período sin datos
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    When solicita las recomendaciones para un período sin transacciones
    Then el sistema retorna las recomendaciones con código 200
    And la lista de recomendaciones está vacía

  @EscenarioFallido
  Scenario: Recomendaciones para un período futuro
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    When solicita las recomendaciones para un período futuro
    Then el sistema rechaza la solicitud de recomendaciones con código 400
    And la respuesta contiene el error "No se puede consultar un período futuro"

  @EscenarioFallido
  Scenario: Recomendaciones con mes inválido
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    When solicita las recomendaciones con un mes fuera del rango permitido
    Then el sistema rechaza la solicitud de recomendaciones con código 400
    And la respuesta contiene el error "El mes debe estar entre 1 y 12"

  @EscenarioFallido
  Scenario: Recomendaciones con parámetro obligatorio faltante
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    When solicita las recomendaciones sin enviar el parámetro de mes
    Then el sistema rechaza la solicitud de recomendaciones con código 400
    And la respuesta contiene el error "El parámetro 'month' es obligatorio"

  @EscenarioFallido
  Scenario: Recomendaciones con parámetro no numérico
    Given que el usuario tiene una sesión activa en el sistema de recomendaciones
    When solicita las recomendaciones con un valor de mes no numérico
    Then el sistema rechaza la solicitud de recomendaciones con código 400
    And la respuesta contiene el error "El parámetro 'month' debe ser un número entero válido"