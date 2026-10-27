# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Qué es

Actividad 03 de Patrones de Diseño de Software (UPS, Maestría en Software): dos escenarios de patrones de
comportamiento. Es un trabajo académico evaluado con rúbrica, así que la **correspondencia UML ↔ código** y
la documentación (informes, uso crítico de IA) pesan tanto como el código. Idioma del repo: español.
Sigue la estructura de `../pds-actividad-01-grupo-01`.

| Escenario | Patrón | Java |
|---|---|---|
| 1 | Mediator | `escenario-1-mediator/` |
| 2 | Chain of Responsibility | `escenario-2-chain-of-responsibility/` |

**Estado: esqueleto.** El enunciado aún no está en `docs/enunciado-actividad-03.md`, por lo que el
dominio de cada escenario, los nombres de clase y los textos de los informes están marcados
**`POR COMPLETAR`** (`grep -rn "POR COMPLETAR" .`). No inventar el dominio: completar a partir del enunciado.
Al completar un hueco, quitar su marcador.

Material de referencia (teoría, diagramas y Java de ejemplo):
`../../class-activities-pds/unidad-03-comportamiento/complementarios/{mediator,chain-of-responsibility}`.

## Comandos

Java 21 y Maven 3.9+. El POM padre de la raíz agrega los dos módulos `*/codigo`.

```text
mvn test                                   # raíz: ambos escenarios
mvn test -pl escenario-1-mediator/codigo
mvn test -Dtest=NombreDeLaClaseTest#metodo
```

Solo Java: no hay port a Go ni `go.work`. No hay linter configurado.

## Arquitectura y reglas que cruzan archivos

- **El UML es contrato, no decoración.** Cuando haya clases, cada escenario debe tener un
  `CorrespondenciaUmlCodigoTest` que lea `../uml/*_mejorado.puml` y lo compare con las clases por
  reflexión, como en `../pds-actividad-01-grupo-01/escenario-1-registro-vehicular/codigo/src/test/java/ec/edu/ups/pds/factorymethod/CorrespondenciaUmlCodigoTest.java`.
  Si cambia una clase hay que actualizar el `.puml` mejorado, su imagen y `docs/trazabilidad-uml-codigo.md`.
  Los conteos de pruebas aparecen en el README raíz y en los informes: actualizarlos si cambian.
- **Mediator.** Los colegas conocen solo al mediador (`Colega.mediador`); el mediador conoce a todos
  (`colegas`). Ningún colega referencia a otro. Evitar un mediador que conozca clases concretas de colegas
  y considerar la reentrada (un colega que responde mientras se reparte un evento: iterar una copia).
- **Chain of Responsibility.** El cliente conoce solo el primer eslabón; cada eslabón conoce solo a su
  `siguiente`. Definir y documentar qué ocurre si nadie atiende y si un eslabón se detiene o continúa.
- Los `.puml` actuales son plantillas con los roles del patrón y relaciones ya dibujadas; renombrar las
  clases con el dominio del enunciado.

## Convenciones

- Código, comentarios y mensajes en español; ASCII sin tildes en mensajes de excepción del código Java.
- Paquetes Java: `ec.edu.ups.pds.mediator` y `ec.edu.ups.pds.chain`.
- Si se renombran las carpetas de escenario con el dominio, actualizar los `<module>` del `pom.xml` raíz.
- `.github/modernize/` está ignorado por git; `target/` también.
