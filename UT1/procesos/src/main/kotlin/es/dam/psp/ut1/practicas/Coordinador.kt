package es.dam.psp.ut1.practicas

import es.dam.psp.ut1.Jvm
import java.io.File
import kotlin.time.measureTime

/**
 * Coordinador.kt · Práctica 4 · proceso PADRE.
 *
 * Reparte los ficheros de datos/ entre procesos hijo Contador (practicas/Contador.kt),
 * recoge lo que escribe cada hijo y muestra una tabla con los resultados y su TOTAL.
 * Ejecuta el trabajo de dos formas y compara el tiempo:
 *  - secuencial: lanza un hijo, espera a que termine y lanza el siguiente.
 *  - concurrente: lanza todos los hijos a la vez y después recoge los resultados.
 * Al final comprueba que ambas formas dan los mismos resultados y muestra los dos tiempos
 * (la aceleración es t_secuencial / t_concurrente).
 *
 * Antes de ejecutarlo: ejecuta practicas/GeneradorDatos.kt para crear datos/ en la raíz del proyecto.
 * Cómo ejecutarlo: triángulo ▶ junto a main, sin argumentos.
 */

/** Resultado que devuelve un hijo para un fichero. */
// data class: clase pensada para guardar datos. Kotlin le genera solo toString() (para imprimirla)
// y equals() (dos Resultado son iguales si TODOS sus campos coinciden; lo usamos en r1 == r2).
data class Resultado(
    val fichero: String,        // nombre del fichero, p. ej. "texto01.txt"
    val lineas: Long,           // número de líneas
    val palabras: Long,         // número de palabras
    val caracteres: Long,       // número de caracteres
    val masFrecuente: String    // palabra que más se repite
)

const val CLASE_HIJO = "es.dam.psp.ut1.practicas.ContadorKt"   // main de Contador.kt

/**
 * Crea y ARRANCA un proceso Contador cuya entrada estándar sea [fichero].
 * Hereda la salida de error del hijo.
 */
fun lanzarContador(fichero: File): Process {
    // Jvm.proceso(CLASE_HIJO): devuelve un ProcessBuilder (la "receta") para lanzar otra JVM
    //   que ejecute la clase ContadorKt. Todavía no ha arrancado nada.
    // .redirectInput(fichero): lo que el hijo lea por su entrada estándar saldrá de ese fichero.
    //   Devuelve el mismo ProcessBuilder, por eso se puede seguir encadenando.
    // .redirectError(INHERIT): los errores del hijo (System.err) salen por la consola del padre.
    // .start(): ARRANCA el proceso y devuelve un Process (ya en marcha, ya no es una receta).
    val procesoHijo =
        Jvm.proceso(CLASE_HIJO).redirectInput(fichero).redirectError(ProcessBuilder.Redirect.INHERIT).start()
    // Se devuelve sin esperar a que termine: quien llama decide cuándo esperar.
    return procesoHijo
}

/**
 * Lee la línea que ha escrito el hijo, espera a que termine y construye el Resultado.
 * Si el código de salida no es 0, lanza una IllegalStateException con un mensaje claro.
 */
fun recogerResultado(fichero: File, hijo: Process): Resultado {
    // hijo.inputStream: lo que el hijo ESCRIBE (su println), visto desde el padre, que lo LEE (bytes).
    // .bufferedReader(): envuelve esos bytes en un lector de texto.
    // .readLine(): lee una línea completa y la devuelve como String (null si no hay nada).
    // Se lee ANTES de esperar para no bloquear al hijo si su salida llenara la tubería.
    val linea = hijo.inputStream.bufferedReader().readLine()
    // waitFor(): bloquea al padre hasta que el hijo termina; devuelve su código de salida (Int).
    val codigo = hijo.waitFor()
    // Código 0 = éxito; cualquier otro = error del hijo.
    if (codigo != 0) {
        // throw: lanza un error y detiene la función; no tiene sentido construir un Resultado si el hijo falló.
        throw IllegalStateException("El hijo falló con el fichero ${fichero.name}, código $codigo")
    }
    // split(";"): corta el texto por cada ";" y devuelve una List<String>.
    // La línea es "lineas;palabras;caracteres;masFrecuente" → partes[0..3].
    val partes = linea.split(";")
    // toLong(): convierte un texto con un número en un Long. fichero.name es solo el nombre (sin ruta).
    val resultado = Resultado(fichero.name, partes[0].toLong(), partes[1].toLong(), partes[2].toLong(), partes[3])
    return resultado
}

/** Procesa los ficheros uno detrás de otro: lanzar, esperar, lanzar, esperar... */
fun secuencial(ficheros: List<File>): List<Resultado> {
    // Lista vacía a la que se le pueden ir añadiendo elementos; solo guardará Resultado.
    val resultados = mutableListOf<Resultado>()
    // forEach: ejecuta el bloque una vez por cada fichero de la lista.
    ficheros.forEach { fichero ->
        // Lanza el hijo de este fichero (devuelve el Process, sin esperar).
        val procesoEnCurso = lanzarContador(fichero)
        // Espera a ESE hijo y recoge su resultado. Hasta que no termina, no se lanza el siguiente fichero.
        val resultado = recogerResultado(fichero, procesoEnCurso)
        resultados.add(resultado)
    }
    return resultados
}

/** Lanza TODOS los hijos a la vez y después recoge los resultados. */
fun concurrente(ficheros: List<File>): List<Resultado> {
    // Fase 1: map recorre la lista y devuelve una lista NUEVA con lo que devuelve cada vuelta.
    //   Aquí, un Process por fichero. Se lanzan los 8 seguidos, sin esperar a ninguno: trabajan a la vez.
    //   La lista mantiene el orden: procesos[0] es el hijo de ficheros[0], y así sucesivamente.
    val procesos = ficheros.map { fichero -> lanzarContador(fichero) }
    // Fase 2: mapIndexed es como map pero también da la posición. Con ella emparejamos cada fichero
    //   con su proceso (procesos[posicion]) y los recogemos por orden. Devuelve una List<Resultado>.
    return ficheros.mapIndexed { posicion, fichero -> recogerResultado(fichero, procesos[posicion]) }
}

fun main() {
    // Ruta relativa: datos/ dentro del directorio de trabajo (la raíz del proyecto en IntelliJ)
    // File("datos").listFiles { ... }: lista lo que hay en la carpeta, filtrando por el bloque
    //   (solo los que tengan extensión "txt"). Devuelve un Array<File>?, que es null si la carpeta no existe.
    // ?.sortedBy { it.name }: si no es null, los ordena por nombre y devuelve una List<File>.
    //   Si era null, todo el resultado es null (por eso el "?").
    val ficheros = File("datos").listFiles { f -> f.extension == "txt" }?.sortedBy { it.name }
    // isNullOrEmpty(): true si es null o no tiene elementos. Si es así, avisa y sale de main con return.
    // Después de este if, Kotlin ya sabe que "ficheros" NO es null (por eso se puede usar sin "?" abajo).
    if (ficheros.isNullOrEmpty()) {
        println("No hay ficheros en datos/. Ejecuta antes GeneradorDatos.kt")
        return
    }
    // availableProcessors(): número de procesadores lógicos que ve la JVM (en mi equipo, 12).
    println("Procesadores disponibles: ${Runtime.getRuntime().availableProcessors()}")

    // lateinit var: variable que se declara ahora y se le da valor más tarde. Hace falta porque
    // se asigna DENTRO de las llaves de measureTime, y desde fuera Kotlin no puede saberlo.
    lateinit var r1: List<Resultado>           // se asignan dentro de measureTime { }
    lateinit var r2: List<Resultado>
    // measureTime { ... }: ejecuta el bloque y devuelve cuánto ha tardado (un Duration).
    // Dentro, además, guardamos en r1 el resultado de la versión secuencial.
    val t1 = measureTime { r1 = secuencial(ficheros) }
    // Lo mismo con la versión concurrente: r2 y su tiempo t2.
    val t2 = measureTime { r2 = concurrente(ficheros) }

    // Cabecera de la tabla. %-12s = texto alineado a la izquierda en 12 huecos; %10s = alineado a la
    // derecha en 10 huecos. .format(...) rellena cada % con un argumento, en orden.
    println("%-12s %10s %10s %12s  %s".format("FICHERO", "LINEAS", "PALABRAS", "CARACTERES", "MAS FRECUENTE"))

    // Una fila por cada Resultado de r1 (r es cada uno). Mismo formato que la cabecera
    // para que las columnas queden alineadas; los argumentos van en el mismo orden.
    r1.forEach { r ->
        println(
            "%-12s %10s %10s %12s  %s".format(
                r.fichero,
                r.lineas,
                r.palabras,
                r.caracteres,
                r.masFrecuente
            )
        )
    }
    // sumOf { ... }: recorre la lista, calcula lo que pongas dentro para cada elemento y devuelve la SUMA.
    val totalLineas = r1.sumOf { it.lineas }
    val totalPalabras = r1.sumOf { it.palabras }
    val totalCaracteres = r1.sumOf { it.caracteres }

    // "-".repeat(50): repite el texto 50 veces → una línea separadora.
    println("-".repeat(50))
    // Fila TOTAL con el mismo formato. El último argumento "" es la columna "más frecuente", que va vacía
    // (si faltara, el formato esperaría un argumento más y daría error).
    println("%-12s %10s %10s %12s  %s".format("TOTAL", totalLineas, totalPalabras, totalCaracteres, ""))
    // r1 == r2: compara las dos listas elemento a elemento (por el equals de la data class).
    // true = el método concurrente da exactamente lo mismo que el secuencial.
    println("¿Resultados iguales? ${r1 == r2}")

    // $t1 / $t2: se imprimen los tiempos medidos (Duration), para calcular la aceleración t1 / t2.
    println("Secuencial:  $t1")
    println("Concurrente: $t2")
}