package es.dam.psp.ut1.ejemplos;

import es.dam.psp.ut1.Jvm;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * E1 · Lanzar un proceso y esperar a que termine.
 *
 * Apuntes UT-1, apartado 5.1. Se modifica en la Actividad 1.4.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Lista la carpeta personal (ls -l en Linux/macOS, dir en Windows). El hijo hereda la
 * consola del padre (inheritIO), así que su salida aparece aquí; después se muestra su PID
 * y su código de salida (0 si todo fue bien).
 */
public class E1_Lanzar {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> comando = Jvm.ES_WINDOWS ? List.of("cmd", "/c", "dir") : List.of("ls", "-l");

        ProcessBuilder pb = new ProcessBuilder(comando)
                .directory(new File(System.getProperty("user.home")))   // directorio de trabajo del hijo
                .inheritIO();                                           // misma entrada/salida que el padre

        Process proceso = pb.start();
        System.out.println(">> Lanzado " + String.join(" ", comando) + " con PID " + proceso.pid());

        int codigo = proceso.waitFor();         // el padre se bloquea hasta que el hijo acaba
        System.out.println(">> El hijo terminó con código de salida " + codigo);
    }
}
