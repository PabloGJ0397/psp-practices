package es.dam.psp.ut2.practicas

import kotlin.random.Random

/**
 * Práctica 1 · Carrera de hilos. Código de partida: completa los TODO (sus letras son las de
 * los apartados del enunciado).
 *
 * Apuntes UT-2, apartados 2 (crear, esperar e interrumpir), 3 (estados) y 6.1 (synchronized).
 * Ejecutar: ▶ junto a `main`. Mientras falten los TODO, lanza NotImplementedError.
 *
 * Cada corredor es un hilo que avanza entre 1 y 10 metros cada 100-300 ms hasta llegar a 100 m.
 * La Meta registra el orden de llegada. Cuando han llegado 3 corredores (podio), el juez
 * interrumpe a los demás, que deben retirarse indicando cuántos metros llevaban.
 */

const val DISTANCIA = 100
const val PODIO = 3

class Meta {
    private val llegadas = mutableListOf<String>()

    /**
     * TODO a: registra la llegada de [nombre] y devuelve su puesto (1, 2, 3...).
     * Varios hilos llaman a este método a la vez: protégelo.
     */
    fun llegar(nombre: String): Int {
        TODO("Práctica 1.a")
    }

    /** Número de corredores que han llegado. También debe estar protegido. */
    fun llegados(): Int {
        TODO("Práctica 1.a")
    }

    /** Copia de la lista de llegadas, hecha dentro del cerrojo para que nadie la cambie a la vez. */
    fun clasificacion(): List<String> = synchronized(this) { llegadas.toList() }
}

class Corredor(nombre: String, private val meta: Meta) : Thread(nombre) {
    var metros = 0
        private set                 // solo el propio corredor cambia sus metros; el juez los lee

    override fun run() {
        // TODO b: bucle de carrera. Mientras metros < DISTANCIA: dormir entre 100 y 300 ms y
        //  avanzar Random.nextInt(1, 11) (sin pasar de DISTANCIA). Al llegar, llamar a
        //  meta.llegar(name) y mostrar el puesto.
        //  Si le interrumpen (InterruptedException), mostrar "<nombre> se retira en X m" y terminar.
    }
}

fun main() {
    val meta = Meta()
    val corredores = listOf("Ana", "Bruno", "Carla", "David", "Elena", "Fran").map { Corredor(it, meta) }

    // TODO c: arranca a todos los corredores.

    // TODO c: el hilo main hace de juez. Cada 500 ms muestra una línea con el estado (state) y los
    //  metros de cada corredor. Cuando meta.llegados() >= PODIO, interrumpe a los que sigan vivos
    //  (isAlive) y sale del bucle.

    // TODO d: espera a que terminen todos (join) y muestra la clasificación final.
}
