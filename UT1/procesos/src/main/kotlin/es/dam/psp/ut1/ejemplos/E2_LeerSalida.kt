package es.dam.psp.ut1.ejemplos

import es.dam.psp.ut1.Jvm

/**
 * E2 · Capturar (leer) la salida de un proceso hijo.
 *
 * Apuntes UT-1, apartado 5.3. Se usa en la Actividad 1.4.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Lanza "java -version" con el mismo java que ejecuta este programa (Jvm.java) y muestra
 * cada línea numerada. Para el padre, la salida estándar del hijo es un InputStream
 * (algo que él lee).
 */
fun main() {
    val pb = ProcessBuilder(Jvm.java, "-version")   // Jvm.java: ruta del java que nos ejecuta
        .redirectErrorStream(true)          // mezcla stderr con stdout (java -version escribe en stderr)

    val proceso = pb.start()

    // Leer TODA la salida antes de waitFor(): si el hijo escribe mucho y nadie lee,
    // el buffer de la tubería se llena, el hijo se bloquea y el padre espera para siempre.
    val lineas = proceso.inputStream.bufferedReader().readLines()
    val codigo = proceso.waitFor()

    lineas.forEachIndexed { i, linea -> println("%2d | %s".format(i + 1, linea)) }
    println("Líneas leídas: ${lineas.size} · código de salida: $codigo")
}
