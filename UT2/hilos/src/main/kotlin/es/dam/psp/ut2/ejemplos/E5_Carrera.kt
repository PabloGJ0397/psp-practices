package es.dam.psp.ut2.ejemplos

import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.thread
import kotlin.concurrent.withLock

/**
 * E5. Condición de carrera y tres formas de evitarla.
 * contador++ son tres pasos (leer, sumar, escribir): dos hilos pueden intercalarlos y perder sumas.
 *
 * Apuntes UT-2, apartados 5 (Compartir información) y 6.1 (synchronized, atómicas y cerrojos).
 * Ejecutar: ▶ junto a `main`. "Sin proteger" da un número menor que 4 000 000 y distinto en
 * cada ejecución; las otras tres líneas dan siempre 4 000 000.
 */
const val HILOS = 4
const val VECES = 1_000_000

var sinProteger = 0

var conSynchronized = 0
private val cerrojo = Any()                  // cualquier objeto sirve como cerrojo

val atomico = AtomicInteger(0)

var conLock = 0
private val lock = ReentrantLock()

/** Lanza HILOS hilos que ejecutan [tarea] VECES veces cada uno y espera (join) a que acaben todos. */
fun lanzar(tarea: () -> Unit) = List(HILOS) { thread { repeat(VECES) { tarea() } } }.forEach { it.join() }

fun main() {
    lanzar { sinProteger++ }
    lanzar { synchronized(cerrojo) { conSynchronized++ } }
    lanzar { atomico.incrementAndGet() }
    lanzar { lock.withLock { conLock++ } }

    val esperado = HILOS * VECES
    println("Esperado:        $esperado")
    println("Sin proteger:    $sinProteger   ← se pierden ${esperado - sinProteger} sumas")
    println("synchronized:    $conSynchronized")
    println("AtomicInteger:   ${atomico.get()}")
    println("ReentrantLock:   $conLock")
}
