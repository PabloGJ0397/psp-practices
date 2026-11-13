package es.dam.psp.ut2.guiada;

/**
 * Práctica guiada · Reto C: taquilla con devoluciones y lista de espera.
 * NO MODIFIQUES ESTE FICHERO (ni tú ni la IA): es la especificación.
 */
public interface TaquillaConEspera extends Taquilla {
    /**
     * cliente devuelve su asiento: el asiento queda libre y se le reembolsa PRECIO.
     * Devuelve false (y no cambia nada) si ese asiento no está vendido a ese cliente.
     */
    boolean devolver(String cliente, int asiento);

    /**
     * Bloquea al hilo hasta que haya un asiento libre y se lo vende a cliente.
     * Devuelve el número de asiento, o null si interrumpen al hilo mientras espera.
     */
    Integer esperarEntrada(String cliente);
}
