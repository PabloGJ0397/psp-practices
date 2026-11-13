package es.dam.psp.ut2.guiada;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * ─────────────────────────────────────────────────────────────────────────────
 *  CÓDIGO GENERADO POR UN ASISTENTE DE IA. NO REVISADO.
 *
 *  Petición que se le hizo: "Implementa en Java la interfaz Taquilla para que la
 *  usen muchos hilos a la vez".
 *
 *  Lo que respondió el asistente junto al código:
 *  "Esta implementación es completamente thread-safe porque utiliza AtomicInteger
 *   para el contador y ConcurrentHashMap para los asientos, que son clases
 *   concurrentes de java.util.concurrent."
 *
 *  Práctica guiada, Reto A: ejecuta Verificador.java (misma carpeta, guiada/) y averigua
 *  si el asistente tenía razón. NO corrijas este fichero: la versión corregida va en
 *  TaquillaSegura.java (Reto B).
 * ─────────────────────────────────────────────────────────────────────────────
 */
public class TaquillaIA implements Taquilla {
    private final int aforo;
    private final AtomicInteger contador = new AtomicInteger(0);
    private final ConcurrentHashMap<Integer, String> asientos = new ConcurrentHashMap<>();
    private long caja = 0;

    public TaquillaIA(int aforo) { this.aforo = aforo; }

    @Override
    public int aforo() { return aforo; }

    @Override
    public List<Integer> comprar(String cliente, int n) {
        // Comprobamos que quedan entradas suficientes
        if (contador.get() + n > aforo) return List.of();

        // Buscamos asientos libres
        List<Integer> asignados = new ArrayList<>();
        for (int asiento = 0; asiento < aforo; asiento++) {
            if (asignados.size() == n) break;
            if (!asientos.containsKey(asiento)) {
                asientos.put(asiento, cliente);
                asignados.add(asiento);
            }
        }

        // Actualizamos los contadores de forma segura
        contador.addAndGet(n);
        caja += n * PRECIO;
        return asignados;
    }

    @Override
    public int vendidas() { return contador.get(); }

    @Override
    public long recaudado() { return caja; }
}
