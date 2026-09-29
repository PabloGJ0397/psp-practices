package es.dam.psp.ut1.ejemplos;

import es.dam.psp.ut1.Jvm;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * E4 · Esperar con límite de tiempo y terminar un proceso.
 *
 * Apuntes UT-1, apartado 6.1. El hijo es ejemplos/Dormilon.java con 10 s.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * waitFor(tiempo, unidad) devuelve true si el hijo acabó a tiempo. Como tarda 10 s y solo
 * esperamos 2, el padre lo termina. El aviso de onExit() muestra el código 143 en
 * Linux/macOS (128 + SIGTERM 15) y 1 en Windows.
 */
public class E4_Timeout {

    public static void main(String[] args) throws IOException, InterruptedException {
        Process hijo = Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", "10").inheritIO().start();

        // onExit() avisa (de forma asíncrona) cuando el proceso termina, sea como sea
        hijo.onExit().thenAccept(p ->
                System.out.println(">> Aviso: el proceso " + p.pid() + " ha terminado (código " + p.exitValue() + ")"));

        if (!hijo.waitFor(2, TimeUnit.SECONDS)) {
            System.out.println(">> Demasiado lento: lo termino");
            hijo.destroy();                                 // petición amable (SIGTERM en Linux/macOS)
            if (!hijo.waitFor(1, TimeUnit.SECONDS)) {
                hijo.destroyForcibly();                     // a la fuerza (SIGKILL)
            }
        }
        System.out.println(">> ¿Sigue vivo? " + hijo.isAlive());
        Thread.sleep(200);                                  // da tiempo a que se imprima el aviso de onExit
    }
}
