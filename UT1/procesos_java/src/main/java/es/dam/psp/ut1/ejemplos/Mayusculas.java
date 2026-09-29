package es.dam.psp.ut1.ejemplos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * Mayusculas.java · proceso HIJO de E3: lee líneas de su entrada estándar hasta el final (EOF)
 * y escribe cada una en mayúsculas. Devuelve como código de salida el número de líneas.
 *
 * Apuntes UT-1, apartado 6 (ejemplos/E3_Tuberia.java lo lanza con Jvm.proceso).
 * Puedes probarlo solo: triángulo ▶ junto a main, escribe varias líneas en la consola y
 * pulsa Ctrl+D (Ctrl+Z e Intro en Windows) para enviar el fin de fichero.
 */
public class Mayusculas {

    public static void main(String[] args) throws IOException {
        int n = 0;
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String linea;
        while ((linea = entrada.readLine()) != null) {   // readLine() devuelve null en el fin de fichero
            System.out.println(linea.toUpperCase());
            n++;
        }
        // Por la salida de error, para no mezclarlo con los datos que lee el padre
        System.err.println("[hijo " + ProcessHandle.current().pid() + "] procesadas " + n + " líneas");
        System.exit(n);   // el código de salida transmite un resultado al padre
    }
}
