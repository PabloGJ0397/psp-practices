package es.dam.psp.ut1.ejemplos;

/**
 * Dormilon.java · proceso HIJO de ejemplo: duerme los segundos indicados en args[0] (por defecto 3).
 * Simula una tarea larga que no consume CPU (el proceso está BLOQUEADO mientras duerme).
 *
 * Apuntes UT-1: apartado 4 (Actividad 1.3), 5.2, 6.1 (E4), 6.3 (E5, Actividad 1.5) y Práctica 2.b y 2.d.
 *
 * Cómo ejecutarlo solo: triángulo ▶ junto a main; el argumento se pone en
 * Run > Edit Configurations > Program arguments (p. ej. 120).
 * Desde otro programa: Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", "2").
 * Salida: "[PID] empiezo, voy a tardar N s" y, al acabar, "[PID] termino".
 */
public class Dormilon {

    public static void main(String[] args) throws InterruptedException {
        long segundos = 3;                          // 3 s si no hay argumento o no es un número
        if (args.length > 0) {
            try {
                segundos = Long.parseLong(args[0]);
            } catch (NumberFormatException e) {
                // no es un número: nos quedamos con 3
            }
        }
        long pid = ProcessHandle.current().pid();
        System.out.println("[" + pid + "] empiezo, voy a tardar " + segundos + " s");
        Thread.sleep(segundos * 1000);
        System.out.println("[" + pid + "] termino");
    }
}
