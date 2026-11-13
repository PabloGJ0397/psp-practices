package es.dam.psp.ut2.ejemplos

import kotlin.concurrent.thread

/**
 * E1. Tres formas de crear un hilo en Kotlin y esperar a que terminen con join().
 *
 * Apuntes UT-2, apartado 2 (Crear, iniciar y finalizar hilos) · Actividad 2.1.
 * Ejecutar: triángulo verde (▶) junto a `main`. Cada ejecución puede mezclar las líneas en otro
 * orden: los hilos se intercalan como decide el planificador.
 * Salida aproximada: "[saludador] hola 0", "[despedidor] adiós 0", "[contador] 1"… y al final
 * "[main] todos han terminado".
 */

// Forma 1: heredar de Thread y sobrescribir run()
class Saludador(private val veces: Int) : Thread("saludador") {
    override fun run() {
        repeat(veces) { println("[${currentThread().name}] hola $it"); sleep(100) }
    }
}

fun main() {
    // Forma 2: pasar un Runnable (una lambda) al constructor de Thread
    val h2 = Thread({
        repeat(3) { println("[${Thread.currentThread().name}] adiós $it"); Thread.sleep(100) }
    }, "despedidor")

    // Forma 3: la función thread { } de Kotlin crea el hilo y lo arranca (start = true)
    val h3 = thread(name = "contador") {
        for (i in 1..3) { println("[${Thread.currentThread().name}] $i"); Thread.sleep(100) }
    }

    val h1 = Saludador(3)
    h1.run()          // start() crea el hilo nuevo; llamar a run() lo ejecutaría en el hilo actual
    h2.run()

    println("[${Thread.currentThread().name}] esperando a los demás…")
    h1.join(); h2.join(); h3.join()          // el hilo main espera a que terminen
    println("[${Thread.currentThread().name}] todos han terminado")
}
