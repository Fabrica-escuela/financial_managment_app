@HU-05 @ClasificacionCategorias
Feature: HU-05 Clasificación por Categorías
  Como usuario autenticado en el sistema
  Quiero categorizar mis movimientos financieros
  Para analizar la distribución de mi dinero

  Background: Usuario autenticado
    Given que el usuario "userteste2e@email.com" ha iniciado sesión correctamente

  @EscenarioExitoso @AsignacionCategoria
  Scenario: Vinculación exitosa de un gasto a una categoría existente
    Given que el usuario ha creado la categoría "Transporte" de tipo "EXPENSE"
    When vincula un gasto de 30000.0 con la descripción "Bus mensual" a esa categoría
    Then el sistema consolida el gasto dentro del rubro seleccionado

  @EscenarioExitoso @PersonalizacionCategoria
  Scenario: Creación exitosa de una nueva categoría personalizada
    When define una nueva categoría con nombre "Gimnasio" y tipo "EXPENSE"
    Then la categoría queda habilitada para uso inmediato

  @EscenarioFallido @UnicidadCategoria
  Scenario: Intento de crear una categoría duplicada
    Given que ya existe la categoría "Alimentacion" de tipo "EXPENSE" para el usuario
    When intenta crear nuevamente la categoría "Alimentacion" de tipo "EXPENSE"
    Then el sistema advierte sobre la existencia previa y evita el duplicado