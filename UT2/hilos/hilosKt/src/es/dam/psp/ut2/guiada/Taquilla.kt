package es.dam.psp.ut2.guiada

/**
 * Práctica guiada · Taquilla de entradas. Contrato que deben cumplir todas las taquillas.
 *
 * Apuntes UT-2, apartados 5 (condición de carrera), 6.1 (synchronized, atómicas y cerrojos)
 * y 7.1 (wait() y notifyAll()). Este fichero no se ejecuta: lo usan Verificador.kt y
 * VerificadorEspera.kt (misma carpeta, guiada/).
 *
 * NO MODIFIQUES ESTE FICHERO (ni tú ni la IA): es la especificación.
 */

/** Precio de cada entrada, en euros. */
const val PRECIO = 25L

/**
 * Taquilla con asientos numerados de 0 a [aforo] - 1.
 * Todos los métodos pueden llamarse a la vez desde varios hilos.
 */
interface Taquilla {
    /** Número total de asientos. */
    val aforo: Int

    /**
     * Vende [n] asientos a [cliente]: TODOS O NINGUNO.
     * Devuelve los números de los [n] asientos vendidos, o una lista vacía si no quedan [n] libres.
     * Un asiento vendido no puede volver a venderse mientras no se devuelva.
     */
    fun comprar(cliente: String, n: Int): List<Int>

    /** Número de asientos vendidos en este momento. */
    fun vendidas(): Int

    /** Dinero en caja: PRECIO por cada entrada vendida, menos los reembolsos. */
    fun recaudado(): Long
}

/** Reto C: taquilla con devoluciones y lista de espera. */
interface TaquillaConEspera : Taquilla {
    /**
     * [cliente] devuelve su [asiento]: el asiento queda libre y se le reembolsa PRECIO.
     * Devuelve false (y no cambia nada) si ese asiento no está vendido a ese cliente.
     */
    fun devolver(cliente: String, asiento: Int): Boolean

    /**
     * Bloquea al hilo hasta que haya un asiento libre y se lo vende a [cliente].
     * Devuelve el número de asiento, o null si interrumpen al hilo mientras espera.
     */
    fun esperarEntrada(cliente: String): Int?
}
