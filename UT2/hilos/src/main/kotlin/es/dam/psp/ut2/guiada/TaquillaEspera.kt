package es.dam.psp.ut2.guiada

/**
 * Práctica guiada · Reto C. Taquilla con devoluciones y lista de espera.
 *
 * Apuntes UT-2, apartados 2 (interrupt), 6.1 (synchronized) y 7.1 (wait() y notifyAll()).
 * No se ejecuta sola: la prueba VerificadorEspera.kt (misma carpeta, guiada/).
 * Debe cumplir el contrato de TaquillaConEspera (Taquilla.kt).
 */
class TaquillaEspera(override val aforo: Int) : TaquillaConEspera {

    override fun comprar(cliente: String, n: Int): List<Int> = TODO("Reto C")

    override fun vendidas(): Int = TODO("Reto C")

    override fun recaudado(): Long = TODO("Reto C")

    override fun devolver(cliente: String, asiento: Int): Boolean = TODO("Reto C")

    override fun esperarEntrada(cliente: String): Int? = TODO("Reto C")
}
