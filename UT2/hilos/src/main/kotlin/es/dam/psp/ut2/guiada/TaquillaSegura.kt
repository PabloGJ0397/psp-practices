package es.dam.psp.ut2.guiada

/**
 * Práctica guiada · Reto B. Versión corregida de TaquillaIA (misma carpeta, guiada/).
 *
 * Apuntes UT-2, apartados 5 (condición de carrera) y 6.1 (synchronized, atómicas y cerrojos).
 * No se ejecuta sola: cambia la línea `fabrica` de Verificador.kt a ::TaquillaSegura y ejecuta
 * Verificador.kt. Debe cumplir el contrato de Taquilla.kt.
 */
class TaquillaSegura(override val aforo: Int) : Taquilla {

    override fun comprar(cliente: String, n: Int): List<Int> = TODO("Reto B")

    override fun vendidas(): Int = TODO("Reto B")

    override fun recaudado(): Long = TODO("Reto B")
}
