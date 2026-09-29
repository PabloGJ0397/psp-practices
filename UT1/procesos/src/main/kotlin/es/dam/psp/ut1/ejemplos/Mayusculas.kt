package es.dam.psp.ut1.ejemplos

/**
 * Mayusculas.kt · proceso HIJO de E3: lee líneas de su entrada estándar hasta el final (EOF)
 * y escribe cada una en mayúsculas. Devuelve como código de salida el número de líneas.
 *
 * Apuntes UT-1, apartado 6 (ejemplos/E3_Tuberia.kt lo lanza con Jvm.proceso).
 * Puedes probarlo solo: triángulo ▶ junto a main, escribe varias líneas en la consola y
 * pulsa Ctrl+D (Ctrl+Z e Intro en Windows) para enviar el fin de fichero.
 */
fun main() {
    var n = 0
    generateSequence(::readLine).forEach { linea ->   // readLine() devuelve null en el fin de fichero
        println(linea.uppercase())
        n++
    }
    // Por la salida de error, para no mezclarlo con los datos que lee el padre
    System.err.println("[hijo ${ProcessHandle.current().pid()}] procesadas $n líneas")
    kotlin.system.exitProcess(n)   // el código de salida transmite un resultado al padre
}
