# Escenario 2 - Chain of Responsibility

Proyecto Maven con Java 21 para el escenario 2 de la actividad de patrones de comportamiento.

> **POR COMPLETAR:** resumen de una o dos frases del escenario (dominio y qué recorre la cadena).

## Estructura del escenario

```text
escenario-2-chain-of-responsibility/
├── README.md                          Informe del escenario (este archivo)
├── uml/                               Diseño: diagramas de clases y de secuencia
│   ├── chainE2.puml                   Fuente PlantUML original
│   ├── chainE2_mejorado.puml          Fuente PlantUML mejorada (versión final)
│   └── chainE2_secuencia.puml         Diagrama de secuencia
├── codigo/                            Implementación: proyecto Maven (Java 21)
│   ├── pom.xml
│   ├── README.md                      Manual de uso
│   └── src/
│       ├── main/java/ec/edu/ups/pds/chain/   Clases del patrón y Main
│       └── test/java/ec/edu/ups/pds/chain/   Pruebas
└── docs/
    └── trazabilidad-uml-codigo.md     Correspondencia entre el UML y el código
```

## Contenido

1. [El problema de comportamiento](#el-problema-de-comportamiento)
2. [Justificación](#justificación)
3. [Diagramas](#diagramas)
4. [Código fuente](#código-fuente)
5. [Uso crítico de inteligencia artificial](#uso-crítico-de-inteligencia-artificial)

## El problema de comportamiento

> **POR COMPLETAR:** describir, con el enunciado, qué solicitud puede atender más de un objeto y por qué
> el emisor no debe decidir quién la atiende. Pistas de «Cómo reconocerlo»: en el enunciado, «el primero
> que pueda», «escala al siguiente nivel», «filtros», «según el monto/prioridad/nivel»; en el código, una
> escalera de `if/else if`; en un diagrama, una clase abstracta con una referencia a su propio tipo.

## Justificación

> **POR COMPLETAR:** por qué Chain of Responsibility y no otro. Contrastar al menos con Observer (entrega a
> todos los suscritos; Chain entrega a uno), Decorator (siempre delega; un eslabón puede decidir no
> hacerlo) y Mediator. Documentar las decisiones del diseño: qué pasa si nadie atiende la solicitud y si
> el eslabón que atiende parcialmente se detiene o continúa.

### Participantes del patrón

| Rol del patrón | Clase | Responsabilidad |
|---|---|---|
| Handler | POR COMPLETAR | Define la interfaz para atender solicitudes y guarda el enlace al siguiente. |
| ConcreteHandler | POR COMPLETAR | Atiende lo que le corresponde; si no, delega en el siguiente. |
| Client | POR COMPLETAR | Arma la cadena y envía la solicitud al primer eslabón. |

La solicitud (POR COMPLETAR) es el dato que recorre la cadena; no es un rol GoF.

### Navegabilidad y multiplicidades

| Relación | Tipo | Navegabilidad | Multiplicidad | Atributo que la implementa |
|---|---|---|---|---|
| Client → Handler | Asociación | Cliente → Handler | `1` → `1` | POR COMPLETAR (`Cliente.cadena`) |
| Handler → Handler (`siguiente`) | Agregación | Handler → Handler | `1` → `0..1` | POR COMPLETAR (`Manejador.siguiente`) |
| Handler ⇢ Solicitud (`usa`) | Dependencia | Handler → dato | — | Ninguno: parámetro del método que atiende |
| ConcreteHandler → Handler | Herencia | ConcreteHandler → Handler | — | — |

## Diagramas

### Diagrama original

Fuente: [`uml/chainE2.puml`](uml/chainE2.puml). Versión previa a la mejora sugerida con IA.

> **POR COMPLETAR:** insertar la imagen `uml/diagrama-clases-original.png` una vez renderizado el `.puml`.

### Diagrama mejorado

Fuente: [`uml/chainE2_mejorado.puml`](uml/chainE2_mejorado.puml). Versión final, la que implementa el código.

> **POR COMPLETAR:** insertar la imagen `uml/diagrama-clases-mejorado.png` y explicar qué cambió respecto al original.

### Diagrama de secuencia

Fuente: [`uml/chainE2_secuencia.puml`](uml/chainE2_secuencia.puml).

> **POR COMPLETAR:** insertar la imagen y explicar un recorrido (qué eslabones rechazan, cuál atiende y
> cómo vuelve la respuesta).

## Código fuente

- Código: [`codigo/src/main/java/ec/edu/ups/pds/chain`](codigo/src/main/java/ec/edu/ups/pds/chain)
- Pruebas: [`codigo/src/test/java/ec/edu/ups/pds/chain`](codigo/src/test/java/ec/edu/ups/pds/chain)
- Manual de uso: [`codigo/README.md`](codigo/README.md)
- Trazabilidad entre el UML y el código: [`docs/trazabilidad-uml-codigo.md`](docs/trazabilidad-uml-codigo.md)

### Verificación UML contra código

> **POR COMPLETAR:** cuando exista `CorrespondenciaUmlCodigoTest`, indicar que comprueba el código contra
> `uml/chainE2_mejorado.puml` y el resultado de la última ejecución (n.º de pruebas, 0 fallos).

## Uso crítico de inteligencia artificial

### Prompt principal

> **POR COMPLETAR:** prompt usado con la IA para revisar el diseño.

### Recomendación aceptada

> **POR COMPLETAR:** qué recomendó la IA, y por qué la aceptamos.

### Recomendación modificada o rechazada

> **POR COMPLETAR:** qué recomendó la IA, y por qué la modificamos o rechazamos.
