package es.dam.psp.ut2.ejemplos

import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.time.measureTime

/**
 * E9. Un pool de hilos (ExecutorService) que cuenta primos por tramos.
 * Callable devuelve un valor; Future es la "promesa" de ese valor.
 *
 * Apuntes UT-2, apartado 8 (Pools de hilos y tareas) · Actividad 2.5.
 * Ejecutar: ▶ junto a `main`. Muestra los primos menores que 5 000 000 (348 513) y el tiempo
 * con 1 hilo y con el pool. Para la Actividad 2.5 cambia el valor de `tamPool`.
 */
fun esPrimo(n: Int): Boolean {
    if (n < 2) return false
    var d = 2
    while (d * d <= n) { if (n % d == 0) return false; d++ }
    return true
}

fun contarPrimos(desde: Int, hasta: Int) = (desde until hasta).count { esPrimo(it) }

fun main() {
    val limite = 5_000_000
    val nucleos = Runtime.getRuntime().availableProcessors()

    var total1 = 0
    val t1 = measureTime { total1 = contarPrimos(0, limite) }  // measureTime: lo que tarda el bloque

    val tamPool = nucleos                                       // Actividad 2.5: prueba 1, 2, 4, 8 y 16
    val pool = Executors.newFixedThreadPool(tamPool)
    val tramo = limite / 16                                     // más tareas que hilos: reparto equilibrado
    // El último tramo llega hasta limite para no perder los números que sobran de la división.
    val tareas = (0 until 16).map { i -> Callable { contarPrimos(i * tramo, if (i == 15) limite else (i + 1) * tramo) } }
    var total2 = 0
    val t2 = measureTime { total2 = pool.invokeAll(tareas).sumOf { it.get() } }   // get() espera el resultado
    pool.shutdown()                                             // sin esto el programa no termina

    println("Núcleos: $nucleos")
    println("1 hilo:            $total1 primos en $t1")
    println("Pool de $tamPool hilos:  $total2 primos en $t2")
}
