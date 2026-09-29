package es.dam.psp.ut1.ejemplos;

import es.dam.psp.ut1.Jvm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * E5 · Varios procesos a la vez: secuencial frente a concurrente.
 *
 * Apuntes UT-1, apartado 6.3. Actividad 1.5: cambia n por 8 y por 16.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos. Los hijos son ejemplos/Dormilon.java.
 * Cuatro tareas de 2 s: una detrás de otra tardan ~8 s; a la vez, ~2 s.
 * Los mensajes de los hijos concurrentes salen mezclados porque todos comparten la consola.
 * (En Kotlin se mide con measureTime { }; en Java, con System.nanoTime() antes y después.)
 */
public class E5_Paralelo {

    public static void main(String[] args) throws IOException, InterruptedException {
        int n = 4;                                  // número de hijos (Actividad 1.5: prueba con 8 y 16)

        long inicio = System.nanoTime();
        for (int i = 0; i < n; i++) {
            // lanzar y esperar en la misma vuelta: el siguiente no empieza hasta que acaba este
            Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", "2").inheritIO().start().waitFor();
        }
        long secuencial = System.nanoTime() - inicio;

        inicio = System.nanoTime();
        List<Process> hijos = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            hijos.add(Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", "2").inheritIO().start());
        }
        for (Process hijo : hijos) {
            hijo.waitFor();                         // punto de sincronización: esperar a TODOS
        }
        long concurrente = System.nanoTime() - inicio;

        System.out.printf("Secuencial:  %.3f s%n", secuencial / 1e9);
        System.out.printf("Concurrente: %.3f s%n", concurrente / 1e9);
    }
}
