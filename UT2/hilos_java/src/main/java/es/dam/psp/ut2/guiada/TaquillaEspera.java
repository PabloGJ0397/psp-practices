package es.dam.psp.ut2.guiada;

import java.util.List;

/**
 * Práctica guiada · Reto C. Taquilla con devoluciones y lista de espera.
 *
 * Apuntes UT-2, apartados 2 (interrupt), 6.1 (synchronized) y 7.1 (wait() y notifyAll()).
 * No se ejecuta sola: la prueba VerificadorEspera.java (misma carpeta, guiada/).
 * Debe cumplir el contrato de TaquillaConEspera.java.
 */
public class TaquillaEspera implements TaquillaConEspera {
    private final int aforo;

    public TaquillaEspera(int aforo) { this.aforo = aforo; }

    @Override
    public int aforo() { return aforo; }

    @Override
    public List<Integer> comprar(String cliente, int n) { throw new UnsupportedOperationException("Reto C"); }

    @Override
    public int vendidas() { throw new UnsupportedOperationException("Reto C"); }

    @Override
    public long recaudado() { throw new UnsupportedOperationException("Reto C"); }

    @Override
    public boolean devolver(String cliente, int asiento) { throw new UnsupportedOperationException("Reto C"); }

    @Override
    public Integer esperarEntrada(String cliente) { throw new UnsupportedOperationException("Reto C"); }
}
