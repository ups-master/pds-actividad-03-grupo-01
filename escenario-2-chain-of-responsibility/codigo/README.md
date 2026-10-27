# Manual de uso - Escenario 2 (Chain of Responsibility)

Cómo compilar, probar y ejecutar el código del escenario 2. Todos los comandos se lanzan desde esta
carpeta (`escenario-2-chain-of-responsibility/codigo`).

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
    ├── main/java/ec/edu/ups/pds/chain/
    │   └── package-info.java          Roles esperados; las clases están POR COMPLETAR
    └── test/java/ec/edu/ups/pds/chain/
```

> **POR COMPLETAR:** reemplazar el árbol por las clases reales, indicando el rol de cada una
> (Handler, ConcreteHandler, Client, solicitud).

## 3. Pruebas

```text
mvn test
```

> **POR COMPLETAR:** listar las pruebas y qué verifica cada una. Incluir `CorrespondenciaUmlCodigoTest`,
> que lee `../uml/chainE2_mejorado.puml`. Casos típicos: cada solicitud la atiende el primer eslabón
> capaz, el límite exacto, qué pasa si nadie atiende, insertar un eslabón sin tocar los demás.

## 4. Ejecución

> **POR COMPLETAR:** comando y salida esperada de `Main`, por ejemplo
> `java -cp target/classes ec.edu.ups.pds.chain.Main`.

## 5. Cómo agregar un eslabón

> **POR COMPLETAR:** pasos para insertar un eslabón nuevo (crear la clase y enlazarla con `setSiguiente`;
> los demás no cambian).
