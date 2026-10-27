# Actividad 03 - Patrones de comportamiento

**UNIVERSIDAD POLITÉCNICA SALESIANA - ECUADOR · Posgrados | Maestría en Software**

> **Estado:** esqueleto. El enunciado todavía no está en el repositorio, así que todo lo que depende del
> dominio dice **POR COMPLETAR**. Para ver todos los huecos: `grep -rn "POR COMPLETAR" .`

## Datos generales de la actividad

| | |
|---|---|
| **Asignatura** | Patrones de Diseño de Software |
| **Actividad de aprendizaje #** | 03 - Patrones de comportamiento |
| **Docente** | Mauricio Ortiz Ochoa |
| **Fecha** | POR COMPLETAR |
| **Grupo base** | Grupo 01 |
| **Resultado de aprendizaje** | POR COMPLETAR (RA3, según el enunciado) |

### Integrantes

| N.º | Integrante (Apellidos Nombres) |
|---|---|
| 1 | Aliatis Ramirez Enzo Paolo |
| 2 | Cadme Vázquez Mauro Emiliano |
| 3 | Murillo Valarezo Jordan Fernando |
| 4 | Suárez Pesantez Galo Fabian |

### Entregables

> **POR COMPLETAR:** confirmar con el enunciado. Por analogía con las actividades anteriores: archivo PDF
> `PDS_Actividad_03_Grupo01.pdf`, aplicación en este repositorio, y carga individual al AVAC.

### Tareas específicas

> **POR COMPLETAR:** copiar del enunciado.

### Requerimientos generales para ambos escenarios

El enunciado completo está en [`docs/enunciado-actividad-03.md`](docs/enunciado-actividad-03.md) (pendiente).

Siguiendo las actividades anteriores, para cada escenario el grupo debe:

1. Identificar el problema de comportamiento.
2. Seleccionar y justificar el patrón correspondiente.
3. Elaborar el diagrama de clases UML (y uno de secuencia).
4. Identificar los participantes del patrón.
5. Implementar la solución en Java y mantener la correspondencia UML-código.
6. Almacenar el código en este repositorio.
7. Documentar el uso crítico de IA: prompt principal, una recomendación aceptada, una modificada o rechazada, y la justificación de ambas.

> **POR COMPLETAR:** verificar esta lista contra el enunciado.

### Rúbrica de evaluación

> **POR COMPLETAR:** copiar la rúbrica del enunciado (criterios por patrón: selección y UML, implementación
> y correspondencia UML-código, y uso crítico de IA y conclusiones).

## Escenarios

| Escenario | Problema | Patrón | Informe |
|---|---|---|---|
| 1 | POR COMPLETAR | Mediator | [`escenario-1-mediator`](escenario-1-mediator/README.md) |
| 2 | POR COMPLETAR | Chain of Responsibility | [`escenario-2-chain-of-responsibility`](escenario-2-chain-of-responsibility/README.md) |

Las carpetas llevan el nombre del patrón porque el dominio aún no se conoce. Al llegar el enunciado se
pueden renombrar (por ejemplo `escenario-1-registro-vehicular` en la actividad 01); en ese caso hay que
actualizar los `<module>` de [`pom.xml`](pom.xml) y los enlaces de este archivo.

La referencia teórica y los ejemplos guiados (sala de chat y aprobaciones de compra) están en
`class-activities-pds/unidad-03-comportamiento/complementarios/{mediator,chain-of-responsibility}`.

## Probar el proyecto

Requisitos: JDK 21 o superior y Maven 3.9 o posterior.

```text
mvn test
```

Desde la raíz compila los dos módulos. Por ahora no hay pruebas; el conteo esperado por escenario es
POR COMPLETAR.

## Conclusiones de la actividad

> **POR COMPLETAR:** conclusiones de unas 200 palabras, cuando estén implementados los dos escenarios.

## Estructura del repositorio

```text
.
├── README.md                              Este archivo
├── CLAUDE.md                              Guía para Claude Code
├── pom.xml                                POM padre: Java 21 y JUnit para los dos módulos
├── docs/
│   └── enunciado-actividad-03.md          Enunciado y plantilla (pendiente)
├── escenario-1-mediator/
│   ├── README.md                          Informe del escenario 1
│   ├── uml/                               Diagramas PlantUML (clases y secuencia)
│   ├── codigo/                            Proyecto Maven y manual de uso
│   └── docs/                              Trazabilidad UML-código
└── escenario-2-chain-of-responsibility/
    ├── README.md                          Informe del escenario 2
    ├── uml/                               Diagramas PlantUML (clases y secuencia)
    ├── codigo/                            Proyecto Maven y manual de uso
    └── docs/                              Trazabilidad UML-código
```
