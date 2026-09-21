import java.util.ArrayList;
import java.util.List;

/**
 * Resuelve el problema de la maraton (Slot Machine) usando solo una ContestTool:
 * gira ruedas y observa cuantos simbolos distintos hay visibles. Nunca ve un color.
 *
 * Modelo: n ruedas con los mismos n simbolos en el mismo orden circular; solo se
 * desconoce cual simbolo muestra cada una. Como los simbolos son distintos, cada
 * rueda esta en una posicion del circulo y "simbolos distintos visibles" equivale
 * a "posiciones distintas ocupadas". Se gana cuando todas ocupan la misma.
 *
 * Estrategia, en tres fases:
 *  1. separate(): deja todas las ruedas mostrando simbolos distintos (respuesta n).
 *  2. discoverOrder(): descubre cuantos puestos a la derecha de la rueda 1 esta cada rueda.
 *  3. align(): con ese orden, lleva todas las ruedas al simbolo que muestra la rueda 1.
 *
 * Con n = 50 no pasa de unas 5000 acciones (cota); el peor caso construido usa
 * 2499 y el enunciado permite 10000 (ver SlotMachineContestTest).
 *
 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 3)
 */
public class ContestSolver{

    private final ContestTool tool;
    private final int n;

    /**
     * Crea un solver para la maquina de n ruedas que expone la herramienta dada.
     */
    public ContestSolver(ContestTool tool, int n){
        this.tool = tool;
        this.n = n;
    }

    /**
     * Juega hasta ganar.
     * @return las acciones realizadas, en orden, como pares {rueda, pasos}.
     */
    public int[][] solve(){
        if(n > 1 && !tool.solved()){
            separate();
            if(!tool.solved()){
                int[] order = discoverOrder();
                if(!tool.solved()){
                    align(order);
                }
            }
        }
        return tool.actions();
    }

    /**
     * Fase 1. Recorre las ruedas una a una y le da a cada una un simbolo que
     * ninguna otra muestra. Una rueda con simbolo unico sigue siendo unica
     * cuando se mueven las demas, asi que al terminar todas lo son (respuesta n).
     */
    private void separate(){
        for(int wheel = 1; wheel <= n && !tool.solved() && tool.distinctSymbols() < n; wheel++){
            makeUnique(wheel);
        }
    }

    /**
     * Gira la rueda de a un paso mirando la respuesta: si sube, la rueda paso de
     * repetida a unica; si baja, ya era unica y el ultimo paso la arruino, y se
     * deshace; si no cambia, su condicion sigue igual y se sigue girando.
     */
    private void makeUnique(int wheel){
        int before = tool.distinctSymbols();
        for(int turn = 1; turn < n; turn++){
            int now = tool.spin(wheel, 1);
            if(tool.solved() || now > before){
                return;
            }
            if(now < before){
                tool.spin(wheel, -1);
                return;
            }
        }
        throw new IllegalStateException("No se pudo separar la rueda " + wheel + ".");
    }

    /**
     * Fase 2. Parte de que todas las ruedas muestran simbolos distintos. Avanza la
     * rueda 1 un puesto por vez: cada avance la hace chocar con la rueda que estaba
     * un puesto adelante (respuesta n-1) y deja libre el puesto que ella ocupaba.
     * Esa rueda se identifica en findCollidingWheel(). Al terminar, la rueda 1 esta
     * en el puesto n-1 y la rueda order[i] esta en el puesto i (habiendo estado
     * i+1 puestos adelante de la rueda 1 al comenzar).
     * @return las ruedas 2..n ordenadas por su distancia a la rueda 1.
     */
    private int[] discoverOrder(){
        List<Integer> candidates = new ArrayList<Integer>();
        for(int wheel = 2; wheel <= n; wheel++){
            candidates.add(wheel);
        }
        int[] order = new int[n - 1];
        for(int i = 0; i < order.length && !tool.solved(); i++){
            tool.spin(1, 1);
            if(!tool.solved()){
                order[i] = findCollidingWheel(candidates);
            }
        }
        return order;
    }

    /**
     * Encuentra, entre las ruedas aun sin identificar, la que choco con la rueda 1.
     * Retroceder un puesto a esa rueda llena el puesto libre y la respuesta vuelve
     * a n; retroceder a cualquier otra deja la respuesta en n-2 y se deshace.
     * Si solo queda una candidata, es esa. La rueda encontrada queda retrocedida
     * un puesto y se saca de la lista. (Si al probar se gana, la partida termina
     * y el resultado ya no importa.)
     */
    private int findCollidingWheel(List<Integer> candidates){
        if(candidates.size() == 1){
            int last = candidates.remove(0);
            tool.spin(last, -1);
            return last;
        }
        for(int i = 0; i < candidates.size(); i++){
            int wheel = candidates.get(i);
            int answer = tool.spin(wheel, -1);
            if(tool.solved() || answer == n){
                candidates.remove(i);
                return wheel;
            }
            tool.spin(wheel, 1);
        }
        throw new IllegalStateException("Ninguna rueda coincide con la rueda 1.");
    }

    /**
     * Fase 3. La rueda 1 esta en el puesto n-1 y la rueda order[i] en el puesto i:
     * girar cada una n-1-i pasos las deja a todas en el puesto de la rueda 1.
     */
    private void align(int[] order){
        for(int i = 0; i < order.length && !tool.solved(); i++){
            tool.spin(order[i], n - 1 - i);
        }
    }
}