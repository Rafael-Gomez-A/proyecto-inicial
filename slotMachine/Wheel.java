import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

/**
 * Una rueda es una parte de una SlotMachine. Mantiene una secuencia
 * ordenada de símbolos la posición 1 es la primera y recuerda qué símbolo está
 * actualmente "visible" (el que se muestra a través de la ventana de la máquina).
 * También se dibuja a sí misma en el Canvas de formas compartidas,
 * reutilizando lo que teniamos desde shapes
 *
 * Las posiciones se numeran a partir de 1, siguiendo la convención de SlotMachine: la posición
 * 1 es el primer símbolo, y cualquier operación de Insert/Delete ajusta las posiciones
 * fuera de rango a la posición válida más cercana.
 *
 *@author Juan Camilo Rojas / Rafael Ricardo Gomez
 *@version 1.0 (Ciclo 1)
 */
public class Wheel{

    private static final int SYMBOL_DIAMETER = 22;
    private static final int ROW_HEIGHT = 28;
    private static final int TOP_MARGIN = 20;
    private static final int FRAME_WIDTH = 34;
    private static final int FRAME_PADDING = 12;
    private static final String FRAME_COLOR = "whitesmoke";
    private static final String FRAME_JACKPOT_COLOR = "gold";
    private static final String POINTER_COLOR = "black";

    private List<String> colors;
    private int visibleIndex;

    private Rectangle frame;
    private int frameX, frameY;
    private List<Circle> dots;
    private List<Integer> dotX, dotY;
    private Triangle pointer;
    private int pointerX, pointerY;
    private boolean highlighted;
    private boolean locked;

    /**
     * Crea una nueva rueda vacia (y libre, es decir, no fija).
     */
    public Wheel(){
        colors = new ArrayList<String>();
        visibleIndex = -1;
        frame = new Rectangle();
        frameX = 70;
        frameY = 15;
        dots = new ArrayList<Circle>();
        dotX = new ArrayList<Integer>();
        dotY = new ArrayList<Integer>();
        pointer = new Triangle();
        pointerX = 140;
        pointerY = 15;
        highlighted = false;
        locked = false;
    }

    /**
     * Inserta un simbolo en la posicion asignada, si es el primer simbolo de una rueda 
     * la hace visible.
     */
    public void addSymbol(int pos1based, String color){
        int idx = pos1based - 1;
        colors.add(idx, color);
        Circle dot = new Circle();
        dot.changeColor(color);
        dot.changeSize(SYMBOL_DIAMETER);
        dots.add(idx, dot);
        dotX.add(idx, 20);
        dotY.add(idx, 15);
        if(visibleIndex == -1){
            visibleIndex = 0;
        } else if(idx <= visibleIndex){
            visibleIndex++;
        }
    }

    /**
     * Remueve el primer simbolo acorde al color dado
     */
    public boolean removeSymbol(String color){
        int idx = (color == null) ? -1 : colors.indexOf(color);
        if(idx == -1){
            return false;
        }
        colors.remove(idx);
        dots.remove(idx).makeInvisible();
        dotX.remove(idx);
        dotY.remove(idx);
        if(colors.isEmpty()){
            visibleIndex = -1;
        } else if(idx < visibleIndex){
            visibleIndex--;
        } else if(idx == visibleIndex && visibleIndex >= colors.size()){
            visibleIndex = colors.size() - 1;
        }
        return true;
    }

    /**
     * Hace a un simbolo ya existente de la rueda visible
     */
    public boolean placeSymbol(String color){
        int idx = (color == null) ? -1 : colors.indexOf(color);
        if(idx == -1){
            return false;
        }
        visibleIndex = idx;
        return true;
    }

    /**
     * Gira la rueda, marcando aleatoreamente uno de los simbolos 
     * de la rueda. Una rueda fija (lock) no gira.
     * @return false si la rueda esta fija o no tiene simbolos.
     */
    public boolean spin(Random rng){
        if(locked || colors.isEmpty()){
            return false;
        }
        visibleIndex = rng.nextInt(colors.size());
        return true;
    }

    /**
     * Retorna los colores de los simbolos de la rueda en orden,
     * empezando en 1.
     */
    public String[] getSymbols(){
        return colors.toArray(new String[0]);
    }

    /**
     * Retorna cuantos simbolos de diferente color hay en la rueda.
     */
    public int distinctCount(){
        return new HashSet<String>(colors).size();
    }

    /**
     * Retorna el color del simbolo marcado, null si esta vacia.
     */
    public String getVisibleColor(){
        return visibleIndex == -1 ? null : colors.get(visibleIndex);
    }

    /**
     * Retorna la cantidad de simbolos en la rueda
     */
    public int size(){
        return colors.size();
    }

    /**
     * este metodo nos marca cuando hay jackpot y nos pinta las ruedas de un color dorado
     */
    public void setHighlighted(boolean highlighted){
        this.highlighted = highlighted;
    }

    /**
     * Recalcula la posición en pantalla de cada elemento visual de esta
     * rueda, situando su borde izquierdo en la coordenada indicada. es necesario
     * que valla seguido de makeVisible() para mostrar realmente el resultado.
     */
    public void reposition(int xLeft){
        int frameHeight = Math.max(ROW_HEIGHT, colors.size() * ROW_HEIGHT) + FRAME_PADDING;
        moveTo(frame, xLeft, TOP_MARGIN);
        frame.changeSize(frameHeight, FRAME_WIDTH);
        frame.changeColor(highlighted ? FRAME_JACKPOT_COLOR : FRAME_COLOR);
        for(int i = 0; i < dots.size(); i++){
            int targetX = xLeft + (FRAME_WIDTH - SYMBOL_DIAMETER) / 2;
            int targetY = TOP_MARGIN + (FRAME_PADDING / 2) + i * ROW_HEIGHT;
            moveDot(i, targetX, targetY);
        }
        if(visibleIndex != -1){
            int targetY = TOP_MARGIN + (FRAME_PADDING / 2) + visibleIndex * ROW_HEIGHT;
            moveTo(pointer, xLeft - 16, targetY - 4);
            pointer.changeSize(12, 12);
            pointer.changeColor(POINTER_COLOR);
        }
    }

    /**
     * Muestra la rueda dentro del Canvas.
     */
    public void makeVisible(){
        frame.makeVisible();
        for(Circle dot : dots){
            dot.makeVisible();
        }
        if(visibleIndex != -1){
            pointer.makeVisible();
        } else {
            pointer.makeInvisible();
        }
    }

    /**
     * Oculta toda la rueda dentro del Canvas.
     */
    public void makeInvisible(){
        frame.makeInvisible();
        for(Circle dot : dots){
            dot.makeInvisible();
        }
        pointer.makeInvisible();
    }

    private void moveTo(Rectangle shape, int targetX, int targetY){
        shape.moveHorizontal(targetX - frameX);
        shape.moveVertical(targetY - frameY);
        frameX = targetX;
        frameY = targetY;
    }

    private void moveTo(Triangle shape, int targetX, int targetY){
        shape.moveHorizontal(targetX - pointerX);
        shape.moveVertical(targetY - pointerY);
        pointerX = targetX;
        pointerY = targetY;
    }

    private void moveDot(int i, int targetX, int targetY){
        Circle dot = dots.get(i);
        dot.moveHorizontal(targetX - dotX.get(i));
        dot.moveVertical(targetY - dotY.get(i));
        dotX.set(i, targetX);
        dotY.set(i, targetY);
    }
    
        // Extensiones Ciclo 2

    /**
     * Fija (bloquea) la rueda. Mientras esté fija ningún spin la mueve:
     * Wheel.spin(...) retorna false y SlotMachine.spin(...) la omite o falla.
     */
    public void lock(){
        locked = true;
    }

    /**
     * Libera una rueda previamente fijada con lock().
     */
    public void unlock(){
        locked = false;
    }

    /**
     * Indica si la rueda está actualmente fija.
     */
    public boolean isLocked(){
        return locked;
    }

    /**
     * Indica si la rueda contiene al menos un simbolo del color dado.
     */
    public boolean hasSymbol(String color){
        return color != null && colors.contains(color);
    }

    /**
     * Gira la rueda x posiciones hacia adelante en sentido circular,
     * Si la rueda esta actualmente visible, cada paso intermedio se muestra 
     * (el puntero se desplaza simbolo a simbolo). Una rueda fija (lock) no gira.
     * @return false si la rueda esta fija o no tiene simbolos.
     */
    public boolean spin(int steps){
        if(locked || colors.isEmpty()){
            return false;
        }
        int size = colors.size();
        int normalizedSteps = ((steps % size) + size) % size;
        for(int i = 0; i < normalizedSteps; i++){
            visibleIndex = (visibleIndex + 1) % size;
            reposition(frameX);
        }
        return true;
    }
}