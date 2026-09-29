package es.dam.psp.ut1

/**
 * Jvm.kt · objeto auxiliar para lanzar otra clase de este proyecto como proceso hijo.
 *
 * Apuntes UT-1, apartados 5.1 (Jvm.esWindows, Jvm.comandoShell) y 5.2 (Jvm.proceso).
 * No tiene main: lo usan los ejemplos (ejemplos/E1..E5) y las prácticas 3 y 4.
 *
 * El hijo se ejecuta en una JVM nueva, con el mismo ejecutable java y el mismo
 * classpath que el padre. Así los ejemplos funcionan igual en Windows, Linux y macOS.
 * Ojo: el main de un fichero Dormilon.kt se compila en la clase DormilonKt, y ese es
 * el nombre (con paquete) que hay que pasar a Jvm.proceso(...).
 */
object Jvm {

    /** Ruta del ejecutable java que está ejecutando este programa. */
    val java: String = ProcessHandle.current().info().command().orElse("java")

    /** Classpath actual: el hijo verá las mismas clases que el padre. */
    val classpath: String = System.getProperty("java.class.path")

    /** true si el sistema operativo es Windows. */
    val esWindows: Boolean = System.getProperty("os.name").lowercase().startsWith("windows")

    /**
     * Crea (sin arrancarlo) un ProcessBuilder que ejecuta el main de [clase].
     * Ejemplo: Jvm.proceso("es.dam.psp.ut1.ejemplos.DormilonKt", "3")
     */
    fun proceso(clase: String, vararg args: String): ProcessBuilder =
        ProcessBuilder(listOf(java, "-cp", classpath, clase) + args)

    /** Convierte una orden de consola en la lista que necesita ProcessBuilder según el SO. */
    fun comandoShell(orden: String): List<String> =
        if (esWindows) listOf("cmd", "/c", orden) else listOf("sh", "-c", orden)
}
