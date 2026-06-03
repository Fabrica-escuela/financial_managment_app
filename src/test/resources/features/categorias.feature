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

  @EscenarioFallido @NombreCategoriaVacio
  Scenario: Intento de crear categoría con nombre vacío
    When intenta crear una categoría con nombre vacío y tipo "EXPENSE"
    Then el sistema rechaza la categoría por nombre no válido

  @EscenarioFallido @TipoCategoriaInvalido
  Scenario: Intento de crear categoría con tipo no permitido
    When intenta crear una categoría con nombre "Viajes" y tipo inválido "INVALIDO"
    Then el sistema rechaza la categoría indicando que el tipo no es reconocido

  @EscenarioFallido @CategoriaSinAutenticacion
  Scenario: Intento de crear categoría sin token de autenticación
    When intenta crear una categoría con nombre "SinToken" y tipo "INCOME" sin autenticación
    Then el sistema deniega la operación de categoría por falta de autenticación