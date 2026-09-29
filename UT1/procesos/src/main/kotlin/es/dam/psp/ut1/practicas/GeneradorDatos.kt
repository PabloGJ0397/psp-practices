package es.dam.psp.ut1.practicas

import java.io.File
import kotlin.random.Random

/**
 * GeneradorDatos.kt · Práctica 4 · genera los datos de entrada. Ejecútalo UNA vez antes de Coordinador.kt.
 *
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Crea la carpeta datos/ en el directorio de trabajo (la raíz del proyecto procesos cuando se
 * ejecuta desde IntelliJ) con 8 ficheros de texto, unos 30 MB en total. La carpeta está en
 * .gitignore: no se sube al repositorio.
 * Siempre genera los mismos ficheros (semilla fija), así todos obtenéis los mismos totales.
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
    val rnd = Random(2627)                       // semilla fija: mismos datos en todos los equipos

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
