package es.dam.psp.ut1.ejemplos

/**
 * E6 · Un "ps" casero con ProcessHandle: información de los procesos del sistema.
 *
 * Apuntes UT-1, apartado 6.4. Base de la Práctica 2.c (practicas/BuscarProcesos.kt).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Muestra el PID propio y el del padre, y una tabla PID, PPID, USUARIO y COMANDO con los
 * 15 primeros procesos visibles. Solo verás los datos que el sistema operativo permite leer
 * a tu usuario: cada dato de info() es un Optional que puede estar vacío.
 */
fun main() {
    val yo = ProcessHandle.current()
    println("Soy el PID ${yo.pid()}, mi padre es ${yo.parent().map { it.pid() }.orElse(-1)}")
    println("%-8s %-8s %-12s %s".format("PID", "PPID", "USUARIO", "COMANDO"))

    ProcessHandle.allProcesses()
        .filter { it.info().command().isPresent }   // solo los procesos cuyo comando podemos leer
        .limit(15)                                   // los 15 primeros, para no llenar la consola
        .forEach { p ->
            val info = p.info()
            println(
                "%-8d %-8s %-12s %s".format(
                    p.pid(),
                    p.parent().map { it.pid().toString() }.orElse("-"),   // "-" si no tiene padre visible
                    info.user().orElse("?").takeLast(12),                  // "?" si el SO no nos deja leerlo
                    info.command().get().takeLast(60)   // final de la ruta: es lo que identifica al programa
                )
            )
        }
}
