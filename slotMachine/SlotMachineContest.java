import java.awt.GraphicsEnvironment;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 * Punto de entrada del problema de la maraton (Problem I, 2025 Slot Machine).
 * Solo coordina: crea la maquina, se la entrega al solver a traves de una
 * ContestTool y, para simular, repite las acciones sobre una copia visible.
 *
 *  - SlotMachine simula la maquina y hace de testing tool; no resuelve nada.
 *  - ContestTool es la unica ventana del solver hacia la maquina.
 *  - ContestSolver contiene el algoritmo.
 *
 * La maquina permanece invisible en solve(n) y visible en simulate(n).
 *
 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 3)
 */
public class SlotMachineContest{

    /**
     * Mayor n que cabe en el canvas de 480 x 320: cada rueda ocupa 60 de ancho
     * (empezando en x = 40) y n simbolos de 28 de alto.
     */
    public static final int MAX_SIMULATED_N = 7;

    private static final int PAUSE_MILLIS = 600;

    private SlotMachineContest(){
        // clase de utilidad: no se instancia
    }

    /**
     * Resuelve una maquina de n ruedas y n simbolos inicializada aleatoriamente
     * (invisible) y retorna las acciones {rueda, pasos} que la llevan al jackpot.
     * Retorna un arreglo vacio si n es invalido o si la maquina ya esta ganada (n = 1).
     * El enunciado de la maraton usa 3 <= n <= 50; aqui se acepta cualquier n valido.
     */
    public static int[][] solve(int n){
        return solve(n, new Random().nextLong());
    }

    /**
     * Simula visualmente la solucion: resuelve una maquina invisible y luego
     * repite las mismas acciones, con pausas, sobre una copia visible con la
     * misma configuracion inicial. Si n no cabe en pantalla no simula.
     */
    public static void simulate(int n){
        if(!canSimulate(n)){
            if(!GraphicsEnvironment.isHeadless()){
                JOptionPane.showMessageDialog(null,
                    "Solo se puede simular con n entre 2 y " + MAX_SIMULATED_N + ".",
                    "Slot Machine", JOptionPane.WARNING_MESSAGE);
            }
            return;
        }
        long seed = new Random().nextLong();
        int[][] actions = solve(n, seed);
        SlotMachine machine = new SlotMachine(n, seed);
        machine.makeVisible();
        play(machine, actions, PAUSE_MILLIS);
    }

    /**
     * Indica si simulate(n) puede mostrar la maquina completa en el canvas.
     */
    public static boolean canSimulate(int n){
        return n >= 2 && n <= MAX_SIMULATED_N;
    }

    /**
     * Resuelve la maquina que produce la semilla dada.
     */
    static int[][] solve(int n, long seed){
        SlotMachine machine = new SlotMachine(n, seed);
        if(!machine.ok()){
            return new int[0][];
        }
        int[][] actions = new ContestSolver(new ContestTool(machine, n), n).solve();
        machine.exit();
        return actions;
    }

    /**
     * Aplica las acciones a la maquina en orden, esperando pauseMillis entre una
     * y otra si la maquina es visible (0 para no esperar).
     */
    static void play(SlotMachine machine, int[][] actions, int pauseMillis){
        for(int[] action : actions){
            machine.spin(action[0], action[1]);
            if(pauseMillis > 0){
                Canvas.getCanvas().wait(pauseMillis);
            }
        }
    }
}