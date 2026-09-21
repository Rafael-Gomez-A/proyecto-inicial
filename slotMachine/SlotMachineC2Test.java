import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de unidad del Ciclo 2: swap, lock/unlock, spin(wheel,steps)
 * y spin(setSymbols), las pruebas se corren invisibles
 *
 * @author Juan Camilo Rojas / Rafael Ricardo Gomez
 */
public class SlotMachineC2Test{

    private SlotMachine machine;

    @Before
    public void setUp(){
        machine = new SlotMachine();
        // 3 ruedas, cada una con 3 simbolos distintos
        for(int w = 1; w <= 3; w++){
            machine.addWheel(w);
            machine.addSymbol(1, "red");
            machine.addSymbol(2, "green");
            machine.addSymbol(3, "blue");
        }
        // Nota activeWheel queda en la ultima rueda creada no afecta
        // a los metodos nuevos porque todos reciben el indice de rueda.
    }

    // swap 

    @Test
    public void SwapTwoWheels(){
        machine.placeSymbol(1, "red");
        machine.placeSymbol(2, "green");
        machine.swap(1, 2);
        assertTrue(machine.ok());
        String[] config = machine.configuration();
        assertEquals("green", config[0]);
        assertEquals("red", config[1]);
    }

    @Test
    public void AjustarPosicionesFueraDeRangoSwap(){
        machine.swap(0, 99);
        assertTrue(machine.ok());
    }

    @Test
    public void FallaAlIntercambiarEnMaquinaVacia(){
        SlotMachine empty = new SlotMachine();
        empty.swap(1, 2);
        assertFalse(empty.ok());
    }

    // lock / unlock

    @Test
    public void LockAWheel(){
        machine.lock(1);
        assertTrue(machine.ok());
    }

    @Test
    public void NoDejaGirarUnaRuedaFijada(){
        machine.lock(1);
        machine.spin(1, 2);
        assertFalse(machine.ok());
    }

    @Test
    public void PermiteGirarDespuesDeUnlock(){
        machine.lock(1);
        machine.unlock(1);
        machine.spin(1, 1);
        assertTrue(machine.ok());
    }

    // spin(wheel, steps)

    @Test
    public void AvanzaLaRuedaLosPasosIndicados(){
        machine.placeSymbol(1, "red");
        machine.spin(1, 1);
        assertTrue(machine.ok());
        assertEquals("green", machine.configuration()[0]);
    }

    @Test
    public void FallaSpinStepsConUnaRuedaVacia(){
        SlotMachine oneEmptyWheel = new SlotMachine();
        oneEmptyWheel.addWheel(1);
        oneEmptyWheel.spin(1, 2);
        assertFalse(oneEmptyWheel.ok());
    }

    // spin(setSymbols)

    @Test
    public void deberiaEstablecerLaConfiguracion(){
        String[] target = {"blue", "red", "green"};
        machine.spin(target);
        assertTrue(machine.ok());
        assertArrayEquals(target, machine.configuration());
    }

    @Test
    public void DetectaJackpotDespuesDeEstablecerConfiguracion(){
        String[] target = {"red", "red", "red"};
        machine.spin(target);
        assertTrue(machine.ok());
        assertTrue(machine.isJackpot());
    }

    @Test
    public void fallaAplicarConfiguracionCuandoUnColorNoExiste(){
        String[] target = {"red", "purple", "green"}; // "purple" no existe en rueda 2
        machine.placeSymbol(1, "blue");
        machine.spin(target);
        assertFalse(machine.ok());
        // La rueda 1 no debe haber cambiado
        assertEquals("blue", machine.configuration()[0]);
    }

    @After
    public void tearDown(){
        machine.exit();
    }
}