import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Pruebas de ACEPTACION del Ciclo 3 de SlotMachine (SlotMachine(n), solve,
 * simulate y la maquina como testing tool del Problem I de la maraton 2025).
 * Cada prueba es un escenario completo visto desde el usuario; se ejecutan
 * con la maquina invisible para no abrir ventanas ni dialogos. La parte
 * visual (simulate) se muestra en vivo llamando SlotMachineContest.simulate(4).
 *
 * @author Juan Camilo Rojas / Rafael Ricardo Gomez
 */
public class SlotMachineC3Test{

    // Limite de acciones del enunciado del Problem I (ICPC World Finals 2025).
    private static final int ACTION_LIMIT = 10000;

    /**
     * Maquina de n ruedas y n simbolos (n = cantidad de argumentos) donde la rueda w
     * muestra el simbolo numero visible[w-1] (0..n-1) del circulo comun.
     */
    private SlotMachine machineShowing(int... visible){
        int n = visible.length;
        List<String> colors = CssColors.distinctNames().subList(0, n);
        SlotMachine machine = new SlotMachine();
        for(int w = 1; w <= n; w++){
            machine.addWheel(w);
            for(int s = 1; s <= n; s++){
                machine.addSymbol(s, colors.get(s - 1));
            }
        }
        for(int w = 0; w < n; w++){
            machine.placeSymbol(w + 1, colors.get(visible[w]));
        }
        return machine;
    }

    // 1. Crear una maquina con igual numero de ruedas y simbolos (requisito 13)

    /* SlotMachine(n) arma n ruedas con los mismos n simbolos, todos de colores
     * distintos y en el mismo orden, y nunca nace ganada; con un n invalido falla. */
    @Test
    public void deberiaCrearUnaMaquinaDeNRuedasYNSimbolosDistintos(){
        for(int n = 3; n <= 10; n++){
            SlotMachine machine = new SlotMachine(n);
            assertTrue(machine.ok());
            assertEquals(n, machine.configuration().length);          // n ruedas
            assertEquals(n, machine.symbols().length);                // n simbolos en la rueda activa
            assertEquals(n, machine.distinctSymbols());               // y todos de colores distintos
            assertFalse("n=" + n, machine.isJackpot());               // no nace ganada

            // todas las ruedas tienen el mismo orden: si todas muestran el primer simbolo
            // y giran un paso, siguen coincidiendo en el segundo
            String[] ring = machine.symbols();
            for(int w = 1; w <= n; w++){
                machine.placeSymbol(w, ring[0]);
            }
            assertTrue(machine.isJackpot());
            for(int w = 1; w <= n; w++){
                machine.spin(w, 1);
            }
            assertTrue(machine.isJackpot());
            assertEquals(ring[1], machine.configuration()[0]);
            machine.exit();
        }
        assertFalse(new SlotMachine(0).ok());
        assertFalse(new SlotMachine(-4).ok());
    }

    // 2. Solucionar el problema de la maraton (requisito 14)

    /* Para cualquier n del enunciado (3 a 50), solve entrega las acciones {rueda, pasos}
     * que llevan una maquina aleatoria al jackpot sin pasarse del limite de acciones. */
    @Test
    public void deberiaSolucionarElProblemaDeLaMaratonParaCualquierN(){
        int maxActions = 0;
        for(int n = 3; n <= 50; n++){
            for(long seed = 0; seed < 5; seed++){
                int[][] actions = SlotMachineContest.solve(n, seed);
                assertTrue("n=" + n, actions.length > 0);
                assertTrue("n=" + n, actions.length <= ACTION_LIMIT);
                maxActions = Math.max(maxActions, actions.length);

                SlotMachine copy = new SlotMachine(n, seed);          // misma maquina que se resolvio
                assertFalse(copy.isJackpot());
                SlotMachineContest.play(copy, actions, 0);
                assertTrue("n=" + n + " semilla=" + seed, copy.isJackpot());
                copy.exit();
            }
        }
        assertTrue(maxActions <= ACTION_LIMIT);
    }

    /* La maquina se comporta como el juez del problema: reproduce las dos
     * interacciones de ejemplo del enunciado, incluidos los pasos negativos. */
    @Test
    public void deberiaResponderComoElJuezEnLosEjemplosDelEnunciado(){
        // Ejemplo 1: n = 5, la amiga responde 4, 3, 3, 3, 2, 1
        SlotMachine first = machineShowing(0, 1, 4, 4, 2);
        ContestTool tool = new ContestTool(first, 5);
        assertEquals(4, tool.distinctSymbols());
        assertEquals(3, tool.spin(1, 1));
        assertEquals(3, tool.spin(4, 2));
        assertEquals(3, tool.spin(3, 1));
        assertEquals(2, tool.spin(3, 1));
        assertEquals(1, tool.spin(5, 4));
        assertTrue(tool.solved());
        assertTrue(first.isJackpot());
        first.exit();

        // Ejemplo 2: n = 3, la amiga responde 3, 2, 2, 1
        SlotMachine second = machineShowing(0, 2, 1);
        tool = new ContestTool(second, 3);
        assertEquals(3, tool.distinctSymbols());
        assertEquals(2, tool.spin(2, -1));
        assertEquals(2, tool.spin(3, -1));
        assertEquals(1, tool.spin(2, -1));
        assertTrue(tool.solved());
        second.exit();
    }

    /* Una vez que todas las ruedas muestran lo mismo la partida termina:
     * la herramienta no acepta mas acciones. */
    @Test
    public void noDeberiaPermitirAccionesDespuesDeGanar(){
        SlotMachine machine = machineShowing(0, 1);
        ContestTool tool = new ContestTool(machine, 2);
        tool.spin(1, 1);
        assertTrue(tool.solved());
        try{
            tool.spin(2, 1);
            fail("debio rechazar la accion");
        }catch(IllegalStateException expected){
            assertEquals(1, tool.actions().length);                  // solo quedo registrada la primera
        }
        machine.exit();
    }

    // 3. Simular la solucion, si es posible (requisito 15)

    /* Solo se simula cuando la maquina cabe en pantalla; si no, no es posible.
     * (La simulacion visual se muestra en vivo con simulate(4).) */
    @Test
    public void deberiaSimularSoloCuandoLaMaquinaCabeEnPantalla(){
        Set<Integer> possible = new HashSet<Integer>();
        for(int n = 0; n <= 12; n++){
            if(SlotMachineContest.canSimulate(n)){
                possible.add(n);
            }
        }
        Set<Integer> expected = new HashSet<Integer>();
        for(int n = 2; n <= SlotMachineContest.MAX_SIMULATED_N; n++){
            expected.add(n);
        }
        assertEquals(expected, possible);
    }
}
