import java.util.ArrayList;
import java.util.List;

/**
 * La herramienta de pruebas (testing tool) de la maraton: es la unica via por la
 * que un solver puede tocar una SlotMachine. Ofrece exactamente lo que ofrece
 * el juez de la maraton: girar una rueda y saber cuantos simbolos distintos
 * hay visibles. No permite consultar colores, la configuracion ni el jackpot.
 *
 * Ademas lleva la cuenta de las acciones realizadas, que es la respuesta de
 * solve(n), y sabe cuando la partida termino (todas las ruedas muestran el
 * mismo simbolo), momento desde el cual no acepta mas acciones.
 *
 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 3)
 */
public class ContestTool{

    private final SlotMachine machine;
    private final int n;
    private final List<int[]> actions;
    private int lastDistinct;

    /**
     * Crea la herramienta sobre una maquina de n ruedas y n simbolos
     * (la creada con SlotMachine(n)).
     */
    public ContestTool(SlotMachine machine, int n){
        this.machine = machine;
        this.n = n;
        this.actions = new ArrayList<int[]>();
        this.lastDistinct = machine.distinctVisible();
    }

    /**
     * Gira la rueda (1..n) la cantidad de posiciones dada hacia adelante; un
     * valor negativo equivale a girar hacia atras. Cuenta como una accion.
     * Un giro que no mueve la rueda (multiplo de n) no se ejecuta ni se cuenta.
     * @return la cantidad de simbolos distintos visibles tras la accion.
     * @throws IllegalStateException si la partida ya termino o la maquina rechaza la accion.
     */
    public int spin(int wheel, int steps){
        if(solved()){
            throw new IllegalStateException("La partida ya termino: no se permiten mas acciones.");
        }
        int normalized = ((steps % n) + n) % n;
        if(normalized == 0){
            return lastDistinct;
        }
        machine.spin(wheel, normalized);
        if(!machine.ok()){
            throw new IllegalStateException("La maquina rechazo la accion (" + wheel + ", " + normalized + ").");
        }
        actions.add(new int[]{wheel, normalized});
        lastDistinct = machine.distinctVisible();
        return lastDistinct;
    }

    /**
     * Retorna cuantos simbolos distintos hay visibles ahora (sin gastar una accion).
     */
    public int distinctSymbols(){
        return lastDistinct;
    }

    /**
     * Indica si la partida termino, es decir, si todas las ruedas muestran el mismo simbolo.
     */
    public boolean solved(){
        return lastDistinct == 1;
    }

    /**
     * Retorna las acciones realizadas hasta ahora, en orden, como pares {rueda, pasos}.
     */
    public int[][] actions(){
        return actions.toArray(new int[0][]);
    }
}