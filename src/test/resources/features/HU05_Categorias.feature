@HU-05 @Categorias
Feature: HU-05 Clasificación por Categorías
  Como usuario autenticado
  Quiero categorizar mis movimientos financieros
  Para analizar la distribución de mi dinero

  @EscenarioExitoso
  Scenario: Creación exitosa de categoría
    Given que el usuario tiene una sesión activa en el sistema de categorías
    When crea una categoría con nombre único generado automáticamente y tipo "EXPENSE"
    Then la categoría se crea exitosamente con código 201
    And el nombre de la categoría se guarda en mayúsculas

  @EscenarioFallido
  Scenario: Intento de crear categoría con nombre duplicado
    Given que el usuario tiene una sesión activa en el sistema de categorías
    And ya existe una categoría con nombre "ALIMENTACION_E2E" y tipo "EXPENSE"
    When intenta crear otra categoría con el mismo nombre "ALIMENTACION_E2E" y tipo "EXPENSE"
    Then el sistema rechaza la categoría con código 409
    And muestra el mensaje de categoría "Ya existe una categoría con ese nombre"

  @EscenarioFallido
  Scenario: Creación de categoría con tipo inválido
    Given que el usuario tiene una sesión activa en el sistema de categorías
    When crea una categoría con nombre "INVALIDA_E2E" y tipo "INVALIDO"
    Then el sistema rechaza la categoría con código 400
    And muestra el mensaje de categoría "El tipo debe ser INCOME o EXPENSE"