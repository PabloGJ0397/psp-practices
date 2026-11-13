package es.dam.psp.ut2.ejemplos

import kotlin.concurrent.thread

/**
 * E6. Interbloqueo (deadlock): cada hilo tiene un recurso y espera el del otro.
 *
 * Apuntes UT-2, apartado 6.2 (Interbloqueo) · Actividad 2.3.
 * Ejecutar: ▶ junto a `main`. El programa se queda colgado para siempre tras imprimir
 * "hilo-1 tiene A, quiere B" y "hilo-2 tiene B, quiere A".
 * Mientras está colgado: en una terminal, `jps -l` (PID) y `jstack <PID>`, o el botón
 * Dump Threads (icono de cámara) de la ventana Run de IntelliJ. Al final del volcado verás
 * "Found one Java-level deadlock". Para pararlo, pulsa el cuadrado rojo (Stop).
 */
fun main() {
    val a = Any()
    val b = Any()

    thread(name = "hilo-1") {
        synchronized(a) {
            println("hilo-1 tiene A, quiere B")
            Thread.sleep(100)               // da tiempo a que el otro hilo coja su cerrojo
            synchronized(b) { println("hilo-1 tiene A y B") }
        }
    }
    thread(name = "hilo-2") {
        synchronized(b) {                   // hilo-2 pide primero B y luego A
            println("hilo-2 tiene B, quiere A")
            Thread.sleep(100)
            synchronized(a) { println("hilo-2 tiene B y A") }
        }
    }
}
