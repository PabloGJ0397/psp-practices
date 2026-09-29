package es.dam.psp.ut1.practicas;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * GeneradorDatos.java · Práctica 4 · genera los datos de entrada. Ejecútalo UNA vez antes de Coordinador.java.
 *
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 * Crea la carpeta datos/ en el directorio de trabajo (la raíz del proyecto procesos_java cuando se
 * ejecuta desde IntelliJ) con 8 ficheros de texto, unos 30 MB en total. La carpeta está en
 * .gitignore: no se sube al repositorio.
 * Siempre genera los mismos ficheros (semilla fija), así todos obtenéis los mismos totales,
 * y además son idénticos a los que genera GeneradorDatos.kt de la versión Kotlin.
 */
public class GeneradorDatos {

    public static void main(String[] args) throws IOException {
        String[] vocabulario = """
                proceso hilo servicio socket servidor cliente puerto tubería señal memoria
                planificador cola cpu núcleo estado listo bloqueado ejecución terminado nuevo
                sincronizar esperar compartir recurso prioridad contexto pila montón sistema operativo
                kotlin android java jvm red protocolo cifrado clave seguro dato fichero archivo
                el la los las un una de del y o que en con por para como más muy también
                """.trim().split("\\s+");

        File carpeta = new File("datos");
        carpeta.mkdirs();
        AleatorioKotlin rnd = new AleatorioKotlin(2627);   // semilla fija: mismos datos en todos los equipos

        for (int f = 1; f <= 8; f++) {
            File fichero = new File(carpeta, String.format("texto%02d.txt", f));
            try (BufferedWriter w = Files.newBufferedWriter(fichero.toPath(), StandardCharsets.UTF_8)) {
                int lineas = 40_000 + f * 5_000;                    // líneas por fichero
                for (int i = 0; i < lineas; i++) {
                    int palabras = 4 + rnd.nextInt(12);
                    StringBuilder linea = new StringBuilder();
                    for (int p = 0; p < palabras; p++) {
                        if (p > 0) linea.append(' ');
                        linea.append(vocabulario[rnd.nextInt(vocabulario.length)]);
                    }
                    w.write(linea.toString());
                    w.newLine();
                }
            }
            System.out.println("Creado " + fichero.getPath() + " (" + fichero.length() / 1024 + " KB)");
        }
    }

    /**
     * Generador de números aleatorios IDÉNTICO a kotlin.random.Random(semilla) (algoritmo xorwow).
     * No hace falta entenderlo: está aquí solo para que los ficheros de datos/ sean exactamente
     * los mismos que genera la versión Kotlin, y así los totales de la Práctica 4 coincidan.
     * (java.util.Random con la misma semilla daría otros números.)
     */
    static class AleatorioKotlin {
        private int x, y, z, w, v, sumando;

        AleatorioKotlin(int semilla) {
            int semilla2 = semilla >> 31;
            x = semilla;
            y = semilla2;
            z = 0;
            w = 0;
            v = ~semilla;
            sumando = (semilla << 10) ^ (semilla2 >>> 4);
            for (int i = 0; i < 64; i++) siguiente();   // Kotlin descarta los 64 primeros valores
        }

        private int siguiente() {
            int t = x;
            t = t ^ (t >>> 2);
            x = y;
            y = z;
            z = w;
            int v0 = v;
            w = v0;
            t = (t ^ (t << 1)) ^ v0 ^ (v0 << 4);
            v = t;
            sumando += 362437;
            return t + sumando;
        }

        /** Número entre 0 (incluido) y n (excluido), como Random.nextInt(n) de Kotlin. */
        int nextInt(int n) {
            if ((n & -n) == n) {                            // n es potencia de 2: bits altos
                int bits = 31 - Integer.numberOfLeadingZeros(n);
                int r = siguiente();                        // se consume un valor aunque n sea 1
                return bits == 0 ? 0 : r >>> (32 - bits);
            }
            int aleatorio, valor;
            do {
                aleatorio = siguiente() >>> 1;
                valor = aleatorio % n;
            } while (aleatorio - valor + (n - 1) < 0);
            return valor;
        }
    }
}
