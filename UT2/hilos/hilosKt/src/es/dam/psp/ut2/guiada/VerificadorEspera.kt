package es.dam.psp.ut2.guiada

import kotlin.concurrent.thread

/**
 * Práctica guiada · Verificador de la lista de espera (Reto C).
 *
 * Apuntes UT-2, apartados 2 (interrupt y join), 3 (estados) y 7.1 (wait() y notifyAll()).
 * Ejecutar: ▶ junto a `main`. En cada ronda se agota el aforo, ESPERAN hilos se ponen a esperar
 * (esperarEntrada), DEVUELVEN dueños devuelven su entrada a la vez y, al final, se interrumpe a
 * los que siguen esperando. Comprueba seis reglas y cuenta en cuántas rondas se rompe cada una.
 *
 * Para probar otra taquilla, cambia solo la línea de `fabricaEspera`.
 * NO MODIFIQUES NADA MÁS (ni tú ni la IA).
 */
val fabricaEspera: (Int) -> TaquillaConEspera = ::TaquillaEspera

const val AFORO_ESPERA = 20
const val ESPERAN = 10
const val DEVUELVEN = 6
const val RONDAS_ESPERA = 5

val REGLAS_ESPERA = listOf(
    "E1 esperan en WAITING (sin espera activa)",
    "E2 devolver() solo vale al dueño",
    "E3 una devolución = un cliente servido",
    "E4 sin asientos repetidos",
    "E5 interrupt() libera a los demás",
    "E6 vendidas() y recaudado() cuadran",
)

/** Ejecuta una ronda completa y devuelve las reglas rotas (vacía si todo va bien). */
fun rondaEspera(): Set<String> {
    val rotas = mutableSetOf<String>()
    val taquilla = fabricaEspera(AFORO_ESPERA)

    // 1. Se vende todo el aforo, una entrada por fan (desde main, sin concurrencia).
    val duenoDe = mutableMapOf<Int, String>()
    repeat(AFORO_ESPERA) { i ->
        taquilla.comprar("fan-$i", 1).forEach { duenoDe[it] = "fan-$i" }
    }
    if (duenoDe.size != AFORO_ESPERA || taquilla.comprar("tarde", 1).isNotEmpty())
        return setOf("E6 vendidas() y recaudado() cuadran")      // ni siquiera vende bien el aforo

    // 2. ESPERAN clientes se ponen a la cola. Cada hilo escribe solo en su posición del array.
    val conseguido = arrayOfNulls<Int>(ESPERAN)
    val esperando = List(ESPERAN) { i ->
        thread(name = "espera-$i") { conseguido[i] = taquilla.esperarEntrada("espera-$i") }
    }
    Thread.sleep(300)
    if (esperando.any { it.state != Thread.State.WAITING }) rotas += REGLAS_ESPERA[0]

    // 3. Un intruso intenta devolver un asiento que no es suyo: debe fallar.
    val asientosDevueltos = duenoDe.keys.sorted().take(DEVUELVEN)
    if (taquilla.devolver("intruso", asientosDevueltos.first())) rotas += REGLAS_ESPERA[1]

    // 4. DEVUELVEN dueños devuelven su entrada a la vez.
    val okDevolucion = BooleanArray(DEVUELVEN)
    asientosDevueltos.mapIndexed { i, asiento ->
        thread { okDevolucion[i] = taquilla.devolver(duenoDe.getValue(asiento), asiento) }
    }.forEach { it.join() }
    if (!okDevolucion.all { it }) rotas += REGLAS_ESPERA[1]

    // 5. Se da tiempo a que se sirva a los que esperan y se interrumpe a los demás.
    Thread.sleep(500)
    esperando.forEach { it.interrupt() }
    esperando.forEach { it.join(1000) }
    if (esperando.any { it.isAlive }) {
        rotas += REGLAS_ESPERA[4]
        return rotas                                             // hay hilos colgados: no se puede seguir
    }

    // 6. Comprobaciones finales.
    val servidos = conseguido.filterNotNull()
    if (servidos.size != DEVUELVEN) rotas += REGLAS_ESPERA[2]
    if (servidos.size != servidos.toSet().size || !asientosDevueltos.containsAll(servidos))
        rotas += REGLAS_ESPERA[3]
    if (servidos.isNotEmpty() && taquilla.devolver(duenoDe.getValue(servidos.first()), servidos.first()))
        rotas += REGLAS_ESPERA[1]                                // el antiguo dueño ya no lo es
    if (taquilla.vendidas() != AFORO_ESPERA - DEVUELVEN + servidos.size ||
        taquilla.recaudado() != taquilla.vendidas() * PRECIO) rotas += REGLAS_ESPERA[5]
    return rotas
}

fun main() {
    val fallos = mutableMapOf<String, Int>()
    repeat(RONDAS_ESPERA) { rondaEspera().forEach { fallos.merge(it, 1, Int::plus) } }

    println("Taquilla: ${fabricaEspera(1)::class.simpleName} · $RONDAS_ESPERA rondas · " +
        "$ESPERAN esperan · $DEVUELVEN devuelven")
    REGLAS_ESPERA.forEach { regla ->
        val n = fallos[regla] ?: 0
        println("  %-42s %s".format(regla, if (n == 0) "OK" else "ROTA en $n/$RONDAS_ESPERA rondas"))
    }
    println(if (fallos.isEmpty()) "VEREDICTO: ninguna regla rota (no demuestra que sea correcta)"
            else "VEREDICTO: INCORRECTA")
}
