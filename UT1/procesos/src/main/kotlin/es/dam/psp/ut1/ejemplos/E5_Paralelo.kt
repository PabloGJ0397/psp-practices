package es.dam.psp.ut1.ejemplos

import es.dam.psp.ut1.Jvm
import kotlin.time.measureTime

/**
 * E5 · Varios procesos a la vez: secuencial frente a concurrente.
 *
 * Apuntes UT-1, apartado 6.3. Actividad 1.5: cambia n por 8 y por 16.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos. Los hijos son ejemplos/Dormilon.kt.
 * Cuatro tareas de 2 s: una detrás de otra tardan ~8 s; a la vez, ~2 s.
 * Los mensajes de los hijos concurrentes salen mezclados porque todos comparten la consola.
 */
fun main() {
    val n = 4                                   // número de hijos (Actividad 1.5: prueba con 8 y 16)

    val secuencial = measureTime {              // measureTime devuelve lo que tarda el bloque
        repeat(n) {
            // lanzar y esperar en la misma vuelta: el siguiente no empieza hasta que acaba este
            Jvm.proceso("es.dam.psp.ut1.ejemplos.DormilonKt", "2").inheritIO().start().waitFor()
        }
    }

    val concurrente = measureTime {
        val hijos = List(n) { Jvm.proceso("es.dam.psp.ut1.ejemplos.DormilonKt", "2").inheritIO().start() }
        hijos.forEach { it.waitFor() }          // punto de sincronización: esperar a TODOS
    }

    println("Secuencial:  $secuencial")
    println("Concurrente: $concurrente")
}
