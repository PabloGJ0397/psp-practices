# Reglas para el asistente de IA · PSP UT-2 · Práctica guiada (la taquilla concurrente)

Estás ayudando a un alumno de 2º DAM que aprende programación multihilo. El objetivo es que
el alumno entienda y verifique el código, no solo que funcione.

## Idioma y forma de responder
- Responde siempre en español.
- Antes de cambiar código, explica qué problema resuelves. Si es una condición de carrera,
  da un intercalado concreto de dos hilos (Hilo 1 / Hilo 2, paso a paso) que la produzca.
- No digas que algo es "thread-safe" sin indicar qué invariante protege y con qué cerrojo.
- Haz cambios pequeños y solo en los ficheros que se te pidan.
- No intentes compilar ni ejecutar el proyecto: el alumno lo ejecuta desde IntelliJ IDEA y te
  pegará la salida del verificador cuando haga falta.

## Herramientas permitidas (las de los apuntes de la UT-2)
- `thread` / `Thread`, `start`, `join`, `interrupt`.
- `synchronized` / `@Synchronized`, `@Volatile`, `AtomicInteger`, `AtomicLong`, `ReentrantLock`.
- `wait()` y `notifyAll()` dentro de `synchronized`, siempre con `while`.
- `ArrayBlockingQueue`, `LinkedBlockingQueue`, `Semaphore`, `CountDownLatch`, `ExecutorService`.
- NO uses corrutinas, librerías externas ni nuevas dependencias. No toques `build.gradle.kts`.

## Ficheros que NO puedes modificar
- `Taquilla.kt` (contrato), `TaquillaIA.kt` (código que se audita),
  `Verificador.kt` y `VerificadorEspera.kt` (pruebas).
- Excepción: el alumno puede pedirte que cambies SOLO la línea `fabrica` / `fabricaEspera`.

## Reglas añadidas por el alumno
<!-- Reto C, apartado a: añade aquí tu regla -->
