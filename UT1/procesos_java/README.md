# UT-1 · Procesos (PSP · 2º DAM) · versión Java

Es el mismo proyecto que `procesos` (Kotlin), escrito en Java 17, para quien quiera tener
la referencia en el lenguaje que ya conoce. Los apuntes y las prácticas usan Kotlin: los
nombres de ficheros, métodos y TODO son los mismos para que puedas compararlos línea a línea.

Ábrelo con IntelliJ IDEA (File > Open > carpeta `procesos_java`).
Cada clase tiene su propio método `main`: se ejecuta con el triángulo verde que aparece junto a él.

Código en `src/main/java/es/dam/psp/ut1/`:

- `Jvm.java`: utilidad para lanzar otra clase del proyecto como proceso hijo (apuntes 5.1 y 5.2).
- `ejemplos/`: los ejemplos de los apuntes y los dos procesos hijo que usan:
  - `E1_Lanzar.java` (5.1, Actividad 1.4), `E2_LeerSalida.java` (5.3), `E3_Tuberia.java` (6),
    `E4_Timeout.java` (6.1), `E5_Paralelo.java` (6.3, Actividad 1.5), `E6_MiniPs.java` (6.4, Práctica 2.c).
  - `Dormilon.java` (Actividad 1.3, E4, E5 y Práctica 2) y `Mayusculas.java` (E3).
- `practicas/`: el código de partida de las prácticas 3 (`Lanzador.java`) y 4 (`GeneradorDatos.java`,
  `Contador.java`, `Coordinador.java`). En la Práctica 2.c crearás aquí `BuscarProcesos.java`.

Los argumentos de un programa se ponen en Run > Edit Configurations > Program arguments.

Antes de la Práctica 4 ejecuta `GeneradorDatos.java` para crear la carpeta `datos/`.

## Diferencias con la versión Kotlin

- **Nombre de la clase hija.** En Kotlin, el `main` de `Dormilon.kt` se compila en la clase
  `DormilonKt`; en Java la clase se llama igual que el fichero. Donde los apuntes pongan
  `Jvm.proceso("es.dam.psp.ut1.ejemplos.DormilonKt", ...)`, en Java es
  `Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", ...)` (igual con `MayusculasKt` y `ContadorKt`).
- **`object Jvm`** de Kotlin es aquí una clase con miembros `static`.
- **`data class Resultado`** es aquí un `record` (Java 16+): también compara por campos con `equals`.
- **`TODO("...")`** de Kotlin es aquí `throw new UnsupportedOperationException("TODO ...")`.
- **`measureTime { }`** no existe en Java: se mide con `System.nanoTime()` antes y después.
- Los hijos que leen texto por la entrada estándar (`Mayusculas`, `Contador`) lo leen en UTF-8
  de forma explícita, y el padre escribe en UTF-8, para que los resultados sean los mismos en
  Windows, Linux y macOS.
