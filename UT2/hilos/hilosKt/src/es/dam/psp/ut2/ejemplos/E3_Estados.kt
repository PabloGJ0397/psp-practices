package es.dam.psp.ut2.ejemplos

/**
 * E3. Estados de un hilo en la JVM: NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.
 *
 * Apuntes UT-2, apartado 3 (Estados de un hilo) · Actividad 2.2.
 * Usa synchronized, wait() y notifyAll() solo para provocar los estados BLOCKED y WAITING;
 * se explican en los apartados 6 y 7.
 * Ejecutar: ▶ junto a `main`. Salida esperada, en este orden: NEW, TIMED_WAITING, WAITING,
 * BLOCKED, RUNNABLE y TERMINATED.
 */
fun main() {
    val cerrojo = Object()        // java.lang.Object (no Any): wait() y notifyAll() son métodos suyos

    val dormilon = Thread({ Thread.sleep(500) }, "dormilon")
    println("Recién creado:     ${dormilon.state}")          // NEW
    dormilon.start()
    Thread.sleep(50)
    println("Durmiendo:         ${dormilon.state}")          // TIMED_WAITING

    val esperador = Thread({ synchronized(cerrojo) { cerrojo.wait() } }, "esperador")
    esperador.start()
    Thread.sleep(50)
    println("En wait():         ${esperador.state}")         // WAITING

    val bloqueado: Thread                                     // se asigna dentro del synchronized
    synchronized(cerrojo) {                                   // main se queda con el cerrojo...
        bloqueado = Thread({ synchronized(cerrojo) { } }, "bloqueado")
        bloqueado.start()
        Thread.sleep(50)
        println("Esperando cerrojo: ${bloqueado.state}")      // BLOCKED
        cerrojo.notifyAll()                                   // despierta al esperador
    }

    val calculador = Thread({ var x = 0L; while (!Thread.currentThread().isInterrupted) x++ }, "calculador")
    calculador.start()
    Thread.sleep(50)
    println("Calculando:        ${calculador.state}")        // RUNNABLE
    calculador.interrupt()                                    // su bucle comprueba isInterrupted y acaba

    listOf(dormilon, esperador, bloqueado, calculador).forEach { it.join() }
    println("Al final:          ${dormilon.state}")          // TERMINATED
}
