# AGENTS.md

Guía única para agentes de código (Claude Code, Codex y similares) al trabajar en este repositorio.

## Qué es

Actividad 03 de Patrones de Diseño de Software (UPS, Maestría en Software): dos escenarios de patrones de
comportamiento. Es un trabajo académico evaluado con rúbrica, así que la **correspondencia UML ↔ código** y
la documentación (informes, uso crítico de IA) pesan tanto como el código. Idioma del repo: español.
Sigue la estructura de `../pds-actividad-01-grupo-01` y `../pds-actividad-02-grupo-01`.

| Escenario | Patrón | Java |
|---|---|---|
| 1 | Chain of Responsibility | `escenario-1-chain-of-responsibility/` |
| 2 | Mediator | `escenario-2-mediator/` |

**Estado: implementado y documentado.** Escenario 1: validación de pedidos de la tienda ficticia *Andes
Market*; escenario 2: coordinación de la cocina del restaurante ficticio *El Fogón Andino* (no usar aeropuerto
ni formularios de UI: son los ejemplos de refactoring.guru). El dominio está en `docs/enunciado-actividad-03.md`,
que también transcribe la plantilla oficial (secciones del informe y rúbrica). No inventar el dominio.
El informe en Word (`PDS_Actividad_03_GrupoXX.docx`) y los PDF de las actividades previas viven en `docs/`
pero no se versionan (`.gitignore`); el Word se llena a partir de los README de cada escenario.

Material de referencia (teoría, diagramas y Java de ejemplo):
`../../class-activities-pds/unidad-03-comportamiento/complementarios/{mediator,chain-of-responsibility}`.

## Comandos

Java 21 y Maven 3.9+. El POM padre de la raíz agrega los dos módulos `*/codigo`.

```text
mvn test                                   # raíz: ambos escenarios
mvn test -pl escenario-1-chain-of-responsibility/codigo
mvn test -Dtest=NombreDeLaClaseTest#metodo
mvn -q compile exec:java -pl escenario-2-mediator/codigo   # demo (Main); el escenario 1 usa Demo
```

Solo Java: no hay port a Go ni `go.work`. No hay linter configurado.

## Arquitectura y reglas que cruzan archivos

- **El diagrama mejorado manda; el código se deriva de él, nunca al revés.** `uml/*_mejorado.puml` es el diseño
  que resulta de aceptar las recomendaciones de la IA. Un cambio de diseño empieza en el `.puml`; después se
  ajusta el código para que lo implemente, y se regenera `uml/diagrama-clases-mejorado.png` (PlantUML con
  Graphviz) y se actualiza `docs/trazabilidad-uml-codigo.md`. Cada escenario tiene un
  `CorrespondenciaUmlCodigoTest` que lee el `.puml` y lo compara con las clases por reflexión: cada miembro del
  diagrama debe existir en el código (visibilidad y tipos) y ningún atributo ni operación propios del código
  puede faltar en el diagrama. Modelo de esa prueba en la actividad 01:
  `../pds-actividad-01-grupo-01/escenario-1-registro-vehicular/codigo/src/test/java/ec/edu/ups/pds/factorymethod/CorrespondenciaUmlCodigoTest.java`.
  Los diagramas mejorados agrupan las clases en paquetes (roles del patrón, datos, dependencias) y usan un
  estereotipo y un color por rol GoF; conservarlos al editarlos, porque los informes usan los mismos nombres.
  `uml/diagrama-clases-mejorado.html` y `uml/diagrama-clases-mejorado-archify.png` son vistas derivadas hechas con
  archify (el PNG se captura del HTML en modo `?embed=1`); no hay candidato que las regenere, así que si el
  diseño cambia hay que rehacerlas o quitarlas de los informes.
  Los conteos de pruebas aparecen en el README raíz, en los informes, en los manuales
  `codigo/README.md` y en la trazabilidad: actualizarlos si cambian.
- **Mediator.** Los colegas conocen solo al mediador (`ComponenteCocina.mediador`); el mediador conoce a todos
  por la abstracción (`componentes`). Ningún colega referencia a otro. `JefeDeCocina` no conoce clases concretas:
  reparte con `atiende(tipo)` y `prepara(plato)`. Itera una copia de la lista (reentrada). Una mesa se sirve una
  sola vez (`EstadoMesa.estaLista` exige no servida y algo listo); la cuenta lleva solo lo servido.
- **Chain of Responsibility.** El cliente (`ProcesadorPedidos`) conoce solo el primer eslabón; cada eslabón
  conoce solo a su `siguiente`. Un rechazo detiene la cadena, una aprobación delega y sin siguiente se aprueba;
  `setSiguiente` rechaza autoenlaces y ciclos. La cadena la arma quien configura (`Demo`, pruebas).
- Los informes (`escenario-*/README.md`) siguen las secciones de la plantilla oficial: Escenario, Problema,
  Justificación, Participantes, Diagrama, Código fuente, Validación de la solución y Uso crítico de IA.

## Convenciones

- Código, comentarios y mensajes en español; ASCII sin tildes en mensajes de excepción del código Java.
- Paquetes Java: `ec.edu.ups.pds.mediator` y `ec.edu.ups.pds.chain`.
- Si se renombran las carpetas de escenario con el dominio, actualizar los `<module>` del `pom.xml` raíz.
- `.github/modernize/` está ignorado por git; `target/` también.
