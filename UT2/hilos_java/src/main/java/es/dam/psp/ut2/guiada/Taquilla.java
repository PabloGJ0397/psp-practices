package es.dam.psp.ut2.guiada;

import java.util.List;

/**
 * Práctica guiada · Taquilla de entradas. Contrato que deben cumplir todas las taquillas.
 *
 * Apuntes UT-2, apartados 5 (condición de carrera), 6.1 (synchronized, atómicas y cerrojos)
 * y 7.1 (wait() y notifyAll()). Esta interfaz no se ejecuta: la usan Verificador.java y
 * VerificadorEspera.java (misma carpeta, guiada/).
 *
 * NO MODIFIQUES ESTE FICHERO (ni tú ni la IA): es la especificación.
 *
 * Taquilla con asientos numerados de 0 a aforo() - 1.
 * Todos los métodos pueden llamarse a la vez desde varios hilos.
 */
public interface Taquilla {
    /** Precio de cada entrada, en euros. */
    long PRECIO = 25L;

    /** Número total de asientos. */
    int aforo();

    /**
     * Vende n asientos a cliente: TODOS O NINGUNO.
     * Devuelve los números de los n asientos vendidos, o una lista vacía si no quedan n libres.
     * Un asiento vendido no puede volver a venderse mientras no se devuelva.
     */
    List<Integer> comprar(String cliente, int n);

    /** Número de asientos vendidos en este momento. */
    int vendidas();

    /** Dinero en caja: PRECIO por cada entrada vendida, menos los reembolsos. */
    long recaudado();
}
