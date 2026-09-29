package es.dam.psp.ut1.ejemplos;

/**
 * E6 · Un "ps" casero con ProcessHandle: información de los procesos del sistema.
 *
 * Apuntes UT-1, apartado 6.4. Base de la Práctica 2.c (practicas/BuscarProcesos.java).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Muestra el PID propio y el del padre, y una tabla PID, PPID, USUARIO y COMANDO con los
 * 15 primeros procesos visibles. Solo verás los datos que el sistema operativo permite leer
 * a tu usuario: cada dato de info() es un Optional que puede estar vacío.
 */
public class E6_MiniPs {

    public static void main(String[] args) {
        ProcessHandle yo = ProcessHandle.current();
        System.out.println("Soy el PID " + yo.pid() + ", mi padre es "
                + yo.parent().map(ProcessHandle::pid).orElse(-1L));
        System.out.printf("%-8s %-8s %-12s %s%n", "PID", "PPID", "USUARIO", "COMANDO");

        ProcessHandle.allProcesses()
                .filter(p -> p.info().command().isPresent())   // solo los procesos cuyo comando podemos leer
                .limit(15)                                      // los 15 primeros, para no llenar la consola
                .forEach(p -> {
                    ProcessHandle.Info info = p.info();
                    System.out.printf("%-8d %-8s %-12s %s%n",
                            p.pid(),
                            p.parent().map(padre -> String.valueOf(padre.pid())).orElse("-"),  // "-" si no tiene padre visible
                            ultimos(info.user().orElse("?"), 12),                              // "?" si el SO no nos deja leerlo
                            ultimos(info.command().get(), 60));  // final de la ruta: es lo que identifica al programa
                });
    }

    /** Los n últimos caracteres de s (o s entera si es más corta). Equivale a takeLast(n) de Kotlin. */
    static String ultimos(String s, int n) {
        return s.length() <= n ? s : s.substring(s.length() - n);
    }
}
