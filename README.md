# Actividad 03 - Patrones de comportamiento

**UNIVERSIDAD POLITÉCNICA SALESIANA - ECUADOR · Posgrados | Maestría en Software**

## Datos generales de la actividad

| | |
|---|---|
| **Asignatura** | Patrones de Diseño de Software |
| **Actividad de aprendizaje #** | 03 - Patrones de comportamiento |
| **Docente** | Mauricio Ortiz Ochoa |
| **Fecha** | 2026/10/29 |
| **Grupo base** | Grupo 01 |
| **Resultado de aprendizaje** | RA3. Implementa soluciones aplicando patrones de comportamiento para organizar responsabilidades e interacciones entre objetos. |

### Integrantes

| N.º | Integrante (Apellidos Nombres) |
|---|---|
| 1 | Aliatis Ramirez Enzo Paolo |
| 2 | Cadme Vázquez Mauro Emiliano |
| 3 | Murillo Valarezo Jordan Fernando |
| 4 | Suárez Pesantez Galo Fabian |

### Entregables

- Archivo en PDF con el nombre `PDS_Actividad_03_Grupo01.pdf`.
- Repositorio: <https://github.com/ups-master/pds-actividad-03-grupo-01>
- Aplicación de software almacenada en un repositorio de código (este repositorio).
- Aunque la actividad se realiza en grupo, la carga al AVAC es individual.

### Tareas específicas

- Participar en las clases de la Unidad 03, secciones 01 y 02, y leer el material de apoyo.
- Crear un repositorio de código para almacenar la aplicación solicitada.
- Analizar los escenarios, identificar el problema de comportamiento y seleccionar y justificar los patrones.
- Elaborar los diagramas de clases UML e implementar las soluciones manteniendo la correspondencia UML-código.
- Utilizar inteligencia artificial como apoyo para revisar, cuestionar y mejorar las decisiones de diseño, y
  documentar al menos una recomendación aceptada, modificada o rechazada con su justificación.

### Requerimientos generales para ambos escenarios

El enunciado completo y la plantilla oficial están en [`docs/enunciado-actividad-03.md`](docs/enunciado-actividad-03.md).
Los patrones elegidos (Chain of Responsibility y Mediator) no son los desarrollados directamente en clase.

Para cada escenario, el grupo debe:

1. Plantear el problema, describir el contexto y explicar por qué una solución directa dificulta la evolución.
2. Seleccionar y justificar el patrón, identificar sus participantes y describir cómo colaboran.
3. Elaborar el diagrama de clases UML (y uno de secuencia).
4. Implementar la solución en Java y mantener la correspondencia UML-código.
5. Validar la solución: qué problema resolvió, qué mejoró, qué costos introdujo y cuándo no usar el patrón.
6. Almacenar el código en este repositorio.
7. Documentar el uso crítico de IA: prompt principal, una recomendación aceptada, una modificada o rechazada, y la justificación de ambas.

### Rúbrica de evaluación

Cada criterio se califica como No presenta (0), Nivel bajo (2), Nivel medio (4) o Nivel alto (6):

- Selección, justificación y diagrama UML del Patrón 01 (Chain of Responsibility).
- Implementación funcional y correspondencia UML-código del Patrón 01.
- Selección, justificación y diagrama UML del Patrón 02 (Mediator).
- Implementación funcional y correspondencia UML-código del Patrón 02.
- Uso crítico de IA, síntesis comparativa, justificación y conclusiones.

## Escenarios

| Escenario | Problema | Patrón | Informe |
|---|---|---|---|
| 1 | Validación de pedidos de Andes Market | Chain of Responsibility | [`escenario-1-chain-of-responsibility`](escenario-1-chain-of-responsibility/README.md) |
| 2 | Coordinación de cocina de El Fogón Andino | Mediator | [`escenario-2-mediator`](escenario-2-mediator/README.md) |

Cada escenario contiene su informe con las secciones de la plantilla, el UML original y el mejorado (con su
secuencia), el código Java, el manual de uso y la trazabilidad UML-código. El diagrama mejorado es el contrato
que implementa el código y que verifica `CorrespondenciaUmlCodigoTest`.

La referencia teórica y los ejemplos guiados están en
`class-activities-pds/unidad-03-comportamiento/complementarios/{mediator,chain-of-responsibility}`.

## Probar el proyecto

Requisitos: JDK 21 o superior y Maven 3.9 o posterior.

```text
mvn test
```

Desde la raíz ejecuta **145 pruebas JUnit** (69 y 76 por escenario), todas aprobadas:

| Escenario | Correspondencia UML-código | Funcionales | Total |
|---|---|---|---|
| 1. Chain of Responsibility | 42 | 27 | 69 |
| 2. Mediator | 45 | 31 | 76 |

Para un solo escenario: `mvn test -pl escenario-1-chain-of-responsibility/codigo` o
`mvn test -pl escenario-2-mediator/codigo`.

Para ver cada demo:

```text
mvn -q compile exec:java -pl escenario-1-chain-of-responsibility/codigo
mvn -q compile exec:java -pl escenario-2-mediator/codigo
```

## Conclusiones de la actividad

Aplicamos dos patrones de comportamiento a problemas distintos, y en ambos el punto de partida fue identificar qué interacción estaba mal repartida antes de elegir la solución. En Andes Market, quien procesaba el pedido conocía todas las reglas de validación. Con Chain of Responsibility cada regla es un eslabón que aprueba y delega, o rechaza y detiene, y así agregamos cupones, omitimos antifraude para clientes corporativos y reordenamos validaciones sin tocar al procesador. En El Fogón Andino, los participantes se referenciaban entre sí. Con Mediator, JefeDeCocina concentra el reparto de platos por estación, el estado de cada mesa y los faltantes, y los colegas solo conocen al mediador.

Al compararlos, ambos reducen el acoplamiento, pero de forma opuesta: en Chain la decisión queda distribuida en cada eslabón y el flujo es lineal, mientras que en Mediator se centraliza y la comunicación es de muchos a muchos. Por eso el riesgo de Chain es una cadena mal armada, y el de Mediator, un mediador demasiado grande.

Aprendimos que el UML es contrato solo si algo lo verifica: la prueba de correspondencia, que lee el diagrama y lo compara con el código por reflexión, nos obligó a mantener ambos sincronizados. La IA sirvió para cuestionar el diseño inicial, pero sus alternativas, como un bucle de validadores o la notificación directa entre estaciones, había que contrastarlas con el enunciado, y algunas se rechazaron.

## Estructura del repositorio

```text
.
├── README.md                              Este archivo
├── AGENTS.md                              Guía para agentes de código
├── pom.xml                                POM padre: Java 21 y JUnit para los dos módulos
├── docs/
│   └── enunciado-actividad-03.md          Enunciado y plantilla oficial transcrita
├── escenario-1-chain-of-responsibility/
│   ├── README.md                          Informe del escenario 1
│   ├── uml/                               chainE1 (original), chainE1_mejorado, chainE1_secuencia, PNG, HTML interactivo y PNG de archify
│   ├── codigo/                            Proyecto Maven y manual de uso
│   └── docs/                              Trazabilidad UML-código
└── escenario-2-mediator/
    ├── README.md                          Informe del escenario 2
    ├── uml/                               mediatorE2 (original), mediatorE2_mejorado, mediatorE2_secuencia, PNG, HTML interactivo y PNG de archify
    ├── codigo/                            Proyecto Maven y manual de uso
    └── docs/                              Trazabilidad UML-código
```
