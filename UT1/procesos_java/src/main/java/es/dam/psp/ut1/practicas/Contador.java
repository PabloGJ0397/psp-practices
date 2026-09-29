package es.dam.psp.ut1.practicas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Contador.java · Práctica 4 · proceso HIJO (ya terminado, no hay que modificarlo).
 *
 * Apuntes UT-1, apartados 6 y 6.3. Lo lanza practicas/Coordinador.java con
 * Jvm.proceso("es.dam.psp.ut1.practicas.Contador") y redirectInput(fichero).
 *
 * Lee un texto completo por su ENTRADA ESTÁNDAR y escribe UNA línea en su SALIDA ESTÁNDAR:
 *
 *     lineas;palabras;caracteres;palabraMasFrecuente
 *
 * Código de salida: 0 si todo va bien, 1 si la entrada estaba vacía.
 *
 * Prueba manual desde la terminal, en la raíz del proyecto y tras ejecutar GeneradorDatos.java
 * (el classpath se copia de la primera línea que muestra IntelliJ al ejecutar):
 *     java -cp <classpath> es.dam.psp.ut1.practicas.Contador < datos/texto01.txt
 */
public class Contador {

    public static void main(String[] args) throws IOException {
        long lineas = 0;
        long palabras = 0;
        long caracteres = 0;
        Map<String, Integer> frecuencias = new HashMap<>();     // palabra -> nº de apariciones

        // UTF-8 explícito: los ficheros de datos/ están en UTF-8 (tildes, ñ) en todos los sistemas
        BufferedReader entrada = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        String linea;
        while ((linea = entrada.readLine()) != null) {          // hasta el fin de fichero (null)
            lineas++;
            caracteres += linea.length();
            for (String p : linea.split(" ")) {
                if (p.isBlank()) continue;
                palabras++;
                frecuencias.merge(p.toLowerCase(Locale.ROOT), 1, Integer::sum);   // suma 1 (o pone 1 si es nueva)
            }
        }

        if (lineas == 0) {
            System.err.println("Contador: entrada vacía");     // por stderr: el padre lo hereda
            System.exit(1);
        }

        String masFrecuente = frecuencias.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("-");
        System.out.println(lineas + ";" + palabras + ";" + caracteres + ";" + masFrecuente);
    }
}
