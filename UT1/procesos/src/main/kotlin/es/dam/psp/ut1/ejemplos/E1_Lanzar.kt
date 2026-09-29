package es.dam.psp.ut1.ejemplos

import es.dam.psp.ut1.Jvm
import java.io.File

/**
 * E1 · Lanzar un proceso y esperar a que termine.
 *
 * Apuntes UT-1, apartado 5.1. Se modifica en la Actividad 1.4.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Lista la carpeta personal (ls -l en Linux/macOS, dir en Windows). El hijo hereda la
 * consola del padre (inheritIO), así que su salida aparece aquí; después se muestra su PID
 * y su código de salida (0 si todo fue bien).
 */
fun main() {
    val comando = if (Jvm.esWindows) listOf("cmd", "/c", "dir") else listOf("ls", "-l")

    val pb = ProcessBuilder(comando)
        .directory(File(System.getProperty("user.home")))   // directorio de trabajo del hijo
        .inheritIO()                                        // misma entrada/salida que el padre

    val proceso = pb.start()
    println(">> Lanzado ${comando.joinToString(" ")} con PID ${proceso.pid()}")

    val codigo = proceso.waitFor()          // el padre se bloquea hasta que el hijo acaba
    println(">> El hijo terminó con código de salida $codigo")
}
