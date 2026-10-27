# Escenario 1 - Mediator

Proyecto Maven con Java 21 para el escenario 1 de la actividad de patrones de comportamiento.

> **POR COMPLETAR:** resumen de una o dos frases del escenario (dominio y qué coordina el mediador).

## Estructura del escenario

```text
escenario-1-mediator/
├── README.md                          Informe del escenario (este archivo)
├── uml/                               Diseño: diagramas de clases y de secuencia
│   ├── mediatorE1.puml                Fuente PlantUML original
│   ├── mediatorE1_mejorado.puml       Fuente PlantUML mejorada (versión final)
│   └── mediatorE1_secuencia.puml      Diagrama de secuencia
├── codigo/                            Implementación: proyecto Maven (Java 21)
│   ├── pom.xml
│   ├── README.md                      Manual de uso
│   └── src/
│       ├── main/java/ec/edu/ups/pds/mediator/   Clases del patrón y Main
│       └── test/java/ec/edu/ups/pds/mediator/   Pruebas
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

> **POR COMPLETAR:** describir, con el enunciado, qué objetos se comunican entre sí y por qué las
> dependencias cruzadas son un problema. Pistas de «Cómo reconocerlo»: en el enunciado, «se comunican
> entre sí», «coordinar», «sin que se conozcan»; en el código, objetos con referencias a muchos otros del
> mismo tipo; en un diagrama, un nodo central conectado a muchos objetos que no se conectan entre sí.

## Justificación

> **POR COMPLETAR:** por qué Mediator y no otro. Contrastar al menos con Observer (difunde cambios de un
> sujeto; Mediator coordina el diálogo en ambos sentidos), Facade (comunicación en un solo sentido) y
> Chain of Responsibility (entrega a un solo receptor; el mediador decide a cuáles entrega). Indicar
> también el riesgo del patrón: que el mediador se vuelva una «clase dios».

### Participantes del patrón

| Rol del patrón | Clase | Responsabilidad |
|---|---|---|
| Mediator | POR COMPLETAR | Interfaz para comunicarse con los colegas. |
| ConcreteMediator | POR COMPLETAR | Coordina a los colegas, los conoce a todos y decide a quién llega cada evento. |
| Colleague | POR COMPLETAR | Conoce solo al mediador y le envía sus eventos. |
| ConcreteColleague | POR COMPLETAR | Participantes concretos; reaccionan cada uno a su manera. |
| Client | POR COMPLETAR (`Main`) | Arma el mediador y registra a los colegas. |

### Navegabilidad y multiplicidades

| Relación | Tipo | Navegabilidad | Multiplicidad | Atributo que la implementa |
|---|---|---|---|---|
| Colleague → Mediator | Asociación | Colega → Mediator | `*` → `1` | POR COMPLETAR (`Colega.mediador`) |
| ConcreteMediator → Colleague | Agregación | Mediator → Colegas | `1` → `0..*` | POR COMPLETAR (`MediadorConcreto.colegas`) |
| ConcreteMediator → Mediator | Realización | ConcreteMediator → Mediator | — | — |
| ConcreteColleague → Colleague | Herencia | ConcreteColleague → Colleague | — | — |

## Diagramas

### Diagrama original

Fuente: [`uml/mediatorE1.puml`](uml/mediatorE1.puml). Versión previa a la mejora sugerida con IA.

> **POR COMPLETAR:** insertar la imagen `uml/diagrama-clases-original.png` una vez renderizado el `.puml`.

### Diagrama mejorado

Fuente: [`uml/mediatorE1_mejorado.puml`](uml/mediatorE1_mejorado.puml). Versión final, la que implementa el código.

> **POR COMPLETAR:** insertar la imagen `uml/diagrama-clases-mejorado.png` y explicar qué cambió respecto al original.

### Diagrama de secuencia

Fuente: [`uml/mediatorE1_secuencia.puml`](uml/mediatorE1_secuencia.puml).

> **POR COMPLETAR:** insertar la imagen y explicar la colaboración (qué evento envía un colega, a quién lo
> entrega el mediador y cómo reacciona el resto, incluida la reentrada si aplica).

## Código fuente

- Código: [`codigo/src/main/java/ec/edu/ups/pds/mediator`](codigo/src/main/java/ec/edu/ups/pds/mediator)
- Pruebas: [`codigo/src/test/java/ec/edu/ups/pds/mediator`](codigo/src/test/java/ec/edu/ups/pds/mediator)
- Manual de uso: [`codigo/README.md`](codigo/README.md)
- Trazabilidad entre el UML y el código: [`docs/trazabilidad-uml-codigo.md`](docs/trazabilidad-uml-codigo.md)

### Verificación UML contra código

> **POR COMPLETAR:** cuando exista `CorrespondenciaUmlCodigoTest`, indicar que comprueba el código contra
> `uml/mediatorE1_mejorado.puml` y el resultado de la última ejecución (n.º de pruebas, 0 fallos).

## Uso crítico de inteligencia artificial

### Prompt principal

> **POR COMPLETAR:** prompt usado con la IA para revisar el diseño.

### Recomendación aceptada

> **POR COMPLETAR:** qué recomendó la IA, y por qué la aceptamos.

### Recomendación modificada o rechazada

> **POR COMPLETAR:** qué recomendó la IA, y por qué la modificamos o rechazamos.
