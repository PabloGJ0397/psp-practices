package es.dam.psp.ut2.guiada;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.IntFunction;

/**
 * Práctica guiada · Verificador de taquillas (Retos A y B).
 *
 * Apuntes UT-2, apartados 2 (start y join), 5 (condición de carrera) y 7.2 (CountDownLatch).
 * Ejecutar: ▶ junto a main. Repite RONDAS veces la misma prueba: CLIENTES hilos salen a la vez
 * (CountDownLatch) y cada uno intenta INTENTOS compras de 1 a 4 entradas. Después comprueba
 * cinco reglas del contrato (Taquilla.java) y cuenta en cuántas rondas se ha roto cada una.
 *
 * Para probar otra taquilla, cambia solo la línea de FABRICA (Reto B: TaquillaSegura::new).
 * NO MODIFIQUES NADA MÁS (ni tú ni la IA): un verificador que se ajusta al código no verifica nada.
 */
public class Verificador {
    static final IntFunction<Taquilla> FABRICA = TaquillaIA::new;

    static final int AFORO = 200;
    static final int CLIENTES = 16;
    static final int INTENTOS = 40;
    static final int RONDAS = 30;

    static final List<String> REGLAS = List.of("R1 todo o nada", "R2 asiento vendido dos veces",
            "R3 sobreventa", "R4 vendidas() no cuadra", "R5 recaudado() no cuadra");

    /** Lo que un cliente pidió y lo que recibió en una compra. */
    record Compra(String cliente, int pedidas, List<Integer> asientos) { }

    /** Ejecuta una ronda sobre taquilla y devuelve todas las compras de todos los clientes. */
    static List<Compra> ronda(Taquilla taquilla) throws InterruptedException {
        CountDownLatch salida = new CountDownLatch(1);
        // Cada hilo apunta sus compras en su propia lista: el verificador no comparte nada entre hilos.
        List<List<Compra>> porCliente = new ArrayList<>();
        List<Thread> hilos = new ArrayList<>();
        for (int c = 0; c < CLIENTES; c++) {
            List<Compra> suyas = new ArrayList<>();
            porCliente.add(suyas);
            String nombre = "cliente-" + c;
            Thread t = new Thread(() -> {
                try {
                    salida.await();                                  // todos salen a la vez
                } catch (InterruptedException e) {
                    return;
                }
                for (int i = 0; i < INTENTOS; i++) {
                    int n = ThreadLocalRandom.current().nextInt(1, 5);
                    suyas.add(new Compra(nombre, n, taquilla.comprar(nombre, n)));
                }
            }, nombre);
            hilos.add(t);
            t.start();
        }
        salida.countDown();
        for (Thread t : hilos) t.join();
        List<Compra> todas = new ArrayList<>();
        porCliente.forEach(todas::addAll);
        return todas;
    }

    /** Comprueba las reglas del contrato. Devuelve el nombre de cada regla que se ha roto. */
    static List<String> reglasRotas(Taquilla taquilla, List<Compra> compras) {
        List<String> rotas = new ArrayList<>();
        List<Integer> entregados = new ArrayList<>();
        compras.forEach(c -> entregados.addAll(c.asientos()));

        if (compras.stream().anyMatch(c -> !c.asientos().isEmpty() && c.asientos().size() != c.pedidas()))
            rotas.add(REGLAS.get(0));
        if (entregados.size() != new HashSet<>(entregados).size())
            rotas.add(REGLAS.get(1));
        if (entregados.size() > taquilla.aforo() || entregados.stream().anyMatch(a -> a < 0 || a >= taquilla.aforo()))
            rotas.add(REGLAS.get(2));
        if (taquilla.vendidas() != entregados.size())
            rotas.add(REGLAS.get(3));
        if (taquilla.recaudado() != entregados.size() * Taquilla.PRECIO)
            rotas.add(REGLAS.get(4));
        return rotas;
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> fallos = new LinkedHashMap<>();
        String ejemplo = "";

        long inicio = System.nanoTime();
        for (int r = 0; r < RONDAS; r++) {
            Taquilla taquilla = FABRICA.apply(AFORO);
            List<Compra> compras = ronda(taquilla);
            List<String> rotas = reglasRotas(taquilla, compras);
            rotas.forEach(regla -> fallos.merge(regla, 1, Integer::sum));
            if (!rotas.isEmpty() && ejemplo.isEmpty()) {
                int entregados = compras.stream().mapToInt(c -> c.asientos().size()).sum();
                ejemplo = "Ronda " + (r + 1) + ": entregados " + entregados + " asientos, vendidas()="
                        + taquilla.vendidas() + ", recaudado()=" + taquilla.recaudado()
                        + " € (debería ser " + entregados * Taquilla.PRECIO + " €)";
            }
        }
        long ms = (System.nanoTime() - inicio) / 1_000_000;

        String nombre = FABRICA.apply(1).getClass().getSimpleName();
        System.out.printf("Taquilla: %s · %d rondas · %d hilos · aforo %d · tiempo %d ms%n",
                nombre, RONDAS, CLIENTES, AFORO, ms);
        for (String regla : REGLAS) {
            int n = fallos.getOrDefault(regla, 0);
            System.out.printf("  %-30s %s%n", regla, n == 0 ? "OK" : "ROTA en " + n + "/" + RONDAS + " rondas");
        }
        if (!ejemplo.isEmpty()) System.out.println("  Primer fallo · " + ejemplo);
        System.out.println(fallos.isEmpty()
                ? "VEREDICTO: ninguna regla rota en " + RONDAS + " rondas (no demuestra que sea correcta)"
                : "VEREDICTO: INCORRECTA");
    }
}
