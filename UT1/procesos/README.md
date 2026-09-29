# UT-1 · Procesos (PSP · 2º DAM)

Proyecto Gradle con Kotlin para la JVM. Ábrelo con IntelliJ IDEA (File > Open > carpeta `procesos`).

Cada fichero tiene su propia función `main`: se ejecuta con el triángulo verde que aparece junto a ella.

Código en `src/main/kotlin/es/dam/psp/ut1/`:

- `Jvm.kt`: utilidad para lanzar otra clase del proyecto como proceso hijo (apuntes 5.1 y 5.2).
- `ejemplos/`: los ejemplos de los apuntes y los dos procesos hijo que usan:
  - `E1_Lanzar.kt` (5.1, Actividad 1.4), `E2_LeerSalida.kt` (5.3), `E3_Tuberia.kt` (6),
    `E4_Timeout.kt` (6.1), `E5_Paralelo.kt` (6.3, Actividad 1.5), `E6_MiniPs.kt` (6.4, Práctica 2.c).
  - `Dormilon.kt` (Actividad 1.3, E4, E5 y Práctica 2) y `Mayusculas.kt` (E3).
- `practicas/`: el código de partida de las prácticas 3 (`Lanzador.kt`) y 4 (`GeneradorDatos.kt`,
  `Contador.kt`, `Coordinador.kt`). En la Práctica 2.c crearás aquí `BuscarProcesos.kt`.

Los argumentos de un programa se ponen en Run > Edit Configurations > Program arguments.

Antes de la Práctica 4 ejecuta `GeneradorDatos.kt` para crear la carpeta `datos/`.

Nota: una función `main` escrita en `Contador.kt` se compila en la clase `ContadorKt`.
Ese es el nombre que hay que pasar a `Jvm.proceso(...)`.
