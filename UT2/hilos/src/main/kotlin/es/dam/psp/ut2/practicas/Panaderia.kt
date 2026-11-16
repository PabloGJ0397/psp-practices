package es.dam.psp.ut2.practicas

import kotlin.concurrent.thread

/**
 * Práctica 3 · La panadería (productor-consumidor). Código de partida: completa los TODO
 * (sus letras son las de los apartados del enunciado).
 *
 * Apuntes UT-2, apartados 7.1 (wait / notifyAll) y 7.2 (colas bloqueantes y píldora venenosa).
 * Ejecutar: ▶ junto a `main`. Hace la simulación dos veces, una con cada Mostrador; al final,
 * entre los tres clientes deben sumar 24 barras en cada una.
 *
 * 2 hornos (productores) hacen 12 barras cada uno y las dejan en un mostrador de 5 huecos.
 * 3 clientes (consumidores) compran barras hasta que se agotan las 24.
 */

data class Barra(val horno: String, val numero: Int)

interface Mostrador {
    /** Deja una barra; si el mostrador está lleno, espera. */
    fun poner(barra: Barra)
    /** Coge una barra; si está vacío, espera. */
    fun sacar(): Barra
}

/** TODO a: implementación con synchronized + wait() / notifyAll() sobre una ArrayDeque. */
class MostradorMonitor(private val capacidad: Int) : Mostrador {
    override fun poner(barra: Barra) {
        TODO("Práctica 3.a")
    }

    override fun sacar(): Barra {
        TODO("Práctica 3.a")
    }
}

/** TODO b: la misma clase usando una ArrayBlockingQueue (sin synchronized ni wait). */
class MostradorCola(capacidad: Int) : Mostrador {
    override fun poner(barra: Barra) {
        TODO("Práctica 3.b")
    }

    override fun sacar(): Barra {
        TODO("Práctica 3.b")
    }
}

const val BARRAS_POR_HORNO = 12
const val HORNOS = 2
const val CLIENTES = 3

/** Barra especial que avisa a un cliente de que no quedan más (técnica de la "píldora venenosa"). */
val CERRADO = Barra("-", -1)

fun simular(mostrador: Mostrador) {
    val hornos = List(HORNOS) { h ->
        thread(name = "horno-$h") {
            for (n in 1..BARRAS_POR_HORNO) {
                Thread.sleep(40)
                mostrador.poner(Barra("horno-$h", n))
            }
        }
    }
    val clientes = List(CLIENTES) { c ->
        thread(name = "cliente-$c") {
            var compradas = 0
            // TODO c: sacar barras hasta recibir CERRADO; contar las compradas y dormir 100 ms por barra.
            println("cliente-$c ha comprado $compradas barras")
        }
    }
    hornos.forEach { it.join() }
    // TODO d: cuando los hornos terminan, poner una barra CERRADO por cada cliente.
    clientes.forEach { it.join() }
}

fun main() {
    println("== Con monitor (wait / notifyAll) ==")
    simular(MostradorMonitor(5))
    println("== Con ArrayBlockingQueue ==")
    simular(MostradorCola(5))
}
