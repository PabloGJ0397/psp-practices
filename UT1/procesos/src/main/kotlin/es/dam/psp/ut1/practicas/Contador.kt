package es.dam.psp.ut1.practicas

import kotlin.system.exitProcess

/**
 * Contador.kt · Práctica 4 · proceso HIJO (ya terminado, no hay que modificarlo).
 *
 * Apuntes UT-1, apartados 6 y 6.3. Lo lanza practicas/Coordinador.kt con
 * Jvm.proceso("es.dam.psp.ut1.practicas.ContadorKt") y redirectInput(fichero).
 *
 * Lee un texto completo por su ENTRADA ESTÁNDAR y escribe UNA línea en su SALIDA ESTÁNDAR:
 *
 *     lineas;palabras;caracteres;palabraMasFrecuente
 *
 * Código de salida: 0 si todo va bien, 1 si la entrada estaba vacía.
 *
 * Prueba manual desde la terminal, en la raíz del proyecto y tras ejecutar GeneradorDatos.kt
 * (el classpath se copia de la primera línea que muestra IntelliJ al ejecutar):
 *     java -cp <classpath> es.dam.psp.ut1.practicas.ContadorKt < datos/texto01.txt
 */
fun main() {
    var lineas = 0L
    var palabras = 0L
    var caracteres = 0L
    val frecuencias = HashMap<String, Int>()     // palabra -> nº de apariciones

    generateSequence(::readLine).forEach { linea ->   // hasta el fin de fichero (null)
        lineas++
        caracteres += linea.length
        linea.split(' ').filter { it.isNotBlank() }.forEach { p ->
            palabras++
            frecuencias.merge(p.lowercase(), 1, Int::plus)   // suma 1 (o pone 1 si es nueva)
        }
    }

    if (lineas == 0L) {
        System.err.println("Contador: entrada vacía")   // por stderr: el padre lo hereda
        exitProcess(1)
    }

    val masFrecuente = frecuencias.maxByOrNull { it.value }?.key ?: "-"
    println("$lineas;$palabras;$caracteres;$masFrecuente")
}
