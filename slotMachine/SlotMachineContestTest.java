import static org.junit.Assert.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.Test;

/**
 * Pruebas de unidad del Ciclo 3: SlotMachine(n), distinctVisible(), ContestTool
 * y solve(n). Todas se corren con la maquina invisible: solve(n, semilla) resuelve
 * una maquina y las pruebas repiten sus acciones sobre otra maquina creada con la
 * misma semilla para comprobar que realmente se llega al jackpot.
 *
 * @author Juan Camilo Rojas / Rafael Ricardo Gomez
 */
public class SlotMachineContestTest{

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

    /** Repite las acciones que solve encontro para la semilla y dice si gana. */
    private boolean winsWhenReplayed(int n, long seed){
        int[][] actions = SlotMachineContest.solve(n, seed);
        SlotMachine copy = new SlotMachine(n, seed);
        SlotMachineContest.play(copy, actions, 0);
        boolean win = copy.isJackpot();
        copy.exit();
        return win;
    }

    // SlotMachine(n)

    @Test
    public void CreaNRuedasYNSimbolosDistintos(){
        SlotMachine machine = new SlotMachine(5);
        assertTrue(machine.ok());
        assertEquals(5, machine.configuration().length);
        assertEquals(5, machine.symbols().length);
        assertEquals(5, machine.distinctSymbols());
        machine.exit();
    }

    @Test
    public void TodasLasRuedasTienenLosMismosSimbolosEnElMismoOrden(){
        SlotMachine machine = new SlotMachine(4, 7L);
        String[] ring = machine.symbols();        // rueda activa: la ultima
        for(int w = 1; w <= 4; w++){
            machine.placeSymbol(w, ring[0]);
        }
        assertTrue(machine.isJackpot());
        for(int w = 1; w <= 4; w++){
            machine.spin(w, 1);
        }
        // si todas tienen el mismo orden, al avanzar un paso siguen coincidiendo
        assertTrue(machine.isJackpot());
        assertEquals(ring[1], machine.configuration()[0]);
        machine.exit();
    }

    @Test
    public void NoEmpiezaGanada(){
        for(int n = 2; n <= 6; n++){
            for(long seed = 0; seed < 200; seed++){
                SlotMachine machine = new SlotMachine(n, seed);
                assertFalse("n=" + n + " semilla=" + seed, machine.isJackpot());
                machine.exit();
            }
        }
    }

    @Test
    public void LaMismaSemillaCreaLaMismaMaquina(){
        SlotMachine one = new SlotMachine(6, 42L);
        SlotMachine other = new SlotMachine(6, 42L);
        assertArrayEquals(one.configuration(), other.configuration());
        assertArrayEquals(one.symbols(), other.symbols());
        one.exit();
        other.exit();
    }

    @Test
    public void FallaConUnNInvalido(){
        SlotMachine none = new SlotMachine(0);
        assertFalse(none.ok());
        SlotMachine tooMany = new SlotMachine(CssColors.distinctNames().size() + 1);
        assertFalse(tooMany.ok());
    }

    // distinctVisible

    @Test
    public void CuentaLosColoresVisiblesYNoLosDeLaRueda(){
        SlotMachine machine = new SlotMachine(3, 1L);
        String[] symbols = machine.symbols();
        machine.placeSymbol(1, symbols[0]);
        machine.placeSymbol(2, symbols[1]);
        machine.placeSymbol(3, symbols[2]);
        assertEquals(3, machine.distinctVisible());
        machine.placeSymbol(2, symbols[0]);
        assertEquals(2, machine.distinctVisible());
        machine.placeSymbol(3, symbols[0]);
        assertEquals(1, machine.distinctVisible());
        assertEquals(3, machine.distinctSymbols());
        machine.exit();
    }

    @Test
    public void DistinctVisibleFallaEnMaquinaVacia(){
        SlotMachine empty = new SlotMachine();
        assertEquals(0, empty.distinctVisible());
        assertFalse(empty.ok());
    }

    // CssColors.distinctNames

    @Test
    public void LosNombresDeColorNoRepitenTono(){
        List<String> names = CssColors.distinctNames();
        Set<Integer> tones = new HashSet<Integer>();
        for(String name : names){
            assertTrue(name, CssColors.isValid(name));
            assertTrue(name, tones.add(CssColors.toAwtColor(name).getRGB()));
        }
        assertTrue(names.contains("red"));
    }

    // ContestTool

    @Test
    public void LaHerramientaCuentaLasAccionesYReportaLosDistintos(){
        SlotMachine machine = new SlotMachine(3, 5L);
        ContestTool tool = new ContestTool(machine, 3);
        int before = tool.distinctSymbols();
        int after = tool.spin(1, 1);
        assertEquals(1, tool.actions().length);
        assertArrayEquals(new int[]{1, 1}, tool.actions()[0]);
        assertEquals(machine.distinctVisible(), after);
        assertTrue(Math.abs(after - before) <= 1);
        machine.exit();
    }

    @Test
    public void UnGiroCompletoNoSeEjecutaNiSeCuenta(){
        SlotMachine machine = new SlotMachine(4, 3L);
        ContestTool tool = new ContestTool(machine, 4);
        int before = tool.distinctSymbols();
        assertEquals(before, tool.spin(2, 4));
        assertEquals(0, tool.actions().length);
        machine.exit();
    }

    @Test
    public void LosPasosNegativosGiranHaciaAtras(){
        SlotMachine machine = new SlotMachine(4, 3L);
        ContestTool tool = new ContestTool(machine, 4);
        tool.spin(2, -1);
        assertArrayEquals(new int[]{2, 3}, tool.actions()[0]);
        machine.exit();
    }

    @Test(expected = IllegalStateException.class)
    public void NoAceptaAccionesDespuesDeGanar(){
        SlotMachine machine = new SlotMachine(2, 9L);     // 2 ruedas, 2 simbolos: siempre distintos
        ContestTool tool = new ContestTool(machine, 2);
        tool.spin(1, 1);                                   // ahora ambas muestran lo mismo
        assertTrue(tool.solved());
        tool.spin(2, 1);
    }

    // Ejemplos de interaccion del enunciado (la respuesta es lo que dice la amiga)

    @Test
    public void ReproduceElEjemploDeInteraccion1(){
        SlotMachine machine = machineShowing(0, 1, 4, 4, 2);   // dos ruedas muestran lo mismo: 4 distintos
        ContestTool tool = new ContestTool(machine, 5);
        assertEquals(4, tool.distinctSymbols());
        assertEquals(3, tool.spin(1, 1));
        assertEquals(3, tool.spin(4, 2));
        assertEquals(3, tool.spin(3, 1));
        assertEquals(2, tool.spin(3, 1));
        assertEquals(1, tool.spin(5, 4));
        assertTrue(tool.solved());
        machine.exit();
    }

    @Test
    public void ReproduceElEjemploDeInteraccion2ConPasosNegativos(){
        SlotMachine machine = machineShowing(0, 2, 1);
        ContestTool tool = new ContestTool(machine, 3);
        assertEquals(3, tool.distinctSymbols());
        assertEquals(2, tool.spin(2, -1));
        assertEquals(2, tool.spin(3, -1));
        assertEquals(1, tool.spin(2, -1));
        assertTrue(tool.solved());
        machine.exit();
    }

    // solve

    @Test
    public void ResuelveMaquinasPequenas(){
        for(int n = 2; n <= 6; n++){
            for(long seed = 0; seed < 100; seed++){
                assertTrue("n=" + n + " semilla=" + seed, winsWhenReplayed(n, seed));
            }
        }
    }

    @Test
    public void ResuelveLaMaquinaMasGrande(){
        int n = 50;
        int[][] actions = SlotMachineContest.solve(n, 2026L);
        assertTrue(actions.length <= ACTION_LIMIT);
        assertTrue(winsWhenReplayed(n, 2026L));
    }

    @Test
    public void ElPeorCasoConocidoDeN50CabeEnElLimite(){
        // Todas distintas y en el orden que obliga a la fase 2 a probar la mayor cantidad de ruedas.
        int n = 50;
        int[] visible = new int[n];
        for(int w = 1; w < n; w++){
            visible[w] = n - w;
        }
        SlotMachine machine = machineShowing(visible);
        ContestTool tool = new ContestTool(machine, n);
        new ContestSolver(tool, n).solve();
        assertTrue(machine.isJackpot());
        assertTrue(tool.actions().length <= ACTION_LIMIT);
        machine.exit();
    }

    @Test
    public void ElJackpotSeAlcanzaConLaUltimaAccion(){
        // Ninguna accion sobra: la maquina no esta ganada antes de la ultima.
        int n = 5;
        int[][] actions = SlotMachineContest.solve(n, 11L);
        SlotMachine copy = new SlotMachine(n, 11L);
        for(int i = 0; i < actions.length - 1; i++){
            copy.spin(actions[i][0], actions[i][1]);
            assertFalse("accion " + i, copy.isJackpot());
        }
        copy.spin(actions[actions.length - 1][0], actions[actions.length - 1][1]);
        assertTrue(copy.isJackpot());
        copy.exit();
    }

    @Test
    public void LasAccionesSonParesRuedaPasosValidos(){
        int n = 8;
        for(int[] action : SlotMachineContest.solve(n, 4L)){
            assertEquals(2, action.length);
            assertTrue(action[0] >= 1 && action[0] <= n);
            assertTrue(action[1] >= 1 && action[1] < n);
        }
    }

    @Test
    public void NoHayAccionesSiNEsUnoOEsInvalido(){
        assertEquals(0, SlotMachineContest.solve(1).length);
        assertEquals(0, SlotMachineContest.solve(0).length);
        assertEquals(0, SlotMachineContest.solve(-2).length);
    }

    // simulate

    @Test
    public void SoloSeSimulaSiLaMaquinaCabeEnPantalla(){
        assertFalse(SlotMachineContest.canSimulate(1));
        assertTrue(SlotMachineContest.canSimulate(2));
        assertTrue(SlotMachineContest.canSimulate(SlotMachineContest.MAX_SIMULATED_N));
        assertFalse(SlotMachineContest.canSimulate(SlotMachineContest.MAX_SIMULATED_N + 1));
    }
}