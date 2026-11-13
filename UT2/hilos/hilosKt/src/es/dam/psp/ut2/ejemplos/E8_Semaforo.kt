package es.dam.psp.ut2.ejemplos

import java.util.concurrent.CountDownLatch
import java.util.concurrent.Semaphore
import kotlin.concurrent.thread

/**
 * E8. Semaphore limita cuántos hilos usan un recurso a la vez (un aparcamiento de 3 plazas).
 *     CountDownLatch hace que un hilo espere a que otros terminen una fase.
 *
 * Apuntes UT-2, apartado 7.2 (Las herramientas de java.util.concurrent).
 * Ejecutar: ▶ junto a `main`. Nunca hay más de 3 coches dentro ("libres" nunca baja de 0) y
 * el programa acaba con "Aparcamiento vacío" cuando han salido los 6.
 */
fun main() {
    val plazas = Semaphore(3)
    val todosFuera = CountDownLatch(6)                         // cuenta atrás: llega a 0 cuando salen los 6

    repeat(6) { n ->
        thread(name = "coche-$n") {
            plazas.acquire()                                   // si no hay plaza, espera
            try {
                println("coche-$n aparca   (libres: ${plazas.availablePermits()})")
                Thread.sleep(300L + n * 50)
            } finally {
                println("coche-$n sale")
                plazas.release()                               // siempre en finally
                todosFuera.countDown()                         // un coche menos por salir
            }
        }
    }
    todosFuera.await()                                         // main espera a que salgan los 6
    println("Aparcamiento vacío")
}
