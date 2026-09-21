import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Clase de pruebas COLECTIVA del Ciclo 3, construida entre todo el curso
 * via wiki. Cada autor agrega sus casos con el prefijo de sus apellidos
 * (iniciales de los primeros apellidos + primera letra del segundo,
 * en orden alfabetico).
 *
 * Casos agregados por: Gomez / Rojas (prefijo: gomRoj)
 */
public class SlotMachineContestCTest{

    @Test
    public void gomRojDeberiaLlevarLaMaquinaAlJackpotAlRepetirLasAcciones(){
        for(int n = 2; n <= 5; n++){
            int[][] actions = SlotMachineContest.solve(n, 100L + n);
            SlotMachine copy = new SlotMachine(n, 100L + n);
            assertFalse(copy.isJackpot());
            SlotMachineContest.play(copy, actions, 0);
            assertTrue("n=" + n, copy.isJackpot());
            copy.exit();
        }
    }

    @Test
    public void gomRojNoDeberiaDarAccionesCuandoLaMaquinaTieneUnaSolaRueda(){
        assertEquals(0, SlotMachineContest.solve(1).length);
        assertTrue(new SlotMachine(1).isJackpot());
    }

    @Test
    public void gomRojNoDeberiaCrearUnaMaquinaConNInvalido(){
        SlotMachine machine = new SlotMachine(-1);
        assertFalse(machine.ok());
        assertEquals(0, SlotMachineContest.solve(-1).length);
    }
}