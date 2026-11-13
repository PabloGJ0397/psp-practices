package es.dam.psp.ut2.guiada;

import java.util.List;

/**
 * Práctica guiada · Reto B. Versión corregida de TaquillaIA (misma carpeta, guiada/).
 *
 * Apuntes UT-2, apartados 5 (condición de carrera) y 6.1 (synchronized, atómicas y cerrojos).
 * No se ejecuta sola: cambia la línea FABRICA de Verificador.java a TaquillaSegura::new y ejecuta
 * Verificador.java. Debe cumplir el contrato de Taquilla.java.
 */
public class TaquillaSegura implements Taquilla {
    private final int aforo;

    public TaquillaSegura(int aforo) { this.aforo = aforo; }

    @Override
    public int aforo() { return aforo; }

    @Override
    public List<Integer> comprar(String cliente, int n) { throw new UnsupportedOperationException("Reto B"); }

    @Override
    public int vendidas() { throw new UnsupportedOperationException("Reto B"); }

    @Override
    public long recaudado() { throw new UnsupportedOperationException("Reto B"); }
}
