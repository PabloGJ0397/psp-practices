package es.dam.psp.ut1.ejemplos;

import es.dam.psp.ut1.Jvm;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * E2 · Capturar (leer) la salida de un proceso hijo.
 *
 * Apuntes UT-1, apartado 5.3. Se usa en la Actividad 1.4.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Lanza "java -version" con el mismo java que ejecuta este programa (Jvm.JAVA) y muestra
 * cada línea numerada. Para el padre, la salida estándar del hijo es un InputStream
 * (algo que él lee).
 */
public class E2_LeerSalida {

    public static void main(String[] args) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(Jvm.JAVA, "-version")   // Jvm.JAVA: ruta del java que nos ejecuta
                .redirectErrorStream(true);     // mezcla stderr con stdout (java -version escribe en stderr)

        Process proceso = pb.start();

        // Leer TODA la salida antes de waitFor(): si el hijo escribe mucho y nadie lee,
        // el buffer de la tubería se llena, el hijo se bloquea y el padre espera para siempre.
        List<String> lineas;
        try (BufferedReader r = new BufferedReader(new InputStreamReader(proceso.getInputStream()))) {
            lineas = r.lines().toList();
        }
        int codigo = proceso.waitFor();

        for (int i = 0; i < lineas.size(); i++) {
            System.out.printf("%2d | %s%n", i + 1, lineas.get(i));
        }
        System.out.println("Líneas leídas: " + lineas.size() + " · código de salida: " + codigo);
    }
}
