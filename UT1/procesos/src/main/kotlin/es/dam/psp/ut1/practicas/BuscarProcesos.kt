package es.dam.psp.ut1.practicas

/**
 * E6 · Un "ps" casero con ProcessHandle: información de los procesos del sistema.
 *
 * Apuntes UT-1, apartado 6.4. Base de la Práctica 2.c (practicas/BuscarProcesos.kt).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Muestra el PID propio y el del padre, y una tabla PID, PPID, USUARIO y COMANDO con los
 * 15 primeros procesos visibles. Solo verás los datos que el sistema operativo permite leer
 * a tu usuario: cada dato de info() es un Optional que puede estar vacío.
 */
fun main(args: Array<String>) {
    val texto = args.firstOrNull()
    if (texto == null) {
        println("Uso: BuscarProcesos <texto a buscar en el comando> Ejemplo: BuscarProcesos java")
        return
    }
    val yo = ProcessHandle.current()
    println("Soy el PID ${yo.pid()}, mi padre es ${yo.parent().map { it.pid() }.orElse(-1)}")
    println("%-8s %-8s %-12s %-27s %-15s %s".format("PID", "PPID", "USUARIO", "INICIO", "CPU", "COMANDO"))

    ProcessHandle.allProcesses()
        .filter {
            it.info().command().isPresent && it.info().command().get().contains(texto, ignoreCase = true)
        }   // solo los procesos cuyo comando contiene el texto buscado (argumento del programa)

        .forEach { p ->
            val info = p.info()
            println(
                "%-8d %-8s %-12s %-27s %-15s %s".format(
                    p.pid(),
                    p.parent().map { it.pid().toString() }.orElse("-"),   // "-" si no tiene padre visible
                    info.user().orElse("?").takeLast(12),                  // "?" si el SO no nos deja leerlo
                    info.startInstant().map { it.toString() }.orElse("-"),        //fecha y hora de arranque
                    info.totalCpuDuration().map { it.toString() }.orElse("-"),   //tiempo de CPU
                    info.command().get()
                        .takeLast(60)                          // final de la ruta: es lo que identifica al programa
                )
            )
        }
}
