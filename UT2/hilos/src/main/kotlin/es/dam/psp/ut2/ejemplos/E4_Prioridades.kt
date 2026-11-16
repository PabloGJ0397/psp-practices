package es.dam.psp.ut2.ejemplos

import java.util.concurrent.atomic.AtomicBoolean
import kotlin.concurrent.thread

/**
 * E4. Prioridades: una PISTA para el planificador, no una orden.
 * Lanza el doble de hilos que núcleos (la mitad con prioridad 10 y la otra mitad con 1), todos
 * compitiendo por la CPU durante 2 s. Aun así, la diferencia de trabajo es pequeña (≈ 5 % en un
 * equipo de 4 núcleos) y en Linux suele ser nula, porque allí la JVM ignora por defecto las prioridades.
 *
 * Apuntes UT-2, apartado 4 (Prioridades y planificación).
 * Ejecutar: ▶ junto a `main`. Tarda unos 2 s y muestra las iteraciones de cada grupo y la
 * relación alta/baja (cercana a 1,00).
 */
fun main() {
    val parar = AtomicBoolean(false)          // bandera que main escribe y los hilos leen (apartado 6.1)
    val nucleos = Runtime.getRuntime().availableProcessors()
    val cuentas = LongArray(nucleos * 2)      // cada hilo escribe solo en su posición: no hay carrera

    val hilos = List(nucleos * 2) { i ->
        thread(start = false, name = "h$i") {
            var c = 0L
            while (!parar.get()) c++
            cuentas[i] = c
        }.apply { priority = if (i % 2 == 0) Thread.MAX_PRIORITY else Thread.MIN_PRIORITY }
    }
    hilos.forEach { it.start() }             // la prioridad se fija antes de start()
    Thread.sleep(2000)
    parar.set(true)
    hilos.forEach { it.join() }

    val alta = cuentas.filterIndexed { i, _ -> i % 2 == 0 }.sum()
    val baja = cuentas.filterIndexed { i, _ -> i % 2 == 1 }.sum()
    println("Prioridad ${Thread.MAX_PRIORITY}: $alta iteraciones")
    println("Prioridad ${Thread.MIN_PRIORITY}: $baja iteraciones")
    println("Relación alta/baja: %.2f".format(alta.toDouble() / baja))
}
