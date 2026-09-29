package es.dam.psp.ut1;

import java.util.ArrayList;
import java.util.List;

/**
 * Jvm.java · clase auxiliar para lanzar otra clase de este proyecto como proceso hijo.
 *
 * Apuntes UT-1, apartados 5.1 (Jvm.ES_WINDOWS, Jvm.comandoShell) y 5.2 (Jvm.proceso).
 * No tiene main: la usan los ejemplos (ejemplos/E1..E5) y las prácticas 3 y 4.
 * Es la versión Java de Jvm.kt (allí es un object; aquí, una clase con todo static).
 *
 * El hijo se ejecuta en una JVM nueva, con el mismo ejecutable java y el mismo
 * classpath que el padre. Así los ejemplos funcionan igual en Windows, Linux y macOS.
 * En Java la clase se llama como el fichero: el main de Dormilon.java está en la clase
 * es.dam.psp.ut1.ejemplos.Dormilon (en Kotlin sería DormilonKt).
 */
public final class Jvm {

    /** Ruta del ejecutable java que está ejecutando este programa. */
    public static final String JAVA = ProcessHandle.current().info().command().orElse("java");

    /** Classpath actual: el hijo verá las mismas clases que el padre. */
    public static final String CLASSPATH = System.getProperty("java.class.path");

    /** true si el sistema operativo es Windows. */
    public static final boolean ES_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");

    private Jvm() { }   // no se crean objetos Jvm: solo se usan sus miembros static

    /**
     * Crea (sin arrancarlo) un ProcessBuilder que ejecuta el main de la clase indicada.
     * Ejemplo: Jvm.proceso("es.dam.psp.ut1.ejemplos.Dormilon", "3")
     */
    public static ProcessBuilder proceso(String clase, String... args) {
        List<String> comando = new ArrayList<>(List.of(JAVA, "-cp", CLASSPATH, clase));
        comando.addAll(List.of(args));
        return new ProcessBuilder(comando);
    }

    /** Convierte una orden de consola en la lista que necesita ProcessBuilder según el SO. */
    public static List<String> comandoShell(String orden) {
        return ES_WINDOWS ? List.of("cmd", "/c", orden) : List.of("sh", "-c", orden);
    }
}
