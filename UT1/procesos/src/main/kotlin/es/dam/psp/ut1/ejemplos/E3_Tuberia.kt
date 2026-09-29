package es.dam.psp.ut1.ejemplos

import es.dam.psp.ut1.Jvm

/**
 * E3 · Comunicación padre ↔ hijo con tuberías (pipes).
 *
 * Apuntes UT-1, apartado 6. El proceso hijo es ejemplos/Mayusculas.kt (clase MayusculasKt).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos (el hijo lo lanza este programa).
 * - Lo que el padre escribe en proceso.outputStream le llega al hijo por su entrada estándar.
 * - Lo que el hijo escribe en su salida estándar el padre lo lee en proceso.inputStream.
 * Salida esperada: las tres líneas en mayúsculas precedidas de "Hijo: ", el mensaje del hijo
 * por la salida de error y "Código de salida (nº de líneas): 3".
 */
fun main() {
    val hijo = Jvm.proceso("es.dam.psp.ut1.ejemplos.MayusculasKt")
        .redirectError(ProcessBuilder.Redirect.INHERIT)  // los mensajes de error del hijo, a nuestra consola
        .start()

    // 1. Enviar datos al hijo y cerrar su entrada (el hijo verá fin de fichero)
    hijo.outputStream.bufferedWriter().use { w ->
        listOf("hola", "programación de servicios", "y procesos").forEach { w.write(it); w.newLine() }
    }

    // 2. Leer su respuesta
    hijo.inputStream.bufferedReader().forEachLine { println("Hijo: $it") }

    // 3. Sincronizar: esperar a que termine y recoger su resultado
    println("Código de salida (nº de líneas): ${hijo.waitFor()}")
}
