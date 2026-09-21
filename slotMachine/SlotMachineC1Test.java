import static org.junit.Assert.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Pruebas de ACEPTACION del Ciclo 1 de SlotMachine (addWheel, delWheel,
 * addSymbol, delSymbol, placeSymbol, spin, symbols, distinctSymbols,
 * configuration, isJackpot, exit y ok). Cada prueba es un escenario
 * completo visto desde el usuario; se ejecutan con la maquina invisible
 * para no abrir dialogos (JOptionPane).
 *
 * @author Juan Camilo Rojas / Rafael Ricardo Gomez
 */
public class SlotMachineC1Test{

    private SlotMachine machine;

    @Before
    public void setUp(){
        machine = new SlotMachine();
    }

    @After
    public void tearDown(){
        machine.exit();
    }

    /** Agrega 3 ruedas [red, green, blue] (visible "red" en cada una). */
    private void threeWheels(){
        for(int w = 1; w <= 3; w++){
            machine.addWheel(w);
            machine.addSymbol(1, "red");
            machine.addSymbol(2, "green");
            machine.addSymbol(3, "blue");
        }
    }

    private String colorOf(int wheel){
        return machine.configuration()[wheel - 1];
    }

    // 1. Armar la maquina

    /* Las ruedas se insertan en la posicion pedida (ajustando las fuera de
     * rango) y la ultima rueda agregada queda como rueda activa. */
    @Test
    public void deberiaAgregarRuedasEnLaPosicionIndicada(){
        assertTrue(machine.ok());
        machine.addWheel(1);  machine.addSymbol(1, "red");
        machine.addWheel(1);  machine.addSymbol(1, "blue");     // entra al frente
        assertArrayEquals(new String[]{"blue", "red"}, machine.configuration());

        machine.addWheel(99); machine.addSymbol(1, "green");    // 99 -> al final
        assertArrayEquals(new String[]{"blue", "red", "green"}, machine.configuration());

        machine.addWheel(0);                                    // 0 -> al frente, vacia
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"", "blue", "red", "green"}, machine.configuration());
    }

    // 2. Simbolos de la rueda activa

    /* Los simbolos se insertan en orden, se ajusta la posicion, se aceptan
     * repetidos y se normalizan mayusculas y espacios. */
    @Test
    public void deberiaAgregarSimbolosAlaRuedaActiva(){
        machine.addWheel(1);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "green");
        machine.addSymbol(1, "blue");
        machine.addSymbol(99, "red");                           // 99 -> al final
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"blue", "red", "green", "red"}, machine.symbols());
        assertEquals(3, machine.distinctSymbols());             // "red" repetido cuenta una vez

        machine.addSymbol(0, " GOLD ");                         // 0 -> posicion 1
        assertTrue(machine.ok());
        assertEquals("gold", machine.symbols()[0]);
        assertEquals(4, machine.distinctSymbols());
    }

    /* Sin rueda activa, con color inexistente o nulo, la operacion falla
     * y no cambia nada. */
    @Test
    public void noDeberiaAgregarSimbolosInvalidos(){
        machine.addSymbol(1, "red");                            // no hay rueda activa
        assertFalse(machine.ok());

        machine.addWheel(1);
        machine.addSymbol(1, "notacolor");
        assertFalse(machine.ok());
        machine.addSymbol(1, null);
        assertFalse(machine.ok());

        assertEquals(0, machine.symbols().length);
        assertTrue(machine.ok());                               // consultar no es una falla
        assertEquals(0, machine.distinctSymbols());
    }

    // 3. Quitar simbolos

    /* delSymbol quita el PRIMER simbolo de ese color, no cambia cual esta
     * visible, y falla si el color no esta en la rueda. */
    @Test
    public void deberiaQuitarSimbolosDeLaRuedaActiva(){
        machine.addWheel(1);
        machine.addSymbol(1, "red");
        machine.addSymbol(2, "green");
        machine.addSymbol(3, "blue");
        machine.addSymbol(4, "green");
        machine.placeSymbol(1, "blue");

        machine.delSymbol("green");
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.symbols());
        assertEquals("blue", colorOf(1));                       // el visible sigue siendo blue

        machine.delSymbol("gold");                              // no existe en la rueda
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"red", "blue", "green"}, machine.symbols());

        machine.delSymbol("green");
        machine.delSymbol("red");
        machine.delSymbol("blue");                              // la rueda queda vacia
        assertTrue(machine.ok());
        assertEquals(0, machine.symbols().length);
        assertEquals("", colorOf(1));
        assertFalse(machine.isJackpot());
    }

    // 4. Quitar ruedas

    /* delWheel ajusta la posicion; si borra la rueda activa, las
     * operaciones de la rueda activa fallan; sin ruedas no se puede borrar. */
    @Test
    public void deberiaQuitarRuedas(){
        machine.addWheel(1); machine.addSymbol(1, "red");
        machine.addWheel(2); machine.addSymbol(1, "green");
        machine.addWheel(3); machine.addSymbol(1, "blue");      // rueda activa

        machine.delWheel(2);
        assertTrue(machine.ok());
        assertArrayEquals(new String[]{"red", "blue"}, machine.configuration());

        machine.delWheel(99);                                   // 99 -> la ultima (la activa)
        assertArrayEquals(new String[]{"red"}, machine.configuration());
        assertEquals(0, machine.symbols().length);              // ya no hay rueda activa
        assertFalse(machine.ok());

        machine.delWheel(0);                                    // 0 -> la primera
        assertTrue(machine.ok());
        machine.delWheel(1);                                    // ya no quedan ruedas
        assertFalse(machine.ok());
    }

    // 5. Forzar simbolos y jackpot

    /* placeSymbol fuerza el visible (sin distinguir mayusculas), ajusta la
     * rueda fuera de rango y falla si el color no existe; isJackpot es
     * verdadero solo cuando todos los visibles coinciden. */
    @Test
    public void deberiaForzarSimbolosYDetectarElJackpot(){
        threeWheels();
        assertTrue(machine.isJackpot());                        // las 3 muestran red

        machine.placeSymbol(2, "GREEN");
        assertEquals("green", colorOf(2));
        assertFalse(machine.isJackpot());

        machine.placeSymbol(1, "green");
        machine.placeSymbol(99, "green");                       // 99 -> rueda 3
        assertTrue(machine.isJackpot());

        machine.placeSymbol(0, "blue");                         // 0 -> rueda 1
        assertArrayEquals(new String[]{"blue", "green", "green"}, machine.configuration());

        machine.placeSymbol(2, "gold");                         // gold no existe en la rueda 2
        assertFalse(machine.ok());
        assertArrayEquals(new String[]{"blue", "green", "green"}, machine.configuration());
    }

    /* Sin ruedas, o con ruedas vacias, nunca hay jackpot. */
    @Test
    public void noDeberiaHaberJackpotSinSimbolos(){
        assertFalse(machine.isJackpot());                       // sin ruedas
        machine.addWheel(1);
        machine.addWheel(2);
        assertFalse(machine.isJackpot());                       // ruedas vacias
    }

    // 6. Girar al azar

    /* spin(wheel) es aleatorio y siempre deja un simbolo de esa rueda;
     * spin() gira todas las ruedas. */
    @Test
    public void deberiaGirarAlAzarSoloConSimbolosDeLaRueda(){
        threeWheels();
        List<String> valid = Arrays.asList("red", "green", "blue");

        Set<String> seen = new HashSet<String>();
        for(int i = 0; i < 100; i++){
            machine.spin(1);
            assertTrue(machine.ok());
            seen.add(colorOf(1));
        }
        assertEquals(3, seen.size());                           // salieron los 3 colores

        for(int i = 0; i < 30; i++){
            machine.spin();
            assertTrue(machine.ok());
            for(String color : machine.configuration()){
                assertTrue(valid.contains(color));
            }
        }
    }

    /* Con una sola opcion por rueda el giro es predecible: siempre jackpot. */
    @Test
    public void giroConUnSoloSimboloPorRuedaSiempreEsJackpot(){
        for(int w = 1; w <= 3; w++){
            machine.addWheel(w);
            machine.addSymbol(1, "gold");
        }
        machine.spin();
        assertTrue(machine.ok());
        assertTrue(machine.isJackpot());
    }

    /* Girar falla sin ruedas o sin simbolos; spin() basta con que una
     * rueda tenga simbolos. */
    @Test
    public void noDeberiaGirarSinRuedasNiSimbolos(){
        machine.spin();
        assertFalse(machine.ok());                              // sin ruedas

        machine.addWheel(1);
        machine.spin(1);
        assertFalse(machine.ok());                              // rueda vacia
        machine.spin();
        assertFalse(machine.ok());                              // ninguna rueda con simbolos

        machine.addWheel(2);
        machine.addSymbol(1, "red");
        machine.spin();
        assertTrue(machine.ok());                               // una rueda si tiene simbolos
        assertArrayEquals(new String[]{"", "red"}, machine.configuration());
    }

    // 7. Terminar la maquina

    /* Despues de exit() la maquina rechaza todo y exit() no se repite. */
    @Test
    public void despuesDeExitNoDeberiaAceptarOperaciones(){
        threeWheels();
        machine.exit();
        assertTrue(machine.ok());

        machine.addWheel(1);                        assertFalse(machine.ok());
        machine.delWheel(1);                        assertFalse(machine.ok());
        machine.addSymbol(1, "red");                assertFalse(machine.ok());
        machine.delSymbol("red");                   assertFalse(machine.ok());
        machine.placeSymbol(1, "red");              assertFalse(machine.ok());
        machine.spin();                             assertFalse(machine.ok());
        machine.spin(1);                            assertFalse(machine.ok());
        assertEquals(0, machine.symbols().length);  assertFalse(machine.ok());
        assertEquals(0, machine.distinctSymbols()); assertFalse(machine.ok());
        assertEquals(0, machine.configuration().length);
        assertFalse(machine.isJackpot());
        machine.exit();                             assertFalse(machine.ok());
    }
}
