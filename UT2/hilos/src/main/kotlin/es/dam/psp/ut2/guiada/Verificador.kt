package es.dam.psp.ut2.guiada

import java.util.concurrent.CountDownLatch
import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.measureTime

/**
 * Práctica guiada · Verificador de taquillas (Retos A y B).
 *
 * Apuntes UT-2, apartados 2 (thread y join), 5 (condición de carrera) y 7.2 (CountDownLatch).
 * Ejecutar: ▶ junto a `main`. Repite RONDAS veces la misma prueba: CLIENTES hilos salen a la vez
 * (CountDownLatch) y cada uno intenta INTENTOS compras de 1 a 4 entradas. Después comprueba
 * cinco reglas del contrato (Taquilla.kt) y cuenta en cuántas rondas se ha roto cada una.
 *
 * Para probar otra taquilla, cambia solo la línea de `fabrica` (Reto B: ::TaquillaSegura).
 * NO MODIFIQUES NADA MÁS (ni tú ni la IA): un verificador que se ajusta al código no verifica nada.
 */
val fabrica: (Int) -> Taquilla = ::TaquillaIA

const val AFORO = 200
const val CLIENTES = 16
const val INTENTOS = 40
const val RONDAS = 30

/** Lo que un cliente pidió y lo que recibió en una compra. */
data class Compra(val cliente: String, val pedidas: Int, val asientos: List<Int>)

/** Ejecuta una ronda y devuelve la taquilla usada y todas las compras de todos los clientes. */
fun ronda(): Pair<Taquilla, List<Compra>> {
    val taquilla = fabrica(AFORO)
    val salida = CountDownLatch(1)
    // Cada hilo apunta sus compras en su propia lista: el verificador no comparte nada entre hilos.
    val porCliente = List(CLIENTES) { mutableListOf<Compra>() }

    val hilos = List(CLIENTES) { c ->
        thread(name = "cliente-$c") {
            salida.await()                                       // todos salen a la vez
            repeat(INTENTOS) {
                val n = Random.nextInt(1, 5)
                porCliente[c] += Compra("cliente-$c", n, taquilla.comprar("cliente-$c", n))
            }
        }
    }
    salida.countDown()
    hilos.forEach { it.join() }
    return taquilla to porCliente.flatten()
}

/** Comprueba las reglas del contrato. Devuelve el nombre de cada regla que se ha roto. */
fun reglasRotas(taquilla: Taquilla, compras: List<Compra>): List<String> {
    val rotas = mutableListOf<String>()
    val entregados = compras.flatMap { it.asientos }

    if (compras.any { it.asientos.isNotEmpty() && it.asientos.size != it.pedidas })
        rotas += "R1 todo o nada"
    if (entregados.size != entregados.toSet().size)
        rotas += "R2 asiento vendido dos veces"
    if (entregados.size > taquilla.aforo || entregados.any { it !in 0 until taquilla.aforo })
        rotas += "R3 sobreventa"
    if (taquilla.vendidas() != entregados.size)
        rotas += "R4 vendidas() no cuadra"
    if (taquilla.recaudado() != entregados.size * PRECIO)
        rotas += "R5 recaudado() no cuadra"
    return rotas
}

fun main() {
    val fallos = mutableMapOf<String, Int>()
    var ejemplo = ""

    val tiempo = measureTime {
        repeat(RONDAS) { r ->
            val (taquilla, compras) = ronda()
            val rotas = reglasRotas(taquilla, compras)
            rotas.forEach { fallos.merge(it, 1, Int::plus) }
            if (rotas.isNotEmpty() && ejemplo.isEmpty()) {
                val entregados = compras.sumOf { it.asientos.size }
                ejemplo = "Ronda ${r + 1}: entregados $entregados asientos, vendidas()=${taquilla.vendidas()}, " +
                    "recaudado()=${taquilla.recaudado()} € (debería ser ${entregados * PRECIO} €)"
            }
        }
    }

    val nombre = fabrica(1)::class.simpleName
    println("Taquilla: $nombre · $RONDAS rondas · $CLIENTES hilos · aforo $AFORO · tiempo $tiempo")
    listOf("R1 todo o nada", "R2 asiento vendido dos veces", "R3 sobreventa",
        "R4 vendidas() no cuadra", "R5 recaudado() no cuadra").forEach { regla ->
        val n = fallos[regla] ?: 0
        println("  %-30s %s".format(regla, if (n == 0) "OK" else "ROTA en $n/$RONDAS rondas"))
    }
    if (ejemplo.isNotEmpty()) println("  Primer fallo · $ejemplo")
    println(if (fallos.isEmpty()) "VEREDICTO: ninguna regla rota en $RONDAS rondas (no demuestra que sea correcta)"
            else "VEREDICTO: INCORRECTA")
}
