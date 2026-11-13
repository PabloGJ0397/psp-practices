package es.dam.psp.ut2.ejemplos

import kotlin.concurrent.thread

/**
 * E7. Productor-consumidor con un buffer acotado y wait() / notifyAll().
 * El productor espera si el buffer está lleno; el consumidor, si está vacío.
 *
 * Apuntes UT-2, apartado 7.1 (wait() y notifyAll()) · Actividad 2.4.
 * Ejecutar: ▶ junto a `main`. Como el productor es más rápido, el buffer se llena enseguida
 * ([1, 2, 3]) y a partir de ahí cada "+" espera a un "-". Termina con "Fin".
 */
class Buffer(private val capacidad: Int) {
    private val cola = ArrayDeque<Int>()
    private val monitor = Object()

    fun poner(x: Int) = synchronized(monitor) {
        while (cola.size == capacidad) monitor.wait()      // SIEMPRE while, nunca if
        cola.addLast(x)
        println("  + $x   (buffer: $cola)")
        monitor.notifyAll()                                // avisa a quien espere
    }

    fun sacar(): Int = synchronized(monitor) {
        while (cola.isEmpty()) monitor.wait()
        val x = cola.removeFirst()
        println("  - $x   (buffer: $cola)")
        monitor.notifyAll()
        x
    }
}

fun main() {
    val buffer = Buffer(3)
    val productor = thread(name = "productor") {
        for (i in 1..10) { buffer.poner(i); Thread.sleep(50) }        // produce rápido
    }
    val consumidor = thread(name = "consumidor") {
        repeat(10) { buffer.sacar(); Thread.sleep(150) }            // consume lento
    }
    productor.join(); consumidor.join()
    println("Fin")
}
