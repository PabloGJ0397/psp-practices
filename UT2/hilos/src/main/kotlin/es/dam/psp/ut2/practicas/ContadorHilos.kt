package es.dam.psp.ut2.practicas

import java.io.File
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.Executors
import kotlin.time.measureTime

/**
 * Práctica 4 · El contador de la UT-1, ahora con un pool de hilos. Código de partida: completa
 * los TODO (sus letras son las de los apartados del enunciado).
 *
 * Apuntes UT-2, apartado 8 (pools, Callable, Future y colecciones concurrentes).
 * Ejecutar: primero practicas/GeneradorDatos.kt (crea datos/); después ▶ junto a `main`.
 * Para 1, 2, 4 y 8 hilos, el total de palabras debe ser siempre 4 751 024.
 *
 * Cada fichero de datos/ es una tarea (Callable) que devuelve su número de líneas y palabras
 * y, además, suma la frecuencia de cada palabra en un mapa GLOBAL compartido por todas las tareas.
 */

data class Recuento(val fichero: String, val lineas: Long, val palabras: Long)

/**
 * TODO a: cuenta líneas y palabras de [fichero] y suma cada palabra (en minúsculas) en [frecuencias].
 * Varios hilos usarán el mismo mapa a la vez: usa una operación atómica de ConcurrentHashMap (merge).
 */
fun contar(fichero: File, frecuencias: ConcurrentHashMap<String, Long>): Recuento {
    TODO("Práctica 4.a")
}

/**
 * TODO b: procesa todos los ficheros con un pool fijo de [hilos] hilos:
 *  una Callable por fichero, invokeAll, recoger los Future y cerrar el pool (shutdown).
 */
fun procesar(ficheros: List<File>, hilos: Int, frecuencias: ConcurrentHashMap<String, Long>): List<Recuento> {
    TODO("Práctica 4.b")
}

fun main() {
    // Ruta relativa: datos/ en la raíz del proyecto hilos (el directorio de trabajo de IntelliJ).
    val ficheros = File("datos").listFiles { f -> f.extension == "txt" }?.sortedBy { it.name }
    if (ficheros.isNullOrEmpty()) { println("Ejecuta antes GeneradorDatos.kt"); return }
    println("Núcleos: ${Runtime.getRuntime().availableProcessors()}")

    for (n in listOf(1, 2, 4, 8)) {
        val frecuencias = ConcurrentHashMap<String, Long>()
        lateinit var r: List<Recuento>           // se asigna dentro de measureTime
        val t = measureTime { r = procesar(ficheros, n, frecuencias) }
        // TODO c: muestra, para cada n, el tiempo, el total de palabras y las 3 palabras más frecuentes.
    }
}
