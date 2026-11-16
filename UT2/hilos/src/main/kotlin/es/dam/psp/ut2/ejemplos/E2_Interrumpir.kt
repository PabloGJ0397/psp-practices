package es.dam.psp.ut2.ejemplos

import kotlin.concurrent.thread

/**
 * E2. Parar un hilo de forma cooperativa con interrupt().
 * Un hilo no se puede "matar": se le pide que pare y él decide cuándo.
 *
 * Apuntes UT-2, apartado 2 (Crear, iniciar y finalizar hilos).
 * Ejecutar: ▶ junto a `main`. Salida esperada: unos 4 bloques descargados, "main: cancelo la
 * descarga", "Interrumpido durante sleep: limpio y salgo" y el número de bloques.
 */
fun main() {
    val descarga = thread(name = "descarga") {
        var bloque = 0
        try {
            while (!Thread.currentThread().isInterrupted) {    // comprobación en bucles de cálculo
                bloque++
                println("Descargado bloque $bloque")
                Thread.sleep(300)                             // si llega interrupt() aquí, salta la excepción
            }
        } catch (e: InterruptedException) {
            println("Interrumpido durante sleep: limpio y salgo")
        }
        println("Hilo ${Thread.currentThread().name} terminado tras $bloque bloques")
    }

    Thread.sleep(1000)
    println("main: cancelo la descarga")
    descarga.interrupt()
    descarga.join()
}
