package es.dam.psp.ut2.guiada;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;

/**
 * Práctica guiada · Verificador de la lista de espera (Reto C).
 *
 * Apuntes UT-2, apartados 2 (interrupt y join), 3 (estados) y 7.1 (wait() y notifyAll()).
 * Ejecutar: ▶ junto a main. En cada ronda se agota el aforo, ESPERAN hilos se ponen a esperar
 * (esperarEntrada), DEVUELVEN dueños devuelven su entrada a la vez y, al final, se interrumpe a
 * los que siguen esperando. Comprueba seis reglas y cuenta en cuántas rondas se rompe cada una.
 *
 * Para probar otra taquilla, cambia solo la línea de FABRICA_ESPERA.
 * NO MODIFIQUES NADA MÁS (ni tú ni la IA).
 */
public class VerificadorEspera {
    static final IntFunction<TaquillaConEspera> FABRICA_ESPERA = TaquillaEspera::new;

    static final int AFORO_ESPERA = 20;
    static final int ESPERAN = 10;
    static final int DEVUELVEN = 6;
    static final int RONDAS_ESPERA = 5;

    static final List<String> REGLAS_ESPERA = List.of(
            "E1 esperan en WAITING (sin espera activa)",
            "E2 devolver() solo vale al dueño",
            "E3 una devolución = un cliente servido",
            "E4 sin asientos repetidos",
            "E5 interrupt() libera a los demás",
            "E6 vendidas() y recaudado() cuadran");

    /** Ejecuta una ronda completa y devuelve las reglas rotas (vacía si todo va bien). */
    static Set<String> rondaEspera() throws InterruptedException {
        Set<String> rotas = new LinkedHashSet<>();
        TaquillaConEspera taquilla = FABRICA_ESPERA.apply(AFORO_ESPERA);

        // 1. Se vende todo el aforo, una entrada por fan (desde main, sin concurrencia).
        Map<Integer, String> duenoDe = new HashMap<>();
        for (int i = 0; i < AFORO_ESPERA; i++) {
            String fan = "fan-" + i;
            taquilla.comprar(fan, 1).forEach(a -> duenoDe.put(a, fan));
        }
        if (duenoDe.size() != AFORO_ESPERA || !taquilla.comprar("tarde", 1).isEmpty())
            return Set.of(REGLAS_ESPERA.get(5));                     // ni siquiera vende bien el aforo

        // 2. ESPERAN clientes se ponen a la cola. Cada hilo escribe solo en su posición del array.
        Integer[] conseguido = new Integer[ESPERAN];
        List<Thread> esperando = new ArrayList<>();
        for (int i = 0; i < ESPERAN; i++) {
            final int k = i;
            Thread t = new Thread(() -> conseguido[k] = taquilla.esperarEntrada("espera-" + k), "espera-" + k);
            esperando.add(t);
            t.start();
        }
        Thread.sleep(300);
        if (esperando.stream().anyMatch(t -> t.getState() != Thread.State.WAITING)) rotas.add(REGLAS_ESPERA.get(0));

        // 3. Un intruso intenta devolver un asiento que no es suyo: debe fallar.
        List<Integer> asientosDevueltos = duenoDe.keySet().stream().sorted().limit(DEVUELVEN).toList();
        if (taquilla.devolver("intruso", asientosDevueltos.get(0))) rotas.add(REGLAS_ESPERA.get(1));

        // 4. DEVUELVEN dueños devuelven su entrada a la vez.
        boolean[] okDevolucion = new boolean[DEVUELVEN];
        List<Thread> devuelven = new ArrayList<>();
        for (int i = 0; i < DEVUELVEN; i++) {
            final int k = i;
            int asiento = asientosDevueltos.get(k);
            Thread t = new Thread(() -> okDevolucion[k] = taquilla.devolver(duenoDe.get(asiento), asiento));
            devuelven.add(t);
            t.start();
        }
        for (Thread t : devuelven) t.join();
        for (boolean ok : okDevolucion) if (!ok) rotas.add(REGLAS_ESPERA.get(1));

        // 5. Se da tiempo a que se sirva a los que esperan y se interrumpe a los demás.
        Thread.sleep(500);
        for (Thread t : esperando) t.interrupt();
        for (Thread t : esperando) t.join(1000);
        if (esperando.stream().anyMatch(Thread::isAlive)) {
            rotas.add(REGLAS_ESPERA.get(4));
            return rotas;                                             // hay hilos colgados: no se puede seguir
        }

        // 6. Comprobaciones finales.
        List<Integer> servidos = new ArrayList<>();
        for (Integer a : conseguido) if (Objects.nonNull(a)) servidos.add(a);
        if (servidos.size() != DEVUELVEN) rotas.add(REGLAS_ESPERA.get(2));
        if (servidos.size() != new HashSet<>(servidos).size() || !asientosDevueltos.containsAll(servidos))
            rotas.add(REGLAS_ESPERA.get(3));
        if (!servidos.isEmpty() && taquilla.devolver(duenoDe.get(servidos.get(0)), servidos.get(0)))
            rotas.add(REGLAS_ESPERA.get(1));                         // el antiguo dueño ya no lo es
        if (taquilla.vendidas() != AFORO_ESPERA - DEVUELVEN + servidos.size()
                || taquilla.recaudado() != taquilla.vendidas() * Taquilla.PRECIO)
            rotas.add(REGLAS_ESPERA.get(5));
        return rotas;
    }

    public static void main(String[] args) throws InterruptedException {
        Map<String, Integer> fallos = new LinkedHashMap<>();
        for (int r = 0; r < RONDAS_ESPERA; r++)
            for (String regla : rondaEspera()) fallos.merge(regla, 1, Integer::sum);

        System.out.printf("Taquilla: %s · %d rondas · %d esperan · %d devuelven%n",
                FABRICA_ESPERA.apply(1).getClass().getSimpleName(), RONDAS_ESPERA, ESPERAN, DEVUELVEN);
        for (String regla : REGLAS_ESPERA) {
            int n = fallos.getOrDefault(regla, 0);
            System.out.printf("  %-42s %s%n", regla, n == 0 ? "OK" : "ROTA en " + n + "/" + RONDAS_ESPERA + " rondas");
        }
        System.out.println(fallos.isEmpty()
                ? "VEREDICTO: ninguna regla rota (no demuestra que sea correcta)"
                : "VEREDICTO: INCORRECTA");
    }
}
