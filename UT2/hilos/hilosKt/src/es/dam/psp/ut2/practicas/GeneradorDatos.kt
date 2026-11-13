package es.dam.psp.ut2.practicas

import java.io.File
import kotlin.random.Random

/**
 * Genera la carpeta datos/ con 8 ficheros de texto para la Práctica 4 (la misma que en la UT-1).
 * Siempre genera los mismos ficheros (semilla fija), así todos obtenéis los mismos totales.
 *
 * Práctica 4 (paso previo). Ejecutar: ▶ junto a `main`, una sola vez. Crea datos/texto01.txt …
 * texto08.txt en la raíz del proyecto hilos (el directorio de trabajo de IntelliJ).
 */
fun main() {
    val vocabulario = """
        proceso hilo servicio socket servidor cliente puerto tubería señal memoria
        planificador cola cpu núcleo estado listo bloqueado ejecución terminado nuevo
        sincronizar esperar compartir recurso prioridad contexto pila montón sistema operativo
        kotlin android java jvm red protocolo cifrado clave seguro dato fichero archivo
        el la los las un una de del y o que en con por para como más muy también
    """.trim().split(Regex("\\s+"))

    val carpeta = File("datos").apply { mkdirs() }
    val rnd = Random(2627)                                   // semilla fija: mismos ficheros siempre

    for (f in 1..8) {
        val fichero = File(carpeta, "texto%02d.txt".format(f))
        fichero.bufferedWriter().use { w ->
            repeat(40_000 + f * 5_000) {                      // líneas por fichero
                val palabras = 4 + rnd.nextInt(12)
                w.write(List(palabras) { vocabulario[rnd.nextInt(vocabulario.size)] }.joinToString(" "))
                w.newLine()
            }
        }
        println("Creado ${fichero.path} (${fichero.length() / 1024} KB)")
    }
}
