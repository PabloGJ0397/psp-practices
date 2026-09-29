package es.dam.psp.ut1.ejemplos;

import es.dam.psp.ut1.Jvm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * E3 · Comunicación padre ↔ hijo con tuberías (pipes).
 *
 * Apuntes UT-1, apartado 6. El proceso hijo es ejemplos/Mayusculas.java (clase Mayusculas).
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos (el hijo lo lanza este programa).
 * - Lo que el padre escribe en proceso.getOutputStream() le llega al hijo por su entrada estándar.
 * - Lo que el hijo escribe en su salida estándar el padre lo lee en proceso.getInputStream().
 * Salida esperada: las tres líneas en mayúsculas precedidas de "Hijo: ", el mensaje del hijo
 * por la salida de error y "Código de salida (nº de líneas): 3".
 */
public class E3_Tuberia {

    public static void main(String[] args) throws IOException, InterruptedException {
        Process hijo = Jvm.proceso("es.dam.psp.ut1.ejemplos.Mayusculas")
                .redirectError(ProcessBuilder.Redirect.INHERIT)   // los mensajes de error del hijo, a nuestra consola
                .start();

        // 1. Enviar datos al hijo y cerrar su entrada (el hijo verá fin de fichero).
        //    try-with-resources cierra el writer al salir del bloque.
        try (BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(hijo.getOutputStream(), StandardCharsets.UTF_8))) {
            for (String linea : List.of("hola", "programación de servicios", "y procesos")) {
                w.write(linea);
                w.newLine();
            }
        }

        // 2. Leer su respuesta
        try (BufferedReader r = new BufferedReader(new InputStreamReader(hijo.getInputStream()))) {
            String linea;
            while ((linea = r.readLine()) != null) {
                System.out.println("Hijo: " + linea);
            }
        }

        // 3. Sincronizar: esperar a que termine y recoger su resultado
        System.out.println("Código de salida (nº de líneas): " + hijo.waitFor());
    }
}
