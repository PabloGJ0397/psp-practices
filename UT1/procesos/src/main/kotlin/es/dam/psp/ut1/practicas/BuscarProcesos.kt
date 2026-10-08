package es.dam.psp.ut1.practicas

/**
 * BuscarProcesos · Práctica 2.c (UT-1, Procesos y servicios).
 *
 * Busca en el sistema los procesos cuyo comando contiene el texto indicado
 * (sin distinguir mayúsculas de minúsculas) y los muestra en una tabla.
 *
 * Cómo ejecutarlo: Run > Edit Configurations > Program arguments y escribir el texto
 * a buscar (p. ej. java). Sin argumento, el programa muestra cómo se usa y termina.
 *
 * Muestra, por cada proceso encontrado: PID, PPID, usuario, hora de inicio (en UTC),
 * tiempo total de CPU consumido y comando. Se usa la API ProcessHandle; cada dato de
 * info() es un Optional que puede estar vacío si el sistema no permite leerlo.
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
