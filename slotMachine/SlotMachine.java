import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.swing.JOptionPane;

/**
 *   La máquina mantiene una referencia a una rueda activa: la última rueda   
 *   añadida mediante addWheel(int) Los métodos addSymbol, delSymbol, 
 *   symbols y distinctSymbols operan sobre dicha rueda activa.  
 *   Por el contrario, placeSymbol y spin(wheel) reciben un índice de rueda explícito  
 *   y pueden operar sobre cualquier rueda
 *   Los símbolos se identifican mediante nombres de colores CSS  
 *   Es posible que dos símbolos del mismo color coexistan en una misma rueda por eso  
 *   distinctSymbols() debe existir
 *   spin() funciona de forma aleatoria, al igual que una máquina tragamonedas real. 
 *   placeSymbol() permite forzar que un símbolo específico sea el visible en una rueda,
 *   esto para hacer pruebas y tener una configuración ganadora sin depender del azar.  
 *   isJackpot() devuelve verdadero cuando la máquina tiene al menos una rueda y 
 *   el símbolo visible de todas las ruedas es del mismo color. 

 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 1)
 */
public class SlotMachine{

    private static final int WHEEL_SPACING = 60;
    private static final int MARGIN_LEFT = 40;

    private List<Wheel> wheels;
    private Wheel activeWheel;
    private boolean visible;
    private boolean ended;
    private boolean ok;
    private Random random;

    /**
     * Crea una nueva maquina vacia.
     */
    public SlotMachine(){
        wheels = new ArrayList<Wheel>();
        activeWheel = null;
        visible = false;
        ended = false;
        ok = true;
        random = new Random();
    }

    /**
     * Agrega una nueva rueda vacia en una posicionm asignada y la vuelve
     * en la rueda activa.
     */
    public void addWheel(int pos){
        if(!beginOperation()){
            return;
        }
        int validPos = clampForInsert(pos, wheels.size());
        Wheel wheel = new Wheel();
        wheels.add(validPos - 1, wheel);
        activeWheel = wheel;
        redraw();
    }

    /**
     * Remueve la rueda en la posicion asignada
     */
    public void delWheel(int pos){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas para eliminar.");
            return;
        }
        int validPos = clampForExisting(pos, wheels.size());
        Wheel removed = wheels.remove(validPos - 1);
        removed.makeInvisible();
        if(removed == activeWheel){
            activeWheel = null;
        }
        redraw();
    }

    /**
     * Agrega un simbolo a la rueda activa en la posicion asignada.
     */
    public void addSymbol(int pos, String color){
        if(!beginOperation()){
            return;
        }
        if(activeWheel == null){
            fail("Debe existir una rueda activa. Cree una rueda primero con addWheel.");
            return;
        }
        if(!CssColors.isValid(color)){
            fail("'" + color + "' no es un nombre de color CSS valido.");
            return;
        }
        int validPos = clampForInsert(pos, activeWheel.size());
        activeWheel.addSymbol(validPos, color.trim().toLowerCase());
        redraw();
    }

    /**
     * Remueve el primer simbolo del color indicado de la rueda activa.
     */
    public void delSymbol(String symbol){
        if(!beginOperation()){
            return;
        }
        if(activeWheel == null){
            fail("Debe existir una rueda activa.");
            return;
        }
        String key = symbol == null ? null : symbol.trim().toLowerCase();
        if(!activeWheel.removeSymbol(key)){
            fail("El simbolo '" + symbol + "' no existe en la rueda activa.");
            return;
        }
        redraw();
    }

    /**
     * Fuerza a un simbolo ya existente de una rueda a comvertirse
     * en el visible.
     */
    public void placeSymbol(int wheel, String symbol){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int validPos = clampForExisting(wheel, wheels.size());
        Wheel target = wheels.get(validPos - 1);
        String key = symbol == null ? null : symbol.trim().toLowerCase();
        if(!target.placeSymbol(key)){
            fail("El simbolo '" + symbol + "' no existe en la rueda " + validPos + ".");
            return;
        }
        redraw();
    }

    /**
     * Gira la rueda indicada, marcando uno de sus simbolos al azar.
     * Si la rueda esta fija (lock) la operacion falla.
     */
    public void spin(int wheel){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int validPos = clampForExisting(wheel, wheels.size());
        Wheel target = wheels.get(validPos - 1);
        if(target.isLocked()){
            fail("La rueda " + validPos + " esta fija (lock); libere la rueda antes de girarla.");
            return;
        }
        if(!target.spin(random)){
            fail("La rueda " + validPos + " no tiene simbolos para girar.");
            return;
        }
        redraw();
    }

    /**
     * Gira todas las ruedas de la maquina. Las ruedas fijas (lock) se omiten
     * y conservan su simbolo; si ninguna rueda pudo girar la operacion falla.
     */
    public void spin(){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        boolean anySpun = false;
        for(Wheel wheel : wheels){
            if(wheel.isLocked()){
                continue;
            }
            anySpun = wheel.spin(random) || anySpun;
        }
        if(!anySpun){
            fail("Ninguna rueda pudo girar: todas estan fijas (lock) o no tienen simbolos.");
            return;
        }
        redraw();
    }

    /**
     * Retorna el tamaño y los colores de los simbolos en orden de la rueda activa 
     * empezando en 1
     */
    public String[] symbols(){
        if(!beginOperation()){
            return new String[0];
        }
        if(activeWheel == null){
            fail("No hay una rueda activa.");
            return new String[0];
        }
        return activeWheel.getSymbols();
    }

    /**
     * Retorna la cantidad de simbolos con color distinto en la rueda activa
     */
    public int distinctSymbols(){
        if(!beginOperation()){
            return 0;
        }
        if(activeWheel == null){
            fail("No hay una rueda activa.");
            return 0;
        }
        return activeWheel.distinctCount();
    }

    /**
     * Retorna el color marcado en cada rueda de izquierda a derecha.
     */
    public String[] configuration(){
        if(!beginOperation()){
            return new String[0];
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return new String[0];
        }
        return currentConfiguration();
    }

    /**
     * Retorna True, cuando todos los colores marcados en las ruedas
     * sean iguales.
     */
    public boolean isJackpot(){
        if(!beginOperation()){
            return false;
        }
        if(wheels.isEmpty()){
            return false;
        }
        return jackpotFor(currentConfiguration());
    }

    /**
     * Hace visible la maquina.
     */
    public void makeVisible(){
        if(!beginOperation()){
            return;
        }
        visible = true;
        redraw();
    }

    /**
     * Oculta la maquina, pero esta sigue operacional.
     */
    public void makeInvisible(){
        if(!beginOperation()){
            return;
        }
        visible = false;
        for(Wheel wheel : wheels){
            wheel.makeInvisible();
        }
    }

    /**
     * Pone fin a la maquina, oculta todo y no permite que se realicen mas operaciones.
     */
    public void exit(){
        ok = true;
        if(ended){
            fail("El simulador ya fue terminado.");
            return;
        }
        for(Wheel wheel : wheels){
            wheel.makeInvisible();
        }
        ended = true;
        visible = false;
    }

    /**
     * Retorna segun el resultado de la operacion anterior
     */
    public boolean ok(){
        return ok;
    }

    private String[] currentConfiguration(){
        String[] config = new String[wheels.size()];
        for(int i = 0; i < wheels.size(); i++){
            String color = wheels.get(i).getVisibleColor();
            config[i] = color == null ? "" : color;
        }
        return config;
    }

    private boolean jackpotFor(String[] config){
        if(config.length == 0){
            return false;
        }
        String first = config[0];
        if(first.isEmpty()){
            return false;
        }
        for(String color : config){
            if(!first.equals(color)){
                return false;
            }
        }
        return true;
    }

    private void redraw(){
        if(!visible){
            for(Wheel wheel : wheels){
                wheel.makeInvisible();
            }
            return;
        }
        boolean jackpot = !wheels.isEmpty() && jackpotFor(currentConfiguration());
        int x = MARGIN_LEFT;
        for(Wheel wheel : wheels){
            wheel.setHighlighted(jackpot);
            wheel.reposition(x);
            wheel.makeVisible();
            x += WHEEL_SPACING;
        }
    }

    private boolean beginOperation(){
        ok = true;
        if(ended){
            fail("El simulador ya fue terminado.");
            return false;
        }
        return true;
    }

    private void fail(String message){
        ok = false;
        if(visible){
            JOptionPane.showMessageDialog(null, message, "Slot Machine",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private int clampForInsert(int pos, int currentSize){
        if(pos < 1){
            return 1;
        }
        if(pos > currentSize + 1){
            return currentSize + 1;
        }
        return pos;
    }

    private int clampForExisting(int pos, int currentSize){
        if(pos < 1){
            return 1;
        }
        if(pos > currentSize){
            return currentSize;
        }
        return pos;
    }
    
        // Extensiones Ciclo 2

    /**
     * Intercambia la posicion de dos ruedas de la maquina.
     */
    public void swap(int wheel1, int wheel2){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int pos1 = clampForExisting(wheel1, wheels.size());
        int pos2 = clampForExisting(wheel2, wheels.size());
        Wheel temp = wheels.get(pos1 - 1);
        wheels.set(pos1 - 1, wheels.get(pos2 - 1));
        wheels.set(pos2 - 1, temp);
        redraw();
    }

    /**
     * Fija la rueda indicada (ver Wheel.lock()).
     */
    public void lock(int wheel){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int validPos = clampForExisting(wheel, wheels.size());
        wheels.get(validPos - 1).lock();
        redraw();
    }

    /**
     * Libera la rueda indicada (ver Wheel.unlock()).
     */
    public void unlock(int wheel){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int validPos = clampForExisting(wheel, wheels.size());
        wheels.get(validPos - 1).unlock();
        redraw();
    }

    /**
     * Gira la rueda indicada x posiciones hacia adelante. Si la rueda
     * esta fija (lock) la operacion falla. Si la maquina es visible, el
     * movimiento se anima paso a paso.
     */
    public void spin(int wheel, int steps){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        int validPos = clampForExisting(wheel, wheels.size());
        Wheel target = wheels.get(validPos - 1);
        if(target.isLocked()){
            fail("La rueda " + validPos + " esta fija (lock); libere la rueda antes de girarla.");
            return;
        }
        if(!target.spin(steps)){
            fail("La rueda " + validPos + " no tiene simbolos para girar.");
            return;
        }
        redraw();
    }

    /**
     * Deja la maquina en la configuracion dada: setSymbols[i] sera el
     * simbolo visible de la rueda. El arreglo debe tener tantos
     * elementos como ruedas tenga la maquina, y cada color debe existir
     * ya en su rueda correspondiente porque no crea simbolos nuevos.
     */
    public void spin(String[] setSymbols){
        if(!beginOperation()){
            return;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return;
        }
        if(setSymbols == null || setSymbols.length != wheels.size()){
            fail("Se debe indicar un color por cada rueda de la maquina (" + wheels.size() + ").");
            return;
        }
        for(int i = 0; i < wheels.size(); i++){
            String key = setSymbols[i] == null ? null : setSymbols[i].trim().toLowerCase();
            if(!wheels.get(i).hasSymbol(key)){
                fail("El simbolo '" + setSymbols[i] + "' no existe en la rueda " + (i + 1) + ".");
                return;
            }
        }
        for(int i = 0; i < wheels.size(); i++){
            wheels.get(i).placeSymbol(setSymbols[i].trim().toLowerCase());
        }
        redraw();
    }

    // Extensiones Ciclo 3

    /**
     * Crea una maquina de n ruedas y n simbolos, inicializada aleatoriamente.
     * Ver {@link #SlotMachine(int, long)}.
     */
    public SlotMachine(int n){
        this(n, new Random().nextLong());
    }

    /**
     * Crea una maquina de n ruedas y n simbolos de colores distintos, inicializada
     * aleatoriamente a partir de la semilla dada 
     * Todas las ruedas tienen los mismos n simbolos en el mismo orden circular;
     * lo unico aleatorio es cual simbolo esta visible en cada rueda, con la
     * garantia de que la maquina inicial no es jackpot (si n >= 2).
     * Si n no esta entre 1 y la cantidad de tonos CSS disponibles, la maquina
     * queda vacia y ok() retorna false.
     */
    public SlotMachine(int n, long seed){
        this();
        random = new Random(seed);
        List<String> palette = CssColors.distinctNames();
        if(n < 1 || n > palette.size()){
            fail("La cantidad de ruedas y simbolos debe estar entre 1 y " + palette.size() + ".");
            return;
        }
        Collections.shuffle(palette, random);
        List<String> ring = palette.subList(0, n);
        int[] visibleAt = new int[n];
        boolean allEqual = true;
        for(int w = 0; w < n; w++){
            Wheel wheel = new Wheel();
            for(int s = 0; s < n; s++){
                wheel.addSymbol(s + 1, ring.get(s));
            }
            visibleAt[w] = random.nextInt(n);
            allEqual = allEqual && visibleAt[w] == visibleAt[0];
            wheels.add(wheel);
        }
        if(n > 1 && allEqual){
            visibleAt[0] = (visibleAt[0] + 1) % n;
        }
        for(int w = 0; w < n; w++){
            wheels.get(w).placeSymbol(ring.get(visibleAt[w]));
        }
        activeWheel = wheels.get(n - 1);
    }

    /**
     * Retorna cuantos colores distintos hay visibles en este momento, contando
     * un simbolo por rueda. Es la respuesta que da la maquina de la maraton
     * despues de cada accion. No confundir con distinctSymbols(), que cuenta los
     * colores de la rueda activa.
     */
    public int distinctVisible(){
        if(!beginOperation()){
            return 0;
        }
        if(wheels.isEmpty()){
            fail("No hay ruedas en la maquina.");
            return 0;
        }
        Set<String> colors = new HashSet<String>();
        for(String color : currentConfiguration()){
            if(!color.isEmpty()){
                colors.add(color);
            }
        }
        return colors.size();
    }
}