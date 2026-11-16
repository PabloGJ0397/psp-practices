package es.dam.psp.ut2.practicas

import kotlin.concurrent.thread
import kotlin.random.Random
import kotlin.time.measureTime

/**
 * Práctica 2 · Banco concurrente. Código de partida: sigue los TODO (letras del enunciado).
 *
 * Apuntes UT-2, apartados 5 (condición de carrera), 6.1 (synchronized y ReentrantLock) y
 * 6.2 (interbloqueo y volcado de hilos).
 * Ejecutar: ▶ junto a `main`. Muestra el total inicial, el final y el tiempo. Si haces una
 * función por versión, elige cuál usar con un argumento del programa
 * (Run > Edit Configurations > Program arguments) y léelo en `main(args: Array<String>)`.
 *
 * 10 cuentas con 1000 € cada una. 8 hilos hacen transferencias aleatorias entre ellas.
 * El dinero total (10 000 €) debe mantenerse siempre: las transferencias solo lo mueven.
 * Sigue los apartados del enunciado: este código tiene un defecto de concurrencia a propósito.
 */

// TODO d: añade a cada cuenta su propio ReentrantLock (val lock = ReentrantLock()).
class Cuenta(val id: Int, var saldo: Long)

class Banco(nCuentas: Int, saldoInicial: Long) {
    val cuentas = List(nCuentas) { Cuenta(it, saldoInicial) }

    /**
     * Mueve [cantidad] de [origen] a [destino] si hay saldo suficiente. SIN PROTEGER.
     * TODO b: versión que bloquea origen y dentro destino (synchronized anidados).
     * TODO c: versión que bloquea siempre primero la cuenta de menor id.
     * TODO d: versión con lock() del origen y tryLock() del destino; si falla, suelta y reintenta.
     */
    fun transferir(origen: Cuenta, destino: Cuenta, cantidad: Long) {
        if (origen.saldo >= cantidad) {
            origen.saldo -= cantidad
            destino.saldo += cantidad
        }
    }

    fun total(): Long = cuentas.sumOf { it.saldo }
}

const val HILOS_BANCO = 8
const val OPERACIONES = 200_000

fun main() {
    // TODO a: ejecuta tres veces esta versión sin proteger y anota el total final.
    val banco = Banco(10, 1000)
    println("Total inicial: ${banco.total()} €")

    val tiempo = measureTime {
        List(HILOS_BANCO) {
            thread {
                repeat(OPERACIONES) {
                    val o = banco.cuentas.random()
                    var d = banco.cuentas.random()
                    while (d === o) d = banco.cuentas.random()   // origen y destino distintos
                    banco.transferir(o, d, Random.nextLong(1, 50))
                }
            }
        }.forEach { it.join() }
    }

    // TODO e: mide así el tiempo de cada versión que termina y compáralos en una tabla.
    println("Total final:   ${banco.total()} €   (tiempo: $tiempo)")
}
