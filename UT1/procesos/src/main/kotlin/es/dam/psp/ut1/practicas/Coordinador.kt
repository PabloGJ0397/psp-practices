package es.dam.psp.ut1.practicas

import es.dam.psp.ut1.Jvm
import java.io.File
import kotlin.time.measureTime

/**
 * Coordinador.kt · Práctica 4 · proceso PADRE. Completa los TODO 4.a … 4.e (letras de la práctica).
 *
 * Apuntes UT-1, apartados 5.2 (Jvm.proceso), 5.1 (redirectInput), 6 (leer antes de esperar)
 * y 6.3 (lanzar todos y después esperar a todos).
 * Antes: ejecuta practicas/GeneradorDatos.kt para crear datos/ en la raíz del proyecto.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos. Cada hijo es practicas/Contador.kt.
 *
 * Reparte los ficheros de datos/ entre procesos hijo Contador, recoge sus resultados
 * y muestra el total. Compara el tiempo en modo secuencial y en modo concurrente.
 * Mejora opcional: añade concurrenteLimitado(ficheros, maximo) y mídelo con 1, 2, 4 y 8.
 */

/** Resultado que devuelve un hijo para un fichero. */
data class Resultado(
    val fichero: String,
    val lineas: Long,
    val palabras: Long,
    val caracteres: Long,
    val masFrecuente: String
)

const val CLASE_HIJO = "es.dam.psp.ut1.practicas.ContadorKt"   // main de Contador.kt

/**
 * Crea y ARRANCA un proceso Contador cuya entrada estándar sea [fichero].
 * Pista: Jvm.proceso(...) y redirectInput(...). Hereda la salida de error del hijo.
 */
fun lanzarContador(fichero: File): Process {
    val procesoHijo =
        Jvm.proceso(CLASE_HIJO).redirectInput(fichero).redirectError(ProcessBuilder.Redirect.INHERIT).start()
    return procesoHijo
}

/**
 * Lee la línea que ha escrito el hijo, espera a que termine y construye el Resultado.
 * Si el código de salida no es 0, lanza una IllegalStateException con un mensaje claro.
 */
fun recogerResultado(fichero: File, hijo: Process): Resultado {
    val linea = hijo.inputStream.bufferedReader().readLine()
    val codigo = hijo.waitFor()
    if (codigo != 0) {
        throw IllegalStateException("El hijo falló con el fichero ${fichero.name}, código $codigo")
    }
    val partes = linea.split(";")
    val resultado = Resultado(fichero.name, partes[0].toLong(), partes[1].toLong(), partes[2].toLong(), partes[3])
    return resultado
}

/** Procesa los ficheros uno detrás de otro: lanzar, esperar, lanzar, esperar... */
fun secuencial(ficheros: List<File>): List<Resultado> {
    val resultados = mutableListOf<Resultado>()
    ficheros.forEach { fichero ->
        val procesoEnCurso = lanzarContador(fichero)
        val resultado = recogerResultado(fichero, procesoEnCurso)
        resultados.add(resultado)
    }
    return resultados
}

/** Lanza TODOS los hijos a la vez y después recoge los resultados. */
fun concurrente(ficheros: List<File>): List<Resultado> {
    val procesos = ficheros.map { fichero -> lanzarContador(fichero) }
    return ficheros.mapIndexed { posicion, fichero -> recogerResultado(fichero, procesos[posicion]) }
}

fun main() {
    // Ruta relativa: datos/ dentro del directorio de trabajo (la raíz del proyecto en IntelliJ)
    val ficheros = File("datos").listFiles { f -> f.extension == "txt" }?.sortedBy { it.name }
    if (ficheros.isNullOrEmpty()) {
        println("No hay ficheros en datos/. Ejecuta antes GeneradorDatos.kt")
        return
    }
    println("Procesadores disponibles: ${Runtime.getRuntime().availableProcessors()}")

    lateinit var r1: List<Resultado>           // se asignan dentro de measureTime { }
    lateinit var r2: List<Resultado>
    val t1 = measureTime { r1 = secuencial(ficheros) }
    val t2 = measureTime { r2 = concurrente(ficheros) }

    println("%-12s %10s %10s %12s  %s".format("FICHERO", "LINEAS", "PALABRAS", "CARACTERES", "MAS FRECUENTE"))

    r1.forEach { r ->
        println(
            "%-12s %10s %10s %12s  %s".format(
                r.fichero,
                r.lineas,
                r.palabras,
                r.caracteres,
                r.masFrecuente
            )
        )
    }
    val totalLineas = r1.sumOf { it.lineas }
    val totalPalabras = r1.sumOf { it.palabras }
    val totalCaracteres = r1.sumOf { it.caracteres }

    println("-".repeat(50))
    println("%-12s %10s %10s %12s  %s".format("TOTAL", totalLineas, totalPalabras, totalCaracteres, ""))
    println("¿Resultados iguales? ${r1 == r2}")

    println("Secuencial:  $t1")
    println("Concurrente: $t2")

    // Práctica 4.f: ejecútalo tres veces y calcula la aceleración t1 / t2
}
