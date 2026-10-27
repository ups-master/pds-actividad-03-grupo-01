# Manual de uso - Escenario 1 (Mediator)

Cómo compilar, probar y ejecutar el código del escenario 1. Todos los comandos se lanzan desde esta
carpeta (`escenario-1-mediator/codigo`).

## 1. Requisitos

| Herramienta | Versión | Para qué se usa |
|---|---|---|
| JDK | 21 o superior | Compilar y ejecutar |
| Maven | 3.9 o posterior | Ejecutar las pruebas con `mvn test` |

## 2. Estructura del código

```text
codigo/
├── pom.xml
└── src/
    ├── main/java/ec/edu/ups/pds/mediator/
    │   └── package-info.java          Roles esperados; las clases están POR COMPLETAR
    └── test/java/ec/edu/ups/pds/mediator/
```

> **POR COMPLETAR:** reemplazar el árbol por las clases reales, indicando el rol de cada una
> (Mediator, ConcreteMediator, Colleague, ConcreteColleague, Client).

## 3. Pruebas

```text
mvn test
```

> **POR COMPLETAR:** listar las pruebas y qué verifica cada una. Incluir `CorrespondenciaUmlCodigoTest`,
> que lee `../uml/mediatorE1_mejorado.puml`.

## 4. Ejecución

> **POR COMPLETAR:** comando y salida esperada de `Main`, por ejemplo
> `java -cp target/classes ec.edu.ups.pds.mediator.Main`.

## 5. Cómo agregar un participante

> **POR COMPLETAR:** pasos para sumar un colega nuevo (debe bastar con registrarlo en el mediador).
