# Trazabilidad UML–código del escenario 1 (Mediator)

El escenario tiene versiones del diagrama en la carpeta `uml/`. El código vive en `codigo/`:

| Versión | Fuente | Imagen | Uso |
|---|---|---|---|
| Original | `uml/mediatorE1.puml` | POR COMPLETAR | Histórico, previo a la mejora; ninguna prueba lo lee |
| Mejorada | `uml/mediatorE1_mejorado.puml` | POR COMPLETAR | Versión final y referencia de `CorrespondenciaUmlCodigoTest` |
| Secuencia | `uml/mediatorE1_secuencia.puml` | POR COMPLETAR | Colaboración en ejecución |

> **POR COMPLETAR:** resumir en qué difiere la versión mejorada de la original.

| Elemento UML | Implementación | Verificación |
|---|---|---|
| Mediator (interfaz) | POR COMPLETAR | Reflexión en `CorrespondenciaUmlCodigoTest` |
| ConcreteMediator y su lista de colegas | POR COMPLETAR | Reflexión y pruebas de reparto |
| Colleague (abstracta, atributo `mediador`) | POR COMPLETAR | Reflexión |
| ConcreteColleague | POR COMPLETAR | Reflexión y pruebas de reacción |
| `Colega "*" --> "1" Mediador` | POR COMPLETAR | Inspección de campos |
| `MediadorConcreto "1" o--> "0..*" Colega` | POR COMPLETAR | Inspección de campos |
| Main (Client) | POR COMPLETAR | `MainTest` |

## Decisiones de implementación

> **POR COMPLETAR:** a quién llega cada evento, quién se registra y cuándo, y cómo se maneja la reentrada
> (p. ej. iterar una copia de la lista de colegas).

## Extensiones deliberadas

> **POR COMPLETAR:** elementos del código que el UML omite por claridad (getters, validaciones, etc.).

## Ejecución de la verificación

```text
# Desde escenario-1-mediator/codigo
mvn test
```
