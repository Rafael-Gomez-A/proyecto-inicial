import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Clase de pruebas COLECTIVA del Ciclo 2, construida entre todo el curso
 * via wiki. Cada autor agrega sus casos con el prefijo de sus apellidos
 * (iniciales de los primeros apellidos + primera letra del segundo,
 * en orden alfabético).
 *
 * Casos agregados por: Gomez / Rojas (prefijo: gomRoj)
 */
public class SlotMachineCC2Test{

    private SlotMachine machine;

    @Before
    public void setUp(){
        machine = new SlotMachine();
        for(int w = 1; w <= 3; w++){
            machine.addWheel(w);
            machine.addSymbol(1, "red");
            machine.addSymbol(2, "green");
            machine.addSymbol(3, "blue");
        }
    }

    @Test
    public void gomRojVolverALaConfiguracionOriginalAlIntercambiarElMismoPar(){
        String[] original = machine.configuration();
        machine.swap(1, 2);
        machine.swap(1, 2);
        assertTrue(machine.ok());
        assertArrayEquals(original, machine.configuration());
    }

    @Test
    public void gomRojNoDeberiaAplicarConfigCuandoUnColorEsInvalido(){
        machine.placeSymbol(1, "blue");
        String[] target = {"red", "purple", "green"}; // "purple" no existe en rueda 2
        machine.spin(target);
        assertFalse(machine.ok());
        assertEquals("blue", machine.configuration()[0]); // no debio cambiar
    }

    @After
    public void clean(){
        machine.exit();
    }
}