package es.dam.psp.ut1.practicas

import es.dam.psp.ut1.Jvm
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Lanzador · Práctica 3 (UT-1, Procesos y servicios).
 *
 * Pide órdenes por consola en bucle y ejecuta cada una como proceso hijo, a través de la
 * shell del sistema (Jvm.comandoShell). Termina cuando se escribe "salir".
 *
 * Para cada orden:
 *  - a) Muestra la salida del hijo (salida estándar y errores juntos) con cada línea numerada.
 *       La salida se redirige a un fichero temporal para que el padre no se quede bloqueado
 *       leyendo si el hijo no termina nunca.
 *  - b) Muestra "[código X · Y ms]": código de salida y tiempo transcurrido desde start().
 *  - c) Espera como máximo TIMEOUT_S segundos. Si se pasa, avisa, termina a los descendientes
 *       y al proceso (destroy y, si sigue vivo, destroyForcibly) y usa -1 como código.
 *  - d) Añade cada ejecución a historial.txt con el formato: orden;codigo;milisegundos
 *
 * Cómo ejecutarlo: triángulo verde junto a main, sin argumentos, y escribir las órdenes en
 * la consola de IntelliJ (p. ej. "echo hola", "dir noexiste" o "ping -n 11 127.0.0.1").
 */

const val TIMEOUT_S = 5L   // segundos que se deja trabajar a cada orden

fun main() {
    println("Lanzador PSP · sistema: ${System.getProperty("os.name")} · escribe 'salir' para terminar")
    while (true) {
        print("> ")
        // Lee lo que escribe el usuario y quita espacios de los extremos; null = fin de la entrada
        val orden = readlnOrNull()?.trim() ?: break
        if (orden == "salir") break          // "salir" termina el programa
        if (orden.isEmpty()) continue        // si no escribe nada, vuelve a pedir otra orden

        // Fichero temporal donde el hijo escribirá su salida (así el padre no se bloquea leyendo)
        val tempFile = File.createTempFile("temp", ".txt")
        // Prepara el proceso: la orden se ejecuta con la shell del sistema, los errores se mezclan
        // con la salida normal y todo se redirige al fichero temporal
        val pb = ProcessBuilder(Jvm.comandoShell(orden)).redirectErrorStream(true).redirectOutput(tempFile)

        val inicio = System.nanoTime()       // cronómetro: instante de arranque
        val proceso = pb.start()             // arranca el proceso hijo
        // Espera como máximo TIMEOUT_S segundos; devuelve true si terminó a tiempo, false si no
        val terminoATiempo = proceso.waitFor(TIMEOUT_S, TimeUnit.SECONDS)
        if (!terminoATiempo) {
            println("Se han agotado los $TIMEOUT_S segundos")
            proceso.descendants().forEach { it.destroy() }   // termina primero a los procesos que lanzó
            proceso.destroy()                                // pide al proceso que termine
            if (proceso.isAlive) proceso.destroyForcibly()   // si sigue vivo, lo fuerza
        }
        // Tiempo transcurrido en milisegundos (los nanosegundos se dividen entre 1.000.000)
        val ms = (System.nanoTime() - inicio) / 1_000_000
        // Código de salida del hijo, o -1 si hubo que matarlo por pasarse de tiempo
        val codigoPrograma = if (terminoATiempo) proceso.exitValue() else -1

        // Lee el fichero temporal (CP850 = codificación de la consola de Windows en español)
        val lineas = tempFile.readLines()
        // Muestra cada línea con su número (las posiciones empiezan en 0, por eso se suma 1)
        lineas.forEachIndexed { posicion, elemento -> println("${posicion + 1} : $elemento") }
        tempFile.delete()                    // borra el fichero temporal
        println("[código $codigoPrograma · $ms ms]")
        // Añade una línea al historial con el formato orden;codigo;milisegundos
        File("historial.txt").appendText("$orden;$codigoPrograma;$ms\n")
    }
    println("Hasta luego")
}