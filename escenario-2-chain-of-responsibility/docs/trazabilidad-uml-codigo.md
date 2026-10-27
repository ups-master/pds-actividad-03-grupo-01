# Trazabilidad UML–código del escenario 2 (Chain of Responsibility)

El escenario tiene versiones del diagrama en la carpeta `uml/`. El código vive en `codigo/`:

| Versión | Fuente | Imagen | Uso |
|---|---|---|---|
| Original | `uml/chainE2.puml` | POR COMPLETAR | Histórico, previo a la mejora; ninguna prueba lo lee |
| Mejorada | `uml/chainE2_mejorado.puml` | POR COMPLETAR | Versión final y referencia de `CorrespondenciaUmlCodigoTest` |
| Secuencia | `uml/chainE2_secuencia.puml` | POR COMPLETAR | Colaboración en ejecución |

> **POR COMPLETAR:** resumir en qué difiere la versión mejorada de la original.

| Elemento UML | Implementación | Verificación |
|---|---|---|
| Handler (abstracta, atributo `siguiente`) | POR COMPLETAR | Reflexión en `CorrespondenciaUmlCodigoTest` |
| `setSiguiente(siguiente)` | POR COMPLETAR | Reflexión y prueba de enlace |
| ConcreteHandler | POR COMPLETAR | Reflexión y pruebas por eslabón |
| Solicitud | POR COMPLETAR | Reflexión |
| `Manejador "1" o--> "0..1" Manejador : siguiente` | POR COMPLETAR | Inspección de campos |
| `Cliente --> "1" Manejador` | POR COMPLETAR | Inspección de campos |
| Client | POR COMPLETAR | `MainTest` |

## Decisiones de implementación

> **POR COMPLETAR:** qué ocurre si nadie atiende la solicitud (rechazo o excepción), si el eslabón que
> atiende se detiene o continúa, y cómo se evitan ciclos al enlazar.

## Extensiones deliberadas

> **POR COMPLETAR:** elementos del código que el UML omite por claridad (getters, validaciones, etc.).

## Ejecución de la verificación

```text
# Desde escenario-2-chain-of-responsibility/codigo
mvn test
```
